import SwiftUI
struct TranscriptionHistoryView: View {
    @ObservedObject var voiceManager: AppleVoiceTaskManager
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            VStack {
                if voiceManager.transcriptionHistory.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "text.bubble")
                            .font(.system(size: 48))
                            .foregroundColor(.gray)
                        
                        Text("No Transcription History")
                            .font(.headline)
                            .foregroundColor(.secondary)
                        
                        Text("Your voice transcriptions will appear here")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    List {
                        ForEach(voiceManager.transcriptionHistory) { record in
                            TranscriptionHistoryRow(record: record)
                        }
                    }
                    .listStyle(PlainListStyle())
                }
            }
            .navigationTitle("Transcription History")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(
                leading: Button("Close") {
                    presentationMode.wrappedValue.dismiss()
                },
                trailing: voiceManager.transcriptionHistory.isEmpty ? nil :
                    AnyView(Button("Clear All") {
                        voiceManager.clearTranscriptionHistory()
                    }
                    .foregroundColor(.red))
            )
        }
    }
}

//Transcription History Row
struct TranscriptionHistoryRow: View {
    let record: TranscriptionRecord
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(record.originalText)
                .font(.body)
                .lineLimit(nil)
            
            HStack {
                Text("\(record.tasksCount) task\(record.tasksCount == 1 ? "" : "s") extracted")
                    .font(.caption)
                    .foregroundColor(.blue)
                
                Spacer()
                
                Text(record.timestamp, style: .date)
                    .font(.caption2)
                    .foregroundColor(.secondary)
                
                Text(record.timestamp, style: .time)
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 4)
    }
}


