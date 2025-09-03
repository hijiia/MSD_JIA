import SwiftUI
//Usage Statistics View

struct UsageStatsView: View {
    @ObservedObject var voiceManager: AppleVoiceTaskManager
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                
                // API Usage Section
                VStack(alignment: .leading, spacing: 12) {
                    Text("DeepSeek API Usage")
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    VStack(spacing: 8) {
                        HStack {
                            Text("Today:")
                                .foregroundColor(.secondary)
                            Spacer()
                            Text("\(DeepSeekConfig.dailyTokenUsage) / \(DeepSeekConfig.dailyFreeLimit) tokens")
                                .font(.system(.body, design: .monospaced))
                                .foregroundColor(tokenUsageColor)
                        }
                        
                        ProgressView(value: Double(DeepSeekConfig.dailyTokenUsage),
                                   total: Double(DeepSeekConfig.dailyFreeLimit))
                            .tint(tokenUsageColor)
                        
                        HStack {
                            Text("Status:")
                                .foregroundColor(.secondary)
                            Spacer()
                            Text(DeepSeekConfig.canUseDeepSeek ? "Available" : "Limit Reached")
                                .foregroundColor(DeepSeekConfig.canUseDeepSeek ? .green : .red)
                                .fontWeight(.medium)
                        }
                    }
                    .padding()
                    .background(Color.gray.opacity(0.1))
                    .cornerRadius(12)
                }
                
                // Processing Method Distribution
                VStack(alignment: .leading, spacing: 12) {
                    Text("Processing Method Usage")
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    VStack(spacing: 8) {
                        ForEach(calculatedMethodStats, id: \.method) { stat in
                            HStack {
                                Circle()
                                    .fill(methodColor(stat.method))
                                    .frame(width: 12, height: 12)
                                
                                Text(stat.method.rawValue)
                                    .foregroundColor(.primary)
                                
                                Spacer()
                                
                                Text("\(stat.count)")
                                    .font(.system(.body, design: .monospaced))
                                    .foregroundColor(.secondary)
                            }
                            .padding(.vertical, 4)
                        }
                    }
                    .padding()
                    .background(Color.gray.opacity(0.1))
                    .cornerRadius(12)
                }
                
                // Recommendations
                VStack(alignment: .leading, spacing: 12) {
                    Text("Recommendations")
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    VStack(alignment: .leading, spacing: 8) {
                        if isCloseToLimit {
                            RecommendationRow(
                                icon: "exclamationmark.triangle.fill",
                                color: .orange,
                                text: "Close to daily limit. Consider using 'Intelligent' mode."
                            )
                        }
                        
                        if intelligentUsageCount < 3 {
                            RecommendationRow(
                                icon: "brain.head.profile",
                                color: .blue,
                                text: "Try 'Intelligent' mode for optimal cost-efficiency."
                            )
                        }
                        
                        RecommendationRow(
                            icon: "info.circle.fill",
                            color: .green,
                            text: "Intelligent mode uses AI only when needed, saving tokens."
                        )
                    }
                    .padding()
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(12)
                }
                
                Spacer()
            }
            .padding()
            .navigationTitle("Usage Statistics")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(
                trailing: Button("Done") {
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
    }
    
    private var tokenUsageColor: Color {
        let percentage = Double(DeepSeekConfig.dailyTokenUsage) / Double(DeepSeekConfig.dailyFreeLimit)
        if percentage > 0.8 {
            return .red
        } else if percentage > 0.6 {
            return .orange
        } else {
            return .green
        }
    }
    
    private var calculatedMethodStats: [MethodStat] {
        let methods = ProcessingMethod.allCases
        let stats = methods.compactMap { method in
            let count = voiceManager.extractedTasks.filter { $0.processingMethod == method }.count
            return count > 0 ? MethodStat(method: method, count: count) : nil
        }
        return stats.sorted { $0.count > $1.count }
    }
    
    private var isCloseToLimit: Bool {
        return DeepSeekConfig.dailyTokenUsage > (DeepSeekConfig.dailyFreeLimit * 8 / 10)
    }
    
    private var intelligentUsageCount: Int {
        return calculatedMethodStats.first { $0.method == .intelligent }?.count ?? 0
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
}

struct MethodStat {
    let method: ProcessingMethod
    let count: Int
}

struct RecommendationRow: View {
    let icon: String
    let color: Color
    let text: String
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .foregroundColor(color)
                .frame(width: 20)
            
            Text(text)
                .font(.caption)
                .foregroundColor(.primary)
            
            Spacer()
        }
    }
}
