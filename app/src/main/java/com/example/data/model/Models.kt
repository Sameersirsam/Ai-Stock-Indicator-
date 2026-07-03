package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

// --- Room Database Entities ---

@Entity(tableName = "watchlist")
data class WatchlistItem(
    @PrimaryKey val symbol: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val isBullish: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val name: String,
    val targetPrice: Double,
    val condition: String, // "Above" or "Below"
    val isActive: Boolean = true,
    val isTriggered: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "portfolio_holdings")
data class PortfolioHolding(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val name: String,
    val quantity: Double,
    val buyPrice: Double,
    val timestamp: Long = System.currentTimeMillis()
)

// --- Stock Display Data Model (In-Memory Live Ticker state) ---

data class StockDetail(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val prevClose: Double,
    val changePrice: Double,
    val changePercent: Double,
    val predictedTarget: Double,
    val stopLoss: Double,
    val riskLevel: String, // "Low", "Medium", "High"
    val volumeIncreasePercent: Double, // e.g., 185.4 for +185.4%
    val rsi: Int, // e.g., 68
    val macdSignal: String, // "Bullish Crossover", "Bearish Crossover", "Neutral"
    val aiScore: Int, // 1 to 100
    val recommendation: String, // "BUY", "HOLD", "AVOID"
    val high24h: Double,
    val low24h: Double,
    val volume: Long,
    val prices: List<Double> = emptyList() // Custom history list (length 10) for Sparkline/Candlesticks
)

// --- Gemini API Models (Moshi) ---

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)
