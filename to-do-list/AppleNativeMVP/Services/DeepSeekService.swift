import Foundation

//DeepSeek API Service
class DeepSeekService: ObservableObject {
    static let shared = DeepSeekService()
    
    private let apiKey = "sk-5ad1aedf5fb84983bc9892e4ba035003"
    private let baseURL = "https://api.deepseek.com/v1/chat/completions"
    
    private init() {}
    
    func extractTasks(from text: String) async throws -> [TaskItem] {
          // 官方 tokenizer
          let exactUsage = TokenEstimator.shared.getExactChatTokens(
              systemPrompt: DeepSeekConfig.compactSystemPrompt,
              userPrompt: createTaskExtractionPrompt(text: text),
              expectedResponseLength: 150
          )
          
          print("Exact token usage: \(exactUsage.description)")
          
          // 检查是否会超出限制
          if DeepSeekConfig.dailyTokenUsage + exactUsage.total > DeepSeekConfig.dailyFreeLimit {
              print("Exact usage would exceed daily limit")
              throw DeepSeekError.dailyLimitReached
          }
          
          // 免费额度
          guard DeepSeekConfig.canUseDeepSeek else {
              print("DeepSeek daily limit reached, falling back to custom rules")
              throw DeepSeekError.dailyLimitReached
          }
          
          let prompt = createTaskExtractionPrompt(text: text)
          
          let request = DeepSeekRequest(
              model: DeepSeekConfig.freeModel,
              messages: [
                  DeepSeekMessage(role: "system", content: DeepSeekConfig.compactSystemPrompt),
                  DeepSeekMessage(role: "user", content: prompt)
              ],
              temperature: DeepSeekConfig.optimizedSettings.temperature,
              maxTokens: DeepSeekConfig.optimizedSettings.maxTokens,
              stream: DeepSeekConfig.optimizedSettings.stream
          )
          
          let response = try await sendRequest(request: request)
          
          // 更新实际 token 使用量
          let actualTokensUsed = response.usage.totalTokens
          DeepSeekConfig.dailyTokenUsage += actualTokensUsed
          
          let accuracyDiff = abs(exactUsage.total - actualTokensUsed)
          print("Used \(actualTokensUsed) tokens (calculated: \(exactUsage.total), diff: \(accuracyDiff))")
          print("Daily total: \(DeepSeekConfig.dailyTokenUsage)/\(DeepSeekConfig.dailyFreeLimit)")
          
          return try parseTasksFromResponse(response: response, originalText: text)
      }
      
      private func getSystemPrompt() -> String {
          return """
          You are an expert task extraction and categorization AI. Your job is to analyze spoken text and extract actionable tasks.
          
          Categories available:
          - homework: Academic work, assignments, studying
          - work: Professional tasks, meetings, projects  
          - social: Social activities, calls, meetings with friends/family
          - chores: Household tasks, shopping, cleaning, bills
          - selfcare: Health, exercise, medical appointments, personal care
          - urgent: Time-sensitive or high-priority tasks
          
          Priority levels: High, Medium, Low
          
          Response format should be valid JSON with this structure:
          {
            "tasks": [
              {
                "text": "Clean and descriptive task description",
                "category": "one of the categories above",
                "priority": "High/Medium/Low", 
                "estimated_time": "time estimate like '30min' or '2hrs' (optional)",
                "deadline": "deadline if mentioned like 'Today' or 'Friday' (optional)",
                "confidence": 0.85
              }
            ],
            "confidence": 0.90,
            "processing_notes": "Any relevant observations (optional)"
          }
          
          Guidelines:
          1. Extract multiple tasks if mentioned
          2. Be specific and actionable in task descriptions
          3. Consider context clues for categorization
          4. Assign realistic time estimates
          5. Higher confidence for clearer, more specific tasks
          - If unclear, default to 'work' category with medium priority
          """
      }
      
      private func createTaskExtractionPrompt(text: String) -> String {
          return "Extract tasks from: \"\(text)\""
      }
      
      private func sendRequest(request: DeepSeekRequest) async throws -> DeepSeekResponse {
          guard let url = URL(string: baseURL) else {
              throw DeepSeekError.invalidURL
          }
          
          var urlRequest = URLRequest(url: url)
          urlRequest.httpMethod = "POST"
          urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
          urlRequest.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
          
          let encoder = JSONEncoder()
          urlRequest.httpBody = try encoder.encode(request)
          
          let (data, response) = try await URLSession.shared.data(for: urlRequest)
          
          guard let httpResponse = response as? HTTPURLResponse else {
              throw DeepSeekError.invalidResponse
          }
          
          guard httpResponse.statusCode == 200 else {
              throw DeepSeekError.apiError(statusCode: httpResponse.statusCode)
          }
          
          let decoder = JSONDecoder()
          return try decoder.decode(DeepSeekResponse.self, from: data)
      }
      
      private func parseTasksFromResponse(response: DeepSeekResponse, originalText: String) throws -> [TaskItem] {
          guard let choice = response.choices.first else {
              throw DeepSeekError.noResponse
          }
          
          var content = choice.message.content
          print("Raw API response: \(content)")
          
          // 清理响应内容 - 移除 markdown 格式
          content = cleanResponseContent(content)
          
          // Parse JSON response
          guard let jsonData = content.data(using: .utf8) else {
              throw DeepSeekError.invalidJSON
          }
          
          let decoder = JSONDecoder()
          let analysis: DeepSeekTaskAnalysis
          
          do {
              analysis = try decoder.decode(DeepSeekTaskAnalysis.self, from: jsonData)
          } catch {
              // create a task if JSON parsing fails
              print("JSON parsing failed, creating fallback task: \(error)")
              print("Failed content: \(content)")
              return [createFallbackTask(text: originalText)]
          }
          
          var tasks: [TaskItem] = []
          
          for deepSeekTask in analysis.tasks {
              let category = mapStringToCategory(deepSeekTask.category)
              let taskInfo = TaskInfo(
                  deadline: deepSeekTask.deadline,
                  priority: deepSeekTask.priority,
                  estimatedTime: deepSeekTask.estimatedTime
              )
              
              let task = TaskItem(
                  text: deepSeekTask.text,
                  category: category,
                  confidence: deepSeekTask.confidence,
                  timestamp: Date(),
                  extractedInfo: taskInfo,
                  processingMethod: .deepSeek
              )
              
              tasks.append(task)
          }
          
          return tasks.isEmpty ? [createFallbackTask(text: originalText)] : tasks
      }
      
      private func cleanResponseContent(_ content: String) -> String {
          var cleaned = content
          
          //REMOVE markdown
          cleaned = cleaned.replacingOccurrences(of: "```json", with: "")
          cleaned = cleaned.replacingOccurrences(of: "```", with: "")
          
          //REMOVE blank
          cleaned = cleaned.trimmingCharacters(in: .whitespacesAndNewlines)
          
          //find json
          if !cleaned.hasPrefix("{") {
              if let jsonStart = cleaned.firstIndex(of: "{"),
                 let jsonEnd = cleaned.lastIndex(of: "}") {
                  let range = jsonStart...jsonEnd
                  cleaned = String(cleaned[range])
              }
          }
          
          print("Cleaned content: \(cleaned)")
          return cleaned
      }
      
      private func mapStringToCategory(_ categoryString: String) -> TaskCategory {
          switch categoryString.lowercased() {
          case "homework": return .homework
          case "work": return .work
          case "social": return .social
          case "chores": return .chores
          case "selfcare": return .selfcare
          case "urgent": return .urgent
          default: return .work
          }
      }
      
      private func createFallbackTask(text: String) -> TaskItem {
          return TaskItem(
              text: text,
              category: .work,
              confidence: 0.50,
              timestamp: Date(),
              extractedInfo: TaskInfo(deadline: nil, priority: "Medium", estimatedTime: nil),
              processingMethod: .deepSeek
          )
      }
  }

  //DeepSeek Errors
  enum DeepSeekError: Error, LocalizedError {
      case invalidURL
      case invalidResponse
      case apiError(statusCode: Int)
      case noResponse
      case invalidJSON
      case networkError
      case dailyLimitReached
      
      var errorDescription: String? {
          switch self {
          case .invalidURL:
              return "Invalid API URL"
          case .invalidResponse:
              return "Invalid response from server"
          case .apiError(let statusCode):
              return "API error with status code: \(statusCode)"
          case .noResponse:
              return "No response from DeepSeek API"
          case .invalidJSON:
              return "Invalid JSON response"
          case .networkError:
              return "Network connection error"
          case .dailyLimitReached:
              return "Daily free limit reached"
          }
      }
  }
