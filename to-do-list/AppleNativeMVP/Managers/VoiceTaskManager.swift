import SwiftUI
import Speech
import AVFoundation
import Foundation
/*
   1.capture speech → convert to text → choose processing method (custom rules, AI, hybrid, or intelligent auto-selection) → extract tasks → save results.
   2.fallback: (if AI fails, use local rules), session management to prevent duplicate processing.
 */
//Voice Manager
class AppleVoiceTaskManager: ObservableObject {
    //trig update of ui
    @Published var isRecording = false
    @Published var recognizedText = ""
    @Published var extractedTasks: [TaskItem] = []
    @Published var transcriptionHistory: [TranscriptionRecord] = []
    @Published var status = "Ready"
    @Published var isProcessing = false
    @Published var processingMethod: ProcessingMethod = .intelligent
    @Published var showMethodComparison = false
    
    private let speechRecognizer = SFSpeechRecognizer(locale: Locale(identifier: "en-US"))
    private let audioEngine = AVAudioEngine()
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest?
    private var recognitionTask: SFSpeechRecognitionTask?
    private var hasProcessedCurrentSession = false
    //init
    init() {
        requestSpeechAuthorization()
        setupAudioSession()
        loadTranscriptionHistory()
    }
    
    func toggleMethodComparison() {
        showMethodComparison.toggle()
    }
    
    func requestSpeechAuthorization() {
        SFSpeechRecognizer.requestAuthorization { authStatus in
            DispatchQueue.main.async {
                switch authStatus {
                case .authorized:
                    self.status = "Ready"
                case .denied, .restricted, .notDetermined:
                    self.status = "Speech recognition unavailable"
                @unknown default:
                    self.status = "Unknown authorization status"
                }
            }
        }
    }
    
    private func setupAudioSession() {
        do {
            let audioSession = AVAudioSession.sharedInstance()
            try audioSession.setCategory(.playAndRecord, mode: .measurement, options: [.duckOthers, .defaultToSpeaker])
            try audioSession.setActive(true, options: .notifyOthersOnDeactivation)
        } catch {
            status = "Audio session setup failed"
        }
    }
    
    func startRecording() {
        guard let speechRecognizer = speechRecognizer, speechRecognizer.isAvailable else {
            status = "Speech recognizer unavailable"
            return
        }
        
        cleanupRecognition()
        recognizedText = ""
        hasProcessedCurrentSession = false
        
        recognitionRequest = SFSpeechAudioBufferRecognitionRequest()
        guard let recognitionRequest = recognitionRequest else {
            status = "Failed to create recognition request"
            return
        }
        
        recognitionRequest.shouldReportPartialResults = true
        recognitionRequest.requiresOnDeviceRecognition = false
        
        let inputNode = audioEngine.inputNode
        let recordingFormat = inputNode.outputFormat(forBus: 0)
        
        inputNode.installTap(onBus: 0, bufferSize: 1024, format: recordingFormat) { buffer, _ in
            recognitionRequest.append(buffer)
        }
        
        audioEngine.prepare()
        
        do {
            try audioEngine.start()
            isRecording = true
            status = "Listening"
        } catch {
            status = "Failed to start recording"
            return
        }
        
        recognitionTask = speechRecognizer.recognitionTask(with: recognitionRequest) { [weak self] result, error in
            DispatchQueue.main.async {
                guard let self = self else { return }
                
                if let result = result {
                    self.recognizedText = result.bestTranscription.formattedString
                    
                    if result.isFinal && !self.hasProcessedCurrentSession {
                        self.processRecognizedText()
                    }
                }
                
                if let error = error {
                    print("Recognition error: \(error)")
                    self.stopRecording()
                }
            }
        }
    }
    
    func stopRecording() {
        cleanupRecognition()
        isRecording = false
        
        if !recognizedText.isEmpty && !hasProcessedCurrentSession {
            processRecognizedText()
        } else if recognizedText.isEmpty {
            status = "No speech detected"
        }
    }
    
    private func cleanupRecognition() {
        recognitionTask?.cancel()
        recognitionTask = nil
        recognitionRequest?.endAudio()
        recognitionRequest = nil
        
        if audioEngine.isRunning {
            audioEngine.stop()
            audioEngine.inputNode.removeTap(onBus: 0)
        }
    }
    
    private func processRecognizedText() {
        guard !hasProcessedCurrentSession else {
            print("Already processed this session, skipping")
            return
        }
        hasProcessedCurrentSession = true
        
        print("Starting to process recognized text: \(recognizedText)")
        
        isProcessing = true
        status = "Processing with \(processingMethod.rawValue)"
        
        Task {
            let textToProcess = self.recognizedText
            
            var extractedTasks: [TaskItem] = []
            
            switch self.processingMethod {
            case .customRules:
                print("Using custom rules")
                extractedTasks = self.extractTasksWithCustomRules(text: textToProcess)
                
            case .deepSeek:
                print("Using DeepSeek AI")
                do {
                    extractedTasks = try await DeepSeekService.shared.extractTasks(from: textToProcess)
                } catch {
                    print("DeepSeek API error: \(error)")
                    extractedTasks = self.extractTasksWithCustomRules(text: textToProcess)
                }
                
            case .hybrid:
                print("Using hybrid method")
                extractedTasks = self.extractTasksWithHybridMethod(text: textToProcess)
                
            case .intelligent, .intelligentLocal, .intelligentAI:
                print("Using intelligent")
                extractedTasks = await IntelligentTaskProcessor.shared.processText(textToProcess, voiceManager: self)
            }
            
            try? await Task.sleep(nanoseconds: 1_200_000_000)
            print("Extraction complete, \(extractedTasks.count) tasks found")
            
            await MainActor.run {
                if !textToProcess.isEmpty {
                    let record = TranscriptionRecord(
                        originalText: textToProcess,
                        timestamp: Date(),
                        tasksCount: extractedTasks.count
                    )
                    print("Saving transcription record: \(record.originalText)")
                    self.transcriptionHistory.insert(record, at: 0)
                    self.saveTranscriptionHistory()
                    print("Transcription history saved, total \(self.transcriptionHistory.count) records")
                }
                
                self.extractedTasks.append(contentsOf: extractedTasks)
                self.isProcessing = false
                self.status = "Complete"
                print("Processing complete, total \(self.extractedTasks.count) tasks")
            }
        }
    }
    
    func extractTasksWithHybridMethod(text: String) -> [TaskItem] {
        print("Starting hybrid method processing: \(text)")
        
        let customTasks = extractTasksWithCustomRules(text: text)
        
        // mixture 
        let selectedTasks = customTasks.map { task in
            TaskItem(
                text: task.text,
                category: task.category,
                confidence: task.confidence,
                timestamp: task.timestamp,
                extractedInfo: task.extractedInfo,
                processingMethod: .hybrid
            )
        }
        
        print("Hybrid method created \(selectedTasks.count) tasks")
        return selectedTasks
    }
    
    func clearTasks() {
        extractedTasks.removeAll()
        recognizedText = ""
        status = "Ready"
    }
    
    func clearTranscriptionHistory() {
        transcriptionHistory.removeAll()
        saveTranscriptionHistory()
    }
    
    private func saveTranscriptionHistory() {
        do {
            let encoded = try JSONEncoder().encode(transcriptionHistory)
            UserDefaults.standard.set(encoded, forKey: "transcriptionHistory")
            UserDefaults.standard.synchronize()
            print("saved \(transcriptionHistory.count) transcription records")
        } catch {
            print("Failed to save history: \(error)")
        }
    }
    
    private func loadTranscriptionHistory() {
        do {
            if let data = UserDefaults.standard.data(forKey: "transcriptionHistory") {
                let decoded = try JSONDecoder().decode([TranscriptionRecord].self, from: data)
                transcriptionHistory = decoded
                print("loaded \(transcriptionHistory.count) transcription records")
            } else {
                print("No history records found")
            }
        } catch {
            print("Failed to load transcription history: \(error)")
            transcriptionHistory = []
        }
    }
}
