import SwiftUI
import Foundation

// Data Models
enum TaskCategory: String, CaseIterable {
    case homework = "homework"
    case work = "work"
    case social = "social"
    case chores = "chores"
    case selfcare = "selfcare"
    case urgent = "urgent"
    
    var displayName: String {
        switch self {
        case .homework: return "Academic"
        case .work: return "Professional"
        case .social: return "Social"
        case .chores: return "Personal"
        case .selfcare: return "Wellness"
        case .urgent: return "Priority"
        }
    }
    
    var color: Color {
        switch self {
        case .homework: return .blue
        case .work: return .indigo
        case .social: return .pink
        case .chores: return .orange
        case .selfcare: return .green
        case .urgent: return .red
        }
    }
}

struct TaskItem: Identifiable {
    let id = UUID()
    let text: String
    let category: TaskCategory
    let confidence: Double
    let timestamp: Date
    let extractedInfo: TaskInfo?
    let processingMethod: ProcessingMethod
}

enum ProcessingMethod: String, CaseIterable {
    case customRules = "Custom Rules"
    case deepSeek = "DeepSeek AI"
    case hybrid = "Hybrid"
    case intelligentLocal = "Smart Local"
    case intelligentAI = "Smart AI"
    case intelligent = "Intelligent"
}

struct TaskInfo {
    let deadline: String?
    let priority: String
    let estimatedTime: String?
}

struct TranscriptionRecord: Identifiable, Codable {
    let id = UUID()
    let originalText: String
    let timestamp: Date
    let tasksCount: Int
    
    enum CodingKeys: String, CodingKey {
        case originalText, timestamp, tasksCount
    }
}
