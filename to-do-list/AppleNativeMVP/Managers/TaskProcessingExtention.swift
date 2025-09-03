import Foundation

// Task Processing Extensions
extension AppleVoiceTaskManager {
    
    func extractTasksWithCustomRules(text: String) -> [TaskItem] {
        print("custom rules processing: \(text)")
        
        var tasks: [TaskItem] = []
        
        let taskSegments = extractMultipleTasks(from: text)
        print("Multi tasks segmentation result: \(taskSegments)")
        //seperate into valid useful sentences
        let sentences = text.components(separatedBy: CharacterSet(charactersIn: ".!?")).filter {
            !$0.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
        }
        print("Sentence splitting result: \(sentences)")
        //decide split method
        let segmentsToProcess: [String]
        if taskSegments.count > 1 {
            segmentsToProcess = taskSegments
            print("multi-task segmentation, found \(taskSegments.count) tasks")
        } else if sentences.count > 1 {
            segmentsToProcess = sentences
            print("sentence splitting, found \(sentences.count) sentences")
        } else {
            segmentsToProcess = [text]
            print("original text")
        }
        //classify all segments
        for (index, segment) in segmentsToProcess.enumerated() {
            let cleanSegment = segment.trimmingCharacters(in: .whitespacesAndNewlines)
            //segement < 3char-->skip
            if cleanSegment.count < 3 { continue }
            
            print("Processing segment \(index+1): \(cleanSegment)")
            
            let (category, confidence, taskInfo) = classifyWithFoundationModel(text: cleanSegment)
            
            tasks.append(TaskItem(
                text: cleanSegment,
                category: category,
                confidence: confidence,
                timestamp: Date(),
                extractedInfo: taskInfo,
                processingMethod: .customRules
            ))
            
            print("Custom rules classification result: \(category.displayName), confidence: \(Int(confidence*100))%")
        }
        
        let finalTasks = tasks.isEmpty ? [createDefaultTask(text: text, method: .customRules)] : tasks
        print("Custom rules created \(finalTasks.count) tasks")
        
        return finalTasks
    }
    // no task created, return default task- set to work 50% medium
    func createDefaultTask(text: String, method: ProcessingMethod) -> TaskItem {
        return TaskItem(
            text: text,
            category: .work,
            confidence: 0.50,
            timestamp: Date(),
            extractedInfo: TaskInfo(deadline: nil, priority: "Medium", estimatedTime: nil),
            processingMethod: method
        )
    }
}
