import Foundation

#if canImport(NaturalLanguage)
import NaturalLanguage
#endif

// MARK: - Apple Entity and Intent Types
enum AppleEntity {
    case academic
    case professional
    case social
    case maintenance
    case wellness
    case general
}

enum AppleIntent {
    case create
    case complete
    case schedule
    case general
}

struct AppleNLResult {
    let text: String
    let entity: AppleEntity
    let confidence: Double
    let intent: AppleIntent
}

// Natural Language Processing Helpers
extension AppleVoiceTaskManager {
    
    func determineEntityFromNLAnalysis(
        text: String,
        detectedEntities: [String],
        actionWords: [String]
    ) -> AppleEntity {
        let lowercaseText = text.lowercased()
        
        let academicKeywords = ["homework", "assignment", "study", "exam", "test", "quiz", "paper", "essay", "research", "thesis"]
        if academicKeywords.contains(where: lowercaseText.contains) {
            return .academic
        }
        
        let workKeywords = ["work", "meeting", "presentation", "project", "deadline", "office", "client", "proposal", "report"]
        if workKeywords.contains(where: lowercaseText.contains) {
            return .professional
        }
        
        let socialKeywords = ["call", "friend", "family", "party", "dinner", "birthday", "wedding", "visit"]
        if socialKeywords.contains(where: lowercaseText.contains) {
            return .social
        }
        
        let choresKeywords = ["buy", "shop", "grocery", "clean", "wash", "cook", "laundry", "bills", "repair"]
        if choresKeywords.contains(where: lowercaseText.contains) {
            return .maintenance
        }
        
        let healthKeywords = ["doctor", "exercise", "gym", "yoga", "meditation", "health", "appointment", "therapy"]
        if healthKeywords.contains(where: lowercaseText.contains) {
            return .wellness
        }
        
        return .general
    }
    
    func determineIntentFromNLAnalysis(actionWords: [String]) -> AppleIntent {
        let createVerbs = ["make", "create", "write", "prepare", "build"]
        let completeVerbs = ["finish", "complete", "submit", "send"]
        let scheduleVerbs = ["schedule", "book", "plan", "arrange"]
        
        if actionWords.contains(where: createVerbs.contains) {
            return .create
        } else if actionWords.contains(where: completeVerbs.contains) {
            return .complete
        } else if actionWords.contains(where: scheduleVerbs.contains) {
            return .schedule
        }
        
        return .general
    }
    
    func calculateNLConfidence(
        text: String,
        entity: AppleEntity,
        actionWords: [String]
    ) -> Double {
        var confidence = 0.5
        
        let keywordMatches = countKeywordMatches(text: text, entity: entity)
        confidence += Double(keywordMatches) * 0.1
        
        confidence += Double(actionWords.count) * 0.05
        
        if text.count > 20 {
            confidence += 0.1
        }
        
        return min(confidence, 0.95)
    }
    
    func countKeywordMatches(text: String, entity: AppleEntity) -> Int {
        let lowercaseText = text.lowercased()
        
        let keywords: [String]
        switch entity {
        case .academic:
            keywords = ["homework", "assignment", "study", "exam", "test"]
        case .professional:
            keywords = ["work", "meeting", "presentation", "project", "deadline"]
        case .social:
            keywords = ["call", "friend", "family", "party", "dinner"]
        case .maintenance:
            keywords = ["buy", "shop", "clean", "wash", "cook"]
        case .wellness:
            keywords = ["doctor", "exercise", "gym", "health", "appointment"]
        case .general:
            keywords = []
        }
        
        return keywords.filter { lowercaseText.contains($0) }.count
    }
    
    func extractDeadlineWithNL(from text: String) -> String? {
        let dateWords = ["today", "tomorrow", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"]
        let lowercaseText = text.lowercased()
        
        for dateWord in dateWords {
            if lowercaseText.contains(dateWord) {
                return dateWord.capitalized
            }
        }
        
        return nil
    }
    
    func determinePriorityWithNL(from text: String) -> String {
        let urgentKeywords = ["urgent", "asap", "important", "critical", "emergency", "immediately", "rush"]
        let lowPriorityKeywords = ["later", "sometime", "eventually", "when possible"]
        
        let lowercaseText = text.lowercased()
        
        if urgentKeywords.contains(where: lowercaseText.contains) {
            return "High"
        } else if lowPriorityKeywords.contains(where: lowercaseText.contains) {
            return "Low"
        }
        
        return "Medium"
    }
    
    func estimateTimeWithNL(from text: String) -> String? {
        let quickKeywords = ["quick", "quickly", "minute", "minutes", "brief"]
        let longKeywords = ["long", "lengthy", "hours", "project", "detailed"]
        
        let lowercaseText = text.lowercased()
        
        if quickKeywords.contains(where: lowercaseText.contains) {
            return "15-30min"
        } else if longKeywords.contains(where: lowercaseText.contains) {
            return "2+ hours"
        } else if lowercaseText.contains("hour") {
            return "1-2 hours"
        }
        
        return nil
    }
    
    func mapAppleEntityToCategory(_ entity: AppleEntity) -> TaskCategory {
        switch entity {
        case .academic: return .homework
        case .professional: return .work
        case .social: return .social
        case .maintenance: return .chores
        case .wellness: return .selfcare
        case .general: return .work
        }
    }
}
