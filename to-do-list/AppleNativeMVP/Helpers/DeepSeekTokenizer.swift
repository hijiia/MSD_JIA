
import Foundation

// MARK: - DeepSeek Official Tokenizer
class DeepSeekTokenizer {
    static let shared = DeepSeekTokenizer()
    
    // 基于官方配置的特殊 tokens
    private struct SpecialTokens {
        static let bosToken = "<｜begin▁of▁sentence｜>"
        static let eosToken = "<｜end▁of▁sentence｜>"
        static let padToken = "<｜end▁of▁sentence｜>"
        static let userToken = "<｜User｜>"
        static let assistantToken = "<｜Assistant｜>"
    }
    
    private var vocabulary: [String: Int] = [:]
    private var reverseVocabulary: [Int: String] = [:]
    private var merges: [(String, String)] = []
    private var specialTokenIds: [String: Int] = [:]
    private var maxLength: Int = 16384
    
    private init() {
        loadTokenizerConfig()
        loadMainTokenizer()
    }
    
    // 加载 tokenizer_config.json
    private func loadTokenizerConfig() {
        guard let path = Bundle.main.path(forResource: "tokenizer_config", ofType: "json") else {
            print("tokenizer_config.json not found")
            return
        }
        
        do {
            let data = try Data(contentsOf: URL(fileURLWithPath: path))
            let config = try JSONSerialization.jsonObject(with: data) as? [String: Any]
            
            if let maxLen = config?["model_max_length"] as? Int {
                self.maxLength = maxLen
                print("Model max length: \(maxLen)")
            }
            
            // 加载特殊 token IDs (需要从 tokenizer.json 获取实际 ID)
            setupSpecialTokens()
            
        } catch {
            print("Failed to load tokenizer_config.json: \(error)")
        }
    }
    
    // 加载主要的 tokenizer.json
    private func loadMainTokenizer() {
        guard let path = Bundle.main.path(forResource: "tokenizer", ofType: "json") else {
            print("tokenizer.json not found, using fallback")
            setupFallbackTokenizer()
            return
        }
        
        do {
            let data = try Data(contentsOf: URL(fileURLWithPath: path))
            let json = try JSONSerialization.jsonObject(with: data) as? [String: Any]
            
            // 加载词汇表
            if let model = json?["model"] as? [String: Any],
               let vocab = model["vocab"] as? [String: Int] {
                self.vocabulary = vocab
                self.reverseVocabulary = Dictionary(uniqueKeysWithValues: vocab.map { ($1, $0) })
                print("Loaded \(vocab.count) vocabulary entries")
            }
            
            // 加载 BPE merges
            if let model = json?["model"] as? [String: Any],
               let mergesArray = model["merges"] as? [String] {
                self.merges = mergesArray.compactMap { merge in
                    let parts = merge.components(separatedBy: " ")
                    return parts.count == 2 ? (parts[0], parts[1]) : nil
                }
                print("Loaded \(merges.count) BPE merges")
            }
            
            // 加载特殊 tokens
            if let addedTokens = json?["added_tokens"] as? [[String: Any]] {
                for token in addedTokens {
                    if let content = token["content"] as? String,
                       let id = token["id"] as? Int {
                        specialTokenIds[content] = id
                    }
                }
                print("Loaded \(specialTokenIds.count) special tokens")
            }
            
        } catch {
            print("Failed to load tokenizer.json: \(error)")
            setupFallbackTokenizer()
        }
    }
    
    // 设置特殊 tokens
    private func setupSpecialTokens() {
        // 这些 ID 需要从实际的 tokenizer.json 获取
        // 暂时使用估算值，实际值需要从文件中读取
        specialTokenIds = [
            SpecialTokens.bosToken: 1,
            SpecialTokens.eosToken: 2,
            SpecialTokens.userToken: 100000,
            SpecialTokens.assistantToken: 100001
        ]
    }
    
    // 主要的 tokenize 方法
    func tokenize(_ text: String) -> [Int] {
        if vocabulary.isEmpty {
            return fallbackTokenize(text)
        }
        
        return bpeTokenize(text)
    }
    
    // 计算 token 数量
    func countTokens(_ text: String) -> Int {
        return tokenize(text).count
    }
    
    // 计算聊天格式的 tokens
    func countChatTokens(systemPrompt: String, userMessage: String) -> ChatTokenCount {
        // 根据 DeepSeek 的 chat_template 格式化
        let formattedPrompt = formatChatPrompt(systemPrompt: systemPrompt, userMessage: userMessage)
        let totalTokens = countTokens(formattedPrompt)
        
        // 分别计算各部分
        let systemTokens = countTokens(systemPrompt)
        let userTokens = countTokens(userMessage)
        let templateOverhead = totalTokens - systemTokens - userTokens
        
        return ChatTokenCount(
            systemPrompt: systemTokens,
            userMessage: userTokens,
            templateOverhead: templateOverhead,
            total: totalTokens
        )
    }
    
    // 根据 DeepSeek 格式化聊天提示
    private func formatChatPrompt(systemPrompt: String, userMessage: String) -> String {
        // 简化版的 chat_template 实现
        let formatted = """
        \(SpecialTokens.bosToken)\(systemPrompt)\(SpecialTokens.userToken)\(userMessage)\(SpecialTokens.assistantToken)
        """
        return formatted
    }
    
    // BPE tokenization
    private func bpeTokenize(_ text: String) -> [Int] {
        var tokens: [Int] = []
        
        // 处理特殊 tokens
        var processedText = text
        for (specialToken, tokenId) in specialTokenIds {
            if processedText.contains(specialToken) {
                // 简化处理：如果包含特殊 token，添加其 ID
                processedText = processedText.replacingOccurrences(of: specialToken, with: " [\(tokenId)] ")
            }
        }
        
        // 按空格分割并处理
        let parts = processedText.components(separatedBy: .whitespacesAndNewlines)
            .filter { !$0.isEmpty }
        
        for part in parts {
            if part.hasPrefix("[") && part.hasSuffix("]") {
                // 特殊 token ID
                if let idString = part.dropFirst().dropLast().split(separator: " ").first,
                   let id = Int(idString) {
                    tokens.append(id)
                }
            } else {
                // 普通文本
                let wordTokens = tokenizeWord(part)
                tokens.append(contentsOf: wordTokens)
            }
        }
        
        return tokens
    }
    
    // 对单词进行 BPE 处理
    private func tokenizeWord(_ word: String) -> [Int] {
        // 直接查找词汇表
        if let tokenId = vocabulary[word] {
            return [tokenId]
        }
        
        // BPE 算法
        var chars = Array(word).map { String($0) }
        
        while chars.count > 1 {
            var bestMerge: (String, String)? = nil
            var bestMergeIndex = Int.max
            var bestPosition = -1
            
            // 查找最佳合并
            for i in 0..<chars.count - 1 {
                let pair = (chars[i], chars[i + 1])
                if let mergeIndex = merges.firstIndex(where: { $0.0 == pair.0 && $0.1 == pair.1 }) {
                    if mergeIndex < bestMergeIndex {
                        bestMerge = pair
                        bestMergeIndex = mergeIndex
                        bestPosition = i
                    }
                }
            }
            
            guard let merge = bestMerge, bestPosition >= 0 else { break }
            
            // 执行合并
            let merged = merge.0 + merge.1
            chars.remove(at: bestPosition + 1)
            chars[bestPosition] = merged
        }
        
        // 转换为 token IDs
        return chars.compactMap { char in
            vocabulary[char] ?? vocabulary["<unk>"] ?? 0
        }
    }
    
    // 降级 tokenizer
    private func setupFallbackTokenizer() {
        print("Using fallback tokenizer - results may not be 100% accurate")
        // 使用简单的估算方法
    }
    
    private func fallbackTokenize(_ text: String) -> [Int] {
        // 基于字符的简单估算
        let chineseCharCount = text.filter { char in
            let scalar = char.unicodeScalars.first!
            return scalar.value >= 0x4E00 && scalar.value <= 0x9FFF
        }.count
        
        let otherCharCount = text.count - chineseCharCount
        
       
        let estimatedCount = chineseCharCount + max(1, otherCharCount / 4)
        
        return Array(0..<estimatedCount)
    }
}

// Chat Token Count Result
struct ChatTokenCount {
    let systemPrompt: Int
    let userMessage: Int
    let templateOverhead: Int
    let total: Int
    
    var description: String {
        return "System: \(systemPrompt), User: \(userMessage), Overhead: \(templateOverhead), Total: \(total)"
    }
}
