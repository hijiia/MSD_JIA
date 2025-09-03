import Foundation

class IntelligentTaskProcessor {
    //单例
    static let shared = IntelligentTaskProcessor()
    
    //init
    private init() {}
    
    //
    func processText(_ text: String, voiceManager: AppleVoiceTaskManager) async -> [TaskItem] {
        print("Start pocessing text: \(text)")
        
        //test complexity
        let complexity = analyzeTextComplexity(text)
        print("text complexity: \(complexity.level) (scores: \(complexity.score))")
        
        //local rules
        let localTasks = voiceManager.extractTasksWithCustomRules(text: text)
        let localConfidence = calculateLocalConfidence(tasks: localTasks, originalText: text)
        
        print("local pocsession result: \(localTasks.count) tasks, accuracy: \(Int(localConfidence * 100))%")
        //decide use ai or not
        let needsAI = shouldUseAI(
            complexity: complexity,
            localTasks: localTasks,
            localConfidence: localConfidence
        )
        
        if needsAI && DeepSeekConfig.canUseDeepSeek {
            print("using ai")
            
            do {
                //api
                let aiTasks = try await DeepSeekService.shared.extractTasks(from: text)
                let aiConfidence = calculateAIConfidence(tasks: aiTasks)
                
                print("ai pocsession result: \(aiTasks.count) tasks, accuracy: \(Int(aiConfidence * 100))%")
                
                //choose better one
                return chooseBestResult(
                    localTasks: localTasks,
                    localConfidence: localConfidence,
                    aiTasks: aiTasks,
                    aiConfidence: aiConfidence,
                    originalText: text
                )
            } catch {
                // AI fail
                print("AI faiked: \(error), use local")
                return enhanceLocalTasks(localTasks)
            }
        } else {
            // local
            let reason = needsAI ? "no need to use ai" : "using local result"
            print("local result: \(reason) (\(reason))")
            return enhanceLocalTasks(localTasks)
        }
    }
    
    //how complex the text is
    private func analyzeTextComplexity(_ text: String) -> TextComplexity {
        var score: Double = 0  // Complexity score: higher means more complex
        let lowercaseText = text.lowercased()
        
        // Text length affects complexity
        if text.count > 100 {
            score += 2.0  // extra long text
        } else if text.count > 50 {
            score += 1.0  // medium length
        }
        
        //words connect multiple tasks
        let taskSeparators = [" and ", " then ", " also ", " plus ", " after that ", " next "]
        let separatorCount = taskSeparators.filter { lowercaseText.contains($0) }.count
        score += Double(separatorCount) * 1.5  // Connecting words usually mean multiple tasks
        
        //time expressions
        let complexTimePatterns = [
            "next week", "this weekend", "by friday", "before monday",
            "in two hours", "within", "deadline", "due date"
        ]
        let timeComplexity = complexTimePatterns.filter { lowercaseText.contains($0) }.count
        score += Double(timeComplexity) * 1.0  // More complex time = higher score
        
        //Vague or uncertain language
        let ambiguousWords = [
            "maybe", "probably", "might", "could", "should probably",
            "if possible", "when i have time", "eventually", "sometime"
        ]
        let ambiguityCount = ambiguousWords.filter { lowercaseText.contains($0) }.count
        score += Double(ambiguityCount) * 2.0
        
        //need context to understand
        let contextualWords = [
            "that thing", "this", "it", "they", "those",
            "the project", "the meeting", "the assignment"
        ]
        let contextCount = contextualWords.filter { lowercaseText.contains($0) }.count
        score += Double(contextCount) * 1.5
        
        //conflicts (saying something is both urgent and not urgent)
        let priorityWords = ["urgent", "important", "asap", "critical", "rush"]
        let lowPriorityWords = ["later", "sometime", "eventually", "when possible"]
        if priorityWords.contains(where: lowercaseText.contains) &&
           lowPriorityWords.contains(where: lowercaseText.contains) {
            score += 3.0
        }
        
        // Turn score into complexity level
        let level: ComplexityLevel
        if score >= 6.0 {
            level = .high      // High complexity
        } else if score >= 3.0 {
            level = .medium    // Medium complexity
        } else {
            level = .low       // Low complexity
        }
        
        return TextComplexity(level: level, score: score)
    }
    
    // Decide if we should use AI
    private func shouldUseAI(
        complexity: TextComplexity,
        localTasks: [TaskItem],
        localConfidence: Double
    ) -> Bool {
        //really complex text - use AI
        if complexity.level == .high {
            return true
        }
        
        // Medium complexity but local processing didn't do well
        if complexity.level == .medium && localConfidence < 0.7 {
            return true
        }
        
        // Local processing found nothing or the tasks are bad
        if localTasks.isEmpty || localTasks.allSatisfy({ $0.confidence < 0.6 }) {
            return true
        }
        
        // Local only found 1 task but text seems like it might have more
        if localTasks.count == 1 && complexity.score > 2.0 {
            return true
        }
        
        //Otherwise don't use AI
        return false
    }
    
    // Calculate how confident we are in local processing
    private func calculateLocalConfidence(tasks: [TaskItem], originalText: String) -> Double {
        // No tasks found = 0 confidence
        guard !tasks.isEmpty else { return 0.0 }
        
        // Average confidence of all tasks
        let avgTaskConfidence = tasks.map(\.confidence).reduce(0, +) / Double(tasks.count)
        
        // Things that make confidence lower
        var penalty = 0.0
        for task in tasks {
            if task.text.count < 10 {
                penalty += 0.1  // Task description too short
            }
            if task.text.lowercased().contains("i need to") && task.text.count < 20 {
                penalty += 0.1  // Generic descriptions
            }
        }
        
        // Things that make confidence higher
        var bonus = 0.0
        if tasks.count > 1 && originalText.count > 50 {
            bonus += 0.1  // Found multiple tasks from complex text
        }
        
        // Final score between 0 and 1
        return max(0.0, min(1.0, avgTaskConfidence - penalty + bonus))
    }
    
    // Calculate AI confidence (simpler than local)
    private func calculateAIConfidence(tasks: [TaskItem]) -> Double {
        // No tasks = 0 confidence
        guard !tasks.isEmpty else { return 0.0 }
        // AI confidence: the average
        return tasks.map(\.confidence).reduce(0, +) / Double(tasks.count)
    }
    
    private func chooseBestResult(
        localTasks: [TaskItem],
        localConfidence: Double,
        aiTasks: [TaskItem],
        aiConfidence: Double,
        originalText: String
    ) -> [TaskItem] {
        
        // AI found more tasks and has good confidence
        if aiTasks.count > localTasks.count && aiConfidence > 0.7 {
            print("Choosing AI result: found more tasks with good confidence")
            return markAsIntelligentlyProcessed(aiTasks)
        }
        if aiConfidence > localConfidence + 0.2 && aiConfidence > 0.6 {
            print("Choosing AI result: much better confidence")
            return markAsIntelligentlyProcessed(aiTasks)
        }
        
        // Local processing is good enough
        if localConfidence > 0.7 {
            print("Choosing local result: local processing is good")
            return enhanceLocalTasks(localTasks)
        }
        
        // Text is complex and AI has results
        if originalText.count > 80 && !aiTasks.isEmpty {
            print("Choosing AI result: text too complex")
            return markAsIntelligentlyProcessed(aiTasks)
        }
        
        // Default to local
        print("Choosing local result: AI doesn't have clear advantage")
        return enhanceLocalTasks(localTasks)
    }
    
    // Mark tasks as processed locally
    private func enhanceLocalTasks(_ tasks: [TaskItem]) -> [TaskItem] {
        return tasks.map { task in
            TaskItem(
                text: task.text,
                category: task.category,
                confidence: task.confidence,
                timestamp: task.timestamp,
                extractedInfo: task.extractedInfo,
                processingMethod: .intelligentLocal  // Mark as smart local processing
            )
        }
    }
    
    // Mark tasks as processed by AI
    private func markAsIntelligentlyProcessed(_ tasks: [TaskItem]) -> [TaskItem] {
        return tasks.map { task in
            TaskItem(
                text: task.text,
                category: task.category,
                confidence: task.confidence,
                timestamp: task.timestamp,
                extractedInfo: task.extractedInfo,
                processingMethod: .intelligentAI  // Mark as AI smart processing
            )
        }
    }
}

//structure for text complexity
struct TextComplexity {
    let level: ComplexityLevel  // Complexity level
    let score: Double          // Actual score
}

// Complexity levels
enum ComplexityLevel {
    case low     // Low
    case medium  // Medium
    case high    // High
    
    var description: String {
        switch self {
        case .low: return "Low"
        case .medium: return "Medium"
        case .high: return "High"
        }
    }
}
