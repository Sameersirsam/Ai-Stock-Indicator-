package com.example.data.repository

import com.example.data.api.RetrofitClient
import com.example.data.database.CreatorDao
import com.example.data.model.WatchlistItem
import com.example.data.model.PriceAlert
import com.example.data.model.PortfolioHolding
import com.example.data.model.Content
import com.example.data.model.GenerateContentRequest
import com.example.data.model.GenerationConfig
import com.example.data.model.Part
import kotlinx.coroutines.flow.Flow

class CreatorRepository(private val creatorDao: CreatorDao) {

    // --- Watchlist DB Operations ---
    val allWatchlist: Flow<List<WatchlistItem>> = creatorDao.getAllWatchlist()

    suspend fun addToWatchlist(item: WatchlistItem) {
        creatorDao.insertWatchlistItem(item)
    }

    suspend fun removeFromWatchlist(symbol: String) {
        creatorDao.deleteWatchlistItem(symbol)
    }

    fun isSymbolInWatchlist(symbol: String): Flow<Boolean> {
        return creatorDao.isInWatchlist(symbol)
    }

    // --- Price Alerts DB Operations ---
    val allPriceAlerts: Flow<List<PriceAlert>> = creatorDao.getAllPriceAlerts()

    suspend fun addPriceAlert(alert: PriceAlert) {
        creatorDao.insertPriceAlert(alert)
    }

    suspend fun deletePriceAlert(id: Int) {
        creatorDao.deletePriceAlert(id)
    }

    suspend fun updatePriceAlert(alert: PriceAlert) {
        creatorDao.updatePriceAlert(alert)
    }

    // --- Portfolio DB Operations ---
    val allPortfolioHoldings: Flow<List<PortfolioHolding>> = creatorDao.getAllPortfolioHoldings()

    suspend fun addPortfolioHolding(holding: PortfolioHolding) {
        creatorDao.insertPortfolioHolding(holding)
    }

    suspend fun deletePortfolioHolding(id: Int) {
        creatorDao.deletePortfolioHolding(id)
    }

    // --- Gemini Content Generation ---

    suspend fun generateContent(
        apiKey: String,
        prompt: String,
        systemInstruction: String? = null
    ): String {
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.5f,
                topP = 0.95f
            ),
            systemInstruction = systemInstruction?.let {
                Content(parts = listOf(Part(text = it)))
            }
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No response from AI Stock advisor. Please try again."
        } catch (e: Exception) {
            "Error calling AI Analyzer: ${e.localizedMessage ?: e.message ?: "Failed to connect to the AI model. Check your connection and try again."}"
        }
    }

    suspend fun getStockPredictionAnalysis(
        apiKey: String,
        symbol: String,
        name: String,
        price: Double,
        rsi: Int,
        macd: String,
        volumePercent: Double
    ): String {
        val systemInstruction = """
            You are a senior quantitative analyst and AI investment researcher specializing in technical and fundamental stock scanning. 
            You must analyze indicators and news to provide detailed, actionable stock insights.
            Disclaimer notice: You are an AI model, not a SEBI/SEC registered advisor. All recommendations must be presented with probability scores, target values, risk levels, and a standard disclaimer.
        """.trimIndent()

        val prompt = """
            Perform a complete AI analysis for $name ($symbol) based on these parameters:
            - Current Price: ₹$price
            - RSI (14): $rsi
            - MACD Signal: $macd
            - Volume Change: +$volumePercent% over 10-day average
            
            Format your response professionally with Markdown. Provide these sections:
            1. **Market Sentiment & Rating**: Outlined with recommendation (Strong Buy, Hold, or Avoid), AI probability score (0-100%), and sentiment details.
            2. **Technical Layout Analysis**: Explain the implications of RSI, MACD, and Volume surges for this stock.
            3. **Target & Risk Strategy**: Suggest a 24-hour target price, stop-loss limit, and overall risk rating (Low, Medium, High).
            4. **AI Catalyst Report**: Give a possible fundamental trigger or news catalyst (e.g. earnings, product launch, industry tailwinds) driving this prediction.
            
            Ensure the tone is analytical, clean, and highly specific to $symbol.
        """.trimIndent()

        return generateContent(apiKey, prompt, systemInstruction)
    }

    suspend fun scanMarketOverview(
        apiKey: String,
        scannerType: String,
        currentSentiment: String
    ): String {
        val systemInstruction = """
            You are an advanced real-time stock screener. You evaluate market-wide momentum to suggest high-probability setups.
            Ensure you output structured, attractive, and actionable scanner ideas.
        """.trimIndent()

        val prompt = """
            Generate a detailed scanning report for the **$scannerType** under the current **$currentSentiment** market sentiment.
            
            Give the report a beautiful title and provide:
            1. **Top 3 Candidate Stocks**: Include realistic symbols, names, current prices, target prices, stop losses, and technical catalysts.
            2. **Trigger Condition**: What momentum levels, volume breakouts, or charting patterns (like double bottoms, cup and handle) caused these to trigger?
            3. **Scan Summary**: A professional qualitative summary of what's happening across this specific segment of the market today.
            
            Format with beautiful markdown and structured bullet points. Keep it highly detailed.
        """.trimIndent()

        return generateContent(apiKey, prompt, systemInstruction)
    }

    suspend fun getStockNewsSentimentAnalysis(
        apiKey: String,
        niche: String
    ): String {
        val systemInstruction = "You are a professional financial news analyst tracking stock catalysts, policy updates, and corporate actions."
        val prompt = """
            Provide a financial newsletter summary of the top 3 high-impact stock news stories or macroeconomic events in India (NSE/BSE) relevant to the "$niche" sector today.
            
            For each story, provide:
            1. **Headline**: Dynamic and professional headline.
            2. **Catalyst & Analysis**: Explain what occurred and which major stock ticker is directly affected.
            3. **AI Sentiment Reading**: Bullish, Neutral, or Bearish with an AI score (0-100%) showing the strength of impact.
            4. **Actionable Trading Idea**: How retail investors can play this setup.
            
            Make the news feel fresh, realistic, and highly relevant. Format with beautiful markdown.
        """.trimIndent()

        return generateContent(apiKey, prompt, systemInstruction)
    }
}
