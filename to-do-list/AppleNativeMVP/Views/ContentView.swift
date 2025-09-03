import SwiftUI

struct ContentView: View {
    @StateObject private var voiceManager = AppleVoiceTaskManager()
    @State private var showingHistory = false
    @State private var showingStats = false
    
    var body: some View {
        NavigationView {
            VStack(spacing: 32) {
                
                // Header Section
                HStack {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Voice Task Manager")
                            .font(.system(size: 28, weight: .light, design: .default))
                            .foregroundColor(.primary)
                        
                        Text(voiceManager.status)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 6)
                            .background(Color.gray.opacity(0.1))
                            .cornerRadius(20)
                    }
                    
                    Spacer()
                    
                    VStack(spacing: 8) {
                        Button(action: {
                            showingHistory = true
                        }) {
                            Image(systemName: "clock.arrow.circlepath")
                                .font(.system(size: 20))
                                .foregroundColor(.blue)
                        }
                        
                        Button(action: {
                            showingStats = true
                        }) {
                            Image(systemName: "chart.bar.fill")
                                .font(.system(size: 16))
                                .foregroundColor(.orange)
                        }
                        
                        Menu {
                            Button("Intelligent (Recommended)") {
                                voiceManager.processingMethod = .intelligent
                            }
                            Button("Custom Rules Only") {
                                voiceManager.processingMethod = .customRules
                            }
                            Button("DeepSeek AI Only") {
                                voiceManager.processingMethod = .deepSeek
                            }
                            Button("Hybrid Method") {
                                voiceManager.processingMethod = .hybrid
                            }
                        } label: {
                            VStack(spacing: 2) {
                                Image(systemName: "gearshape.fill")
                                    .font(.system(size: 16))
                                    .foregroundColor(.orange)
                                Text(voiceManager.processingMethod.rawValue)
                                    .font(.caption2)
                                    .foregroundColor(.orange)
                            }
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
                
                // Recording Button Section
                VStack(spacing: 24) {
                    Button(action: {
                        if voiceManager.isRecording {
                            voiceManager.stopRecording()
                        } else {
                            voiceManager.startRecording()
                        }
                    }) {
                        ZStack {
                            Circle()
                                .fill(voiceManager.isRecording ? Color.red : Color.blue)
                                .frame(width: 80, height: 80)
                                .shadow(color: voiceManager.isRecording ? .red.opacity(0.3) : .blue.opacity(0.3), radius: 10)
                            
                            Image(systemName: voiceManager.isRecording ? "stop.fill" : "mic.fill")
                                .font(.system(size: 24, weight: .medium))
                                .foregroundColor(.white)
                        }
                    }
                    .scaleEffect(voiceManager.isRecording ? 1.05 : 1.0)
                    .animation(.easeInOut(duration: 0.15), value: voiceManager.isRecording)
                    .disabled(voiceManager.isProcessing)
                    
                    if voiceManager.isProcessing {
                        HStack(spacing: 8) {
                            ProgressView()
                                .scaleEffect(0.8)
                            Text("Processing")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                
                //Recognized Text Section
                if !voiceManager.recognizedText.isEmpty {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Recognized Speech")
                            .font(.headline)
                            .foregroundColor(.secondary)
                        
                        Text(voiceManager.recognizedText)
                            .font(.body)
                            .padding(16)
                            .background(Color.gray.opacity(0.05))
                            .cornerRadius(12)
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                            )
                    }
                    .padding(.horizontal, 20)
                }
                
                // Extracted Tasks Section
                if !voiceManager.extractedTasks.isEmpty {
                    VStack(spacing: 16) {
                        HStack {
                            Text("Extracted Tasks")
                                .font(.headline)
                                .foregroundColor(.secondary)
                            
                            Spacer()
                            
                            Button("Clear") {
                                voiceManager.clearTasks()
                            }
                            .font(.caption)
                            .foregroundColor(.red)
                        }
                        .padding(.horizontal, 20)
                        
                        ScrollView {
                            LazyVStack(spacing: 12) {
                                ForEach(voiceManager.extractedTasks) { task in
                                    TaskRowView(task: task)
                                        .padding(.horizontal, 20)
                                }
                            }
                        }
                    }
                }
                
                Spacer()
            }
            .navigationBarHidden(true)
            .sheet(isPresented: $showingHistory) {
                TranscriptionHistoryView(voiceManager: voiceManager)
            }
            .sheet(isPresented: $showingStats) {
                UsageStatsView(voiceManager: voiceManager)
            }
        }
    }
}

//Preview
#Preview {
    ContentView()
}
