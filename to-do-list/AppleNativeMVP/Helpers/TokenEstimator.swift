import Foundation

// Token Estimator
class TokenEstimator {
    static let shared = TokenEstimator()
    
    private init() {}
    func estimateTokens(for text: String) -> Int {
       
        
        let chineseCharCount = countChineseCharacters(in: text)
        let englishCharCount = text.count - chineseCharCount
        
        let chineseTokens = Int(Double(chineseCharCount) / 1.5)
        let englishTokens = Int(Double(englishCharCount) / 4.0)
        
        return max(1, chineseTokens + englishTokens)
    }
    
    // 使用官方 DeepSeek tokenizer（精确计算）
    func getExactTokenCount(for text: String) -> Int {
        // 如果有官方 tokenizer，使用它
        if let tokenizer = DeepSeekTokenizer.shared as? DeepSeekTokenizer {
            return tokenizer.countTokens(text)
        }
        // 否则使用估算
        return estimateTokens(for: text)
    }
    
    // 计算聊天格式的精确 token 数
    func getExactChatTokens(systemPrompt: String, userPrompt: String, expectedResponseLength: Int = 100) -> ChatTokenUsage {
        let systemTokens = getExactTokenCount(for: systemPrompt)
        let userTokens = getExactTokenCount(for: userPrompt)
        let templateOverhead = 10 // 估算模板开销
        
        return ChatTokenUsage(
            systemPrompt: systemTokens,
            userPrompt: userTokens,
            templateOverhead: templateOverhead,
            expectedResponse: expectedResponseLength,
            total: systemTokens + userTokens + templateOverhead + expectedResponseLength
        )
    }
    
    //api requests
    func estimateRequestTokens(systemPrompt: String, userPrompt: String, expectedResponseLength: Int = 100) -> ChatTokenUsage {
        return getExactChatTokens(
            systemPrompt: systemPrompt,
            userPrompt: userPrompt,
            expectedResponseLength: expectedResponseLength
        )
    }
    
    private func countChineseCharacters(in text: String) -> Int {
        return text.filter { char in
            let scalar = char.unicodeScalars.first!
            return scalar.value >= 0x4E00 && scalar.value <= 0x9FFF
        }.count
    }
}

//Chat Token Usage Result
struct ChatTokenUsage {
    let systemPrompt: Int
    let userPrompt: Int
    let templateOverhead: Int
    let expectedResponse: Int
    let total: Int
    
    var description: String {
        return "System: \(systemPrompt), User: \(userPrompt), Template: \(templateOverhead), Response: \(expectedResponse), Total: \(total)"
    }
}
