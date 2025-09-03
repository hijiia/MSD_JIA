import SwiftUI

//Task Row View
struct TaskRowView: View {
    let task: TaskItem
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            
            HStack {
                Text(task.category.displayName)
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(task.category.color)
                
                Spacer()
                
                Text(task.processingMethod.rawValue)
                    .font(.caption2)
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(methodColor(task.processingMethod).opacity(0.2))
                    .foregroundColor(methodColor(task.processingMethod))
                    .cornerRadius(4)
                
                Text("\(Int(task.confidence * 100))%")
                    .font(.caption)
                    .fontWeight(.medium)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 2)
                    .background(confidenceColor(task.confidence).opacity(0.2))
                    .foregroundColor(confidenceColor(task.confidence))
                    .cornerRadius(6)
            }
            
            Text(task.text)
                .font(.body)
                .lineLimit(3)
                .foregroundColor(.primary)
            
            if let info = task.extractedInfo {
                HStack(spacing: 16) {
                    if let deadline = info.deadline {
                        Label(deadline, systemImage: "calendar")
                            .font(.caption)
                            .foregroundColor(.orange)
                    }
                    
                    Label(info.priority, systemImage: "flag.fill")
                        .font(.caption)
                        .foregroundColor(priorityColor(info.priority))
                    
                    if let time = info.estimatedTime {
                        Label(time, systemImage: "clock")
                            .font(.caption)
                            .foregroundColor(.blue)
                    }
                    
                    Spacer()
                    
                    Text(task.timestamp, style: .time)
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding(16)
        .background(Color.white)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(task.category.color.opacity(0.2), lineWidth: 1)
        )
        .shadow(color: .black.opacity(0.04), radius: 8, x: 0, y: 2)
    }
    
    private func methodColor(_ method: ProcessingMethod) -> Color {
        switch method {
        case .customRules: return .blue
        case .deepSeek: return .green
        case .hybrid: return .purple
        case .intelligent: return .indigo
        case .intelligentLocal: return .cyan
        case .intelligentAI: return .mint
        }
    }
    
    private func confidenceColor(_ confidence: Double) -> Color {
        if confidence > 0.8 { return .green }
        else if confidence > 0.6 { return .orange }
        else { return .red }
    }
    
    private func priorityColor(_ priority: String) -> Color {
        switch priority.lowercased() {
        case "high": return .red
        case "medium": return .orange
        case "low": return .green
        default: return .gray
        }
    }
}
