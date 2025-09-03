import Foundation

//Task Processing Helper Methods
extension AppleVoiceTaskManager {
    
    func extractMultipleTasks(from text: String) -> [String] {
        let lowercaseText = text.lowercased()
        var segments: [String] = []
        
        print("Attempting multi-task segmentation: \(text)")
        
        let separators = [
            " and then ", " and also ", " and ", " also ", " plus ",
            " then ", " after that ", " next ", " secondly ", " finally ",
            " i need to ", " i have to ", " i should ", " i must ",
            " don't forget to ", " remember to ", " let me ", " i want to "
        ]
        
        var foundSeparation = false
        
        for separator in separators {
            if lowercaseText.contains(separator) {
                print("Found separator: '\(separator)'")
                let parts = text.components(separatedBy: separator)
                if parts.count > 1 {
                    foundSeparation = true
                    print("Split into \(parts.count) parts: \(parts)")
                    
                    if !parts[0].trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                        segments.append(parts[0].trimmingCharacters(in: .whitespacesAndNewlines))
                    }
                    
                    for i in 1..<parts.count {
                        let part = parts[i].trimmingCharacters(in: .whitespacesAndNewlines)
                        if !part.isEmpty {
                            let contextualPart = addContextIfNeeded(part: part, originalSeparator: separator)
                            segments.append(contextualPart)
                        }
                    }
                    break
                }
            }
        }
        
        if !foundSeparation {
            print("Attempting verb splitting")
            segments = splitByActionVerbs(text: text)
        }
        
        print("Multi-task segmentation final result: \(segments)")
        return segments
    }
    
    func addContextIfNeeded(part: String, originalSeparator: String) -> String {
        let lowercasePart = part.lowercased()
        
        let hasSubject = lowercasePart.hasPrefix("i ") ||
                        lowercasePart.hasPrefix("we ") ||
                        lowercasePart.hasPrefix("you ") ||
                        lowercasePart.contains(" i ") ||
                        lowercasePart.hasPrefix("need to") ||
                        lowercasePart.hasPrefix("have to") ||
                        lowercasePart.hasPrefix("should") ||
                        lowercasePart.hasPrefix("must")
        
        if !hasSubject && !originalSeparator.contains("i ") {
            return "I need to " + part
        }
        
        return part
    }
    
    func splitByActionVerbs(text: String) -> [String] {
        let actionVerbs = [
            "do ", "make ", "buy ", "call ", "write ", "send ", "finish ",
            "complete ", "study ", "read ", "clean ", "wash ", "cook ",
            "schedule ", "book ", "pay ", "submit ", "prepare ", "organize "
        ]
        
        var segments: [String] = []
        let words = text.components(separatedBy: " ")
        var currentSegment: [String] = []
        
        for (index, word) in words.enumerated() {
            currentSegment.append(word)
            
            if index < words.count - 1 {
                let nextWord = words[index + 1].lowercased()
                
                for verb in actionVerbs {
                    if nextWord.hasPrefix(verb.trimmingCharacters(in: .whitespacesAndNewlines)) {
                        let segment = currentSegment.joined(separator: " ")
                        if !segment.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                            segments.append(segment)
                        }
                        currentSegment = []
                        break
                    }
                }
            }
        }
        
        if !currentSegment.isEmpty {
            let segment = currentSegment.joined(separator: " ")
            if !segment.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                segments.append(segment)
            }
        }
        
        return segments.count > 1 ? segments : []
    }
    
    func intelligentSentenceSplitting(text: String) -> [String] {
        let conjunctions = [
            " and then ", " and also ", " and ", " also ", " plus ",
            " then ", " after that ", " next ", " secondly ", " finally ",
            " i need to ", " i have to ", " i should ", " i must "
        ]
        
        for conjunction in conjunctions {
            if text.lowercased().contains(conjunction) {
                let parts = text.components(separatedBy: conjunction)
                if parts.count > 1 {
                    var segments: [String] = []
                    
                    if !parts[0].trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                        segments.append(parts[0].trimmingCharacters(in: .whitespacesAndNewlines))
                    }
                    
                    for i in 1..<parts.count {
                        let part = parts[i].trimmingCharacters(in: .whitespacesAndNewlines)
                        if !part.isEmpty {
                            let contextualPart = part.lowercased().hasPrefix("i ") ? part : "I need to " + part
                            segments.append(contextualPart)
                        }
                    }
                    
                    print("splitting result: \(segments)")
                    return segments
                }
            }
        }
        
        return [text]
    }
    
    func classifyWithFoundationModel(text: String) -> (TaskCategory, Double, TaskInfo) {
        let lowercaseText = text.lowercased()
        
        let classificationRules: [(keywords: [String], category: TaskCategory, baseConfidence: Double)] = [
            (["homework", "assignment", "study", "essay", "exam", "test", "quiz", "paper"], .homework, 0.94),
            (["work", "meeting", "presentation", "project", "deadline", "office", "client"], .work, 0.91),
            (["friend", "party", "dinner", "call", "social", "birthday", "family"], .social, 0.89),
            (["clean", "laundry", "grocery", "shopping", "chores", "bills"], .chores, 0.87),
            (["exercise", "doctor", "health", "meditation", "gym", "yoga"], .selfcare, 0.90),
            (["urgent", "asap", "emergency", "important", "critical", "rush"], .urgent, 0.96)
        ]
        
        var bestMatch: (TaskCategory, Double) = (.work, 0.60)
        
        for rule in classificationRules {
            let matches = rule.keywords.filter { lowercaseText.contains($0) }
            if !matches.isEmpty {
                let confidence = min(rule.baseConfidence + Double(matches.count - 1) * 0.01, 0.98)
                if confidence > bestMatch.1 {
                    bestMatch = (rule.category, confidence)
                }
            }
        }
        
        let taskInfo = extractTaskInfo(from: lowercaseText)
        return (bestMatch.0, bestMatch.1, taskInfo)
    }
    
    func extractTaskInfo(from text: String) -> TaskInfo {
        var deadline: String? = nil
        let deadlinePatterns = ["tomorrow", "today", "next week", "friday", "monday", "tuesday", "wednesday", "thursday", "saturday", "sunday"]
        for pattern in deadlinePatterns {
            if text.contains(pattern) {
                deadline = pattern.capitalized
                break
            }
        }
        
        let priority: String
        if text.contains("urgent") || text.contains("important") || text.contains("asap") {
            priority = "High"
        } else if text.contains("later") || text.contains("sometime") {
            priority = "Low"
        } else {
            priority = "Medium"
        }
        
        var estimatedTime: String? = nil
        if text.contains("quick") || text.contains("minute") {
            estimatedTime = "30min"
        } else if text.contains("hour") {
            estimatedTime = "2hrs"
        } else if text.contains("long") || text.contains("project") {
            estimatedTime = "4hrs+"
        }
        
        return TaskInfo(deadline: deadline, priority: priority, estimatedTime: estimatedTime)
    }
}
