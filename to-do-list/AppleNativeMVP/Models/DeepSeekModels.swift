import Foundation

//DeepSeek API Models
struct DeepSeekRequest: Codable {
    let model: String
    let messages: [DeepSeekMessage]
    let temperature: Double
    let maxTokens: Int
    let stream: Bool
    
    enum CodingKeys: String, CodingKey {
        case model, messages, temperature, stream
        case maxTokens = "max_tokens"
    }
}

struct DeepSeekMessage: Codable {
    let role: String
    let content: String
}

struct DeepSeekResponse: Codable {
    let id: String
    let object: String
    let created: Int
    let model: String
    let choices: [DeepSeekChoice]
    let usage: DeepSeekUsage
}

struct DeepSeekChoice: Codable {
    let index: Int
    let message: DeepSeekMessage
    let finishReason: String?
    
    enum CodingKeys: String, CodingKey {
        case index, message
        case finishReason = "finish_reason"
    }
}

struct DeepSeekUsage: Codable {
    let promptTokens: Int
    let completionTokens: Int
    let totalTokens: Int
    
    enum CodingKeys: String, CodingKey {
        case promptTokens = "prompt_tokens"
        case completionTokens = "completion_tokens"
        case totalTokens = "total_tokens"
    }
}

// Task Analysis Result from DeepSeek
struct DeepSeekTaskAnalysis: Codable {
    let tasks: [DeepSeekTask]
    let confidence: Double
    let processingNotes: String?
    
    enum CodingKeys: String, CodingKey {
        case tasks, confidence
        case processingNotes = "processing_notes"
    }
}

struct DeepSeekTask: Codable {
    let text: String
    let category: String
    let priority: String
    let estimatedTime: String?
    let deadline: String?
    let confidence: Double
    
    enum CodingKeys: String, CodingKey {
        case text, category, priority, confidence
        case estimatedTime = "estimated_time"
        case deadline
    }
}
