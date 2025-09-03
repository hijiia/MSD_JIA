import Foundation

// DeepSeek Configuration for Free Usage
struct DeepSeekConfig {
    // 免费模型选择
    static let freeModel = "deepseek-chat" // 免费模型
    
    // 优化参数以节省 tokens
    static let optimizedSettings = DeepSeekSettings(
        temperature: 0.1,           // 降低随机性，更精确的结果
        maxTokens: 300,            // 限制响应长度以节省 tokens
        stream: false
    )
    
    // 简化的 System Prompt 以节省 tokens
    static let compactSystemPrompt = """
    Extract tasks from text. Return JSON:
    {"tasks":[{"text":"task","category":"work/homework/social/chores/selfcare/urgent","priority":"High/Medium/Low","confidence":0.8}]}
    Categories: work, homework, social, chores, selfcare, urgent
    """
    
    // Token 使用追踪
    static var dailyTokenUsage: Int {
        get { UserDefaults.standard.integer(forKey: "deepseek_daily_tokens") }
        set {
            UserDefaults.standard.set(newValue, forKey: "deepseek_daily_tokens")
            UserDefaults.standard.set(Date(), forKey: "deepseek_last_update")
        }
    }
    
    // 检查是否应该重置每日计数
    static func checkAndResetDailyUsage() {
        let lastUpdate = UserDefaults.standard.object(forKey: "deepseek_last_update") as? Date ?? Date.distantPast
        let calendar = Calendar.current
        
        if !calendar.isDate(lastUpdate, inSameDayAs: Date()) {
            dailyTokenUsage = 0
        }
    }
    
    // 免费额度限制（保守估计）
    static let dailyFreeLimit = 10000 // tokens per day
    static let monthlyFreeLimit = 200000 // tokens per month
    
    // 检查是否可以使用 DeepSeek
    static var canUseDeepSeek: Bool {
        checkAndResetDailyUsage()
        return dailyTokenUsage < dailyFreeLimit
    }
}

struct DeepSeekSettings {
    let temperature: Double
    let maxTokens: Int
    let stream: Bool
}
