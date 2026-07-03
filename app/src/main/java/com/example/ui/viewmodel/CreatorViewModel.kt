package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.database.AppDatabase
import com.example.data.model.WatchlistItem
import com.example.data.model.PriceAlert
import com.example.data.model.PortfolioHolding
import com.example.data.model.StockDetail
import com.example.data.repository.CreatorRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class CreatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CreatorRepository

    // --- DB Flow States ---
    val watchlist: StateFlow<List<WatchlistItem>>
    val priceAlerts: StateFlow<List<PriceAlert>>
    val portfolioHoldings: StateFlow<List<PortfolioHolding>>

    // --- Live Ticker Ticker states ---
    private val _stocksList = MutableStateFlow<List<StockDetail>>(emptyList())
    val stocksList: StateFlow<List<StockDetail>> = _stocksList.asStateFlow()

    private val _marketSentiment = MutableStateFlow(68) // 0-100 gauge
    val marketSentiment: StateFlow<Int> = _marketSentiment.asStateFlow()

    // --- Active Selection State ---
    private val _selectedStock = MutableStateFlow<StockDetail?>(null)
    val selectedStock: StateFlow<StockDetail?> = _selectedStock.asStateFlow()

    // --- UI State managers ---
    private val _activeScannerReport = MutableStateFlow<String?>(null)
    val activeScannerReport: StateFlow<String?> = _activeScannerReport.asStateFlow()

    private val _activeNewsReport = MutableStateFlow<String?>(null)
    val activeNewsReport: StateFlow<String?> = _activeNewsReport.asStateFlow()

    private val _activeAnalysisReport = MutableStateFlow<String?>(null)
    val activeAnalysisReport: StateFlow<String?> = _activeAnalysisReport.asStateFlow()

    // --- Loading States ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // --- Monitization / Free Quota states ---
    private val _freeScanCount = MutableStateFlow(3)
    val freeScanCount: StateFlow<Int> = _freeScanCount.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    // --- Active Alert Triggers ---
    private val _triggeredAlerts = MutableStateFlow<List<PriceAlert>>(emptyList())
    val triggeredAlerts: StateFlow<List<PriceAlert>> = _triggeredAlerts.asStateFlow()

    // --- API Configuration ---
    private val _apiKey = MutableStateFlow(BuildConfig.GEMINI_API_KEY)
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CreatorRepository(database.creatorDao())

        watchlist = repository.allWatchlist.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        priceAlerts = repository.allPriceAlerts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        portfolioHoldings = repository.allPortfolioHoldings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Initialize API key
        if (_apiKey.value == "MY_GEMINI_API_KEY" || _apiKey.value.isBlank()) {
            _apiKey.value = ""
        }

        // Setup mock stock list with historic prices
        initializeMockStocks()

        // Start live stock price ticking simulation
        startLiveStockPriceTicker()
    }

    private fun initializeMockStocks() {
        val initialList = listOf(
            StockDetail(
                symbol = "RELIANCE",
                name = "Reliance Industries Ltd.",
                currentPrice = 2465.50,
                prevClose = 2420.00,
                changePrice = 45.50,
                changePercent = 1.88,
                predictedTarget = 2650.00,
                stopLoss = 2380.00,
                riskLevel = "Low",
                volumeIncreasePercent = 145.2,
                rsi = 58,
                macdSignal = "Bullish Crossover",
                aiScore = 88,
                recommendation = "BUY",
                high24h = 2480.00,
                low24h = 2410.00,
                volume = 3840200L,
                prices = generateInitialHistory(2465.50)
            ),
            StockDetail(
                symbol = "TATASTEEL",
                name = "Tata Steel Ltd.",
                currentPrice = 134.80,
                prevClose = 122.30,
                changePrice = 12.50,
                changePercent = 10.22,
                predictedTarget = 155.00,
                stopLoss = 125.00,
                riskLevel = "Medium",
                volumeIncreasePercent = 380.5,
                rsi = 74,
                macdSignal = "Strong Breakout",
                aiScore = 95,
                recommendation = "BUY",
                high24h = 136.20,
                low24h = 121.50,
                volume = 24500000L,
                prices = generateInitialHistory(134.80)
            ),
            StockDetail(
                symbol = "INFY",
                name = "Infosys Ltd.",
                currentPrice = 1420.20,
                prevClose = 1435.00,
                changePrice = -14.80,
                changePercent = -1.03,
                predictedTarget = 1580.00,
                stopLoss = 1380.00,
                riskLevel = "Low",
                volumeIncreasePercent = -15.4,
                rsi = 42,
                macdSignal = "Bearish Trend",
                aiScore = 48,
                recommendation = "HOLD",
                high24h = 1445.00,
                low24h = 1410.00,
                volume = 1205000L,
                prices = generateInitialHistory(1420.20)
            ),
            StockDetail(
                symbol = "HDFCBANK",
                name = "HDFC Bank Ltd.",
                currentPrice = 1612.40,
                prevClose = 1605.00,
                changePrice = 7.40,
                changePercent = 0.46,
                predictedTarget = 1750.00,
                stopLoss = 1570.00,
                riskLevel = "Low",
                volumeIncreasePercent = 45.8,
                rsi = 52,
                macdSignal = "Neutral",
                aiScore = 72,
                recommendation = "BUY",
                high24h = 1625.00,
                low24h = 1595.00,
                volume = 5120000L,
                prices = generateInitialHistory(1612.40)
            ),
            StockDetail(
                symbol = "SBIN",
                name = "State Bank of India",
                currentPrice = 582.10,
                prevClose = 562.40,
                changePrice = 19.70,
                changePercent = 3.50,
                predictedTarget = 640.00,
                stopLoss = 550.00,
                riskLevel = "Medium",
                volumeIncreasePercent = 192.6,
                rsi = 68,
                macdSignal = "Bullish Crossover",
                aiScore = 91,
                recommendation = "BUY",
                high24h = 589.40,
                low24h = 560.20,
                volume = 11450000L,
                prices = generateInitialHistory(582.10)
            ),
            StockDetail(
                symbol = "ADANIENT",
                name = "Adani Enterprises Ltd.",
                currentPrice = 2390.00,
                prevClose = 2120.00,
                changePrice = 270.00,
                changePercent = 12.74,
                predictedTarget = 2850.00,
                stopLoss = 2150.00,
                riskLevel = "High",
                volumeIncreasePercent = 485.2,
                rsi = 79,
                macdSignal = "Strong Volatility Breakout",
                aiScore = 94,
                recommendation = "BUY",
                high24h = 2440.00,
                low24h = 2100.00,
                volume = 8240000L,
                prices = generateInitialHistory(2390.00)
            ),
            StockDetail(
                symbol = "ITC",
                name = "ITC Limited",
                currentPrice = 435.60,
                prevClose = 438.00,
                changePrice = -2.40,
                changePercent = -0.55,
                predictedTarget = 475.00,
                stopLoss = 422.00,
                riskLevel = "Low",
                volumeIncreasePercent = -8.2,
                rsi = 49,
                macdSignal = "Neutral Consolidating",
                aiScore = 60,
                recommendation = "HOLD",
                high24h = 442.00,
                low24h = 434.10,
                volume = 3450000L,
                prices = generateInitialHistory(435.60)
            ),
            StockDetail(
                symbol = "COALINDIA",
                name = "Coal India Ltd.",
                currentPrice = 286.40,
                prevClose = 264.10,
                changePrice = 22.30,
                changePercent = 8.44,
                predictedTarget = 325.00,
                stopLoss = 260.00,
                riskLevel = "Medium",
                volumeIncreasePercent = 312.4,
                rsi = 71,
                macdSignal = "MACD Bullish Cross",
                aiScore = 93,
                recommendation = "BUY",
                high24h = 292.00,
                low24h = 262.50,
                volume = 9800000L,
                prices = generateInitialHistory(286.40)
            ),
            StockDetail(
                symbol = "TATAMOTORS",
                name = "Tata Motors Ltd.",
                currentPrice = 612.50,
                prevClose = 582.10,
                changePrice = 30.40,
                changePercent = 5.22,
                predictedTarget = 690.00,
                stopLoss = 575.00,
                riskLevel = "Medium",
                volumeIncreasePercent = 215.3,
                rsi = 66,
                macdSignal = "Bullish Cross",
                aiScore = 92,
                recommendation = "BUY",
                high24h = 621.00,
                low24h = 580.00,
                volume = 12840000L,
                prices = generateInitialHistory(612.50)
            ),
            StockDetail(
                symbol = "SUNPHARMA",
                name = "Sun Pharmaceutical Industries",
                currentPrice = 1125.80,
                prevClose = 1128.50,
                changePrice = -2.70,
                changePercent = -0.24,
                predictedTarget = 1250.00,
                stopLoss = 1090.00,
                riskLevel = "Low",
                volumeIncreasePercent = 12.4,
                rsi = 51,
                macdSignal = "Neutral",
                aiScore = 68,
                recommendation = "HOLD",
                high24h = 1139.00,
                low24h = 1120.00,
                volume = 890000L,
                prices = generateInitialHistory(1125.80)
            ),
            StockDetail(
                symbol = "WIPRO",
                name = "Wipro Limited",
                currentPrice = 388.40,
                prevClose = 412.00,
                changePrice = -23.60,
                changePercent = -5.73,
                predictedTarget = 440.00,
                stopLoss = 378.00,
                riskLevel = "Medium",
                volumeIncreasePercent = 180.2,
                rsi = 28,
                macdSignal = "Oversold Bearish Trend",
                aiScore = 32,
                recommendation = "AVOID",
                high24h = 413.00,
                low24h = 385.00,
                volume = 6840000L,
                prices = generateInitialHistory(388.40)
            ),
            StockDetail(
                symbol = "ICICIBANK",
                name = "ICICI Bank Ltd.",
                currentPrice = 945.10,
                prevClose = 932.40,
                changePrice = 12.70,
                changePercent = 1.36,
                predictedTarget = 1040.00,
                stopLoss = 910.00,
                riskLevel = "Low",
                volumeIncreasePercent = 89.4,
                rsi = 61,
                macdSignal = "Bullish",
                aiScore = 82,
                recommendation = "BUY",
                high24h = 952.10,
                low24h = 930.50,
                volume = 4820000L,
                prices = generateInitialHistory(945.10)
            ),
            StockDetail(
                symbol = "BHARTIARTL",
                name = "Bharti Airtel Ltd.",
                currentPrice = 872.40,
                prevClose = 855.20,
                changePrice = 17.20,
                changePercent = 2.01,
                predictedTarget = 950.00,
                stopLoss = 835.00,
                riskLevel = "Medium",
                volumeIncreasePercent = 150.8,
                rsi = 63,
                macdSignal = "Bullish",
                aiScore = 85,
                recommendation = "BUY",
                high24h = 879.40,
                low24h = 852.10,
                volume = 2490000L,
                prices = generateInitialHistory(872.40)
            ),
            StockDetail(
                symbol = "TCS",
                name = "Tata Consultancy Services Ltd.",
                currentPrice = 3225.00,
                prevClose = 3254.00,
                changePrice = -29.00,
                changePercent = -0.89,
                predictedTarget = 3550.00,
                stopLoss = 3120.00,
                riskLevel = "Low",
                volumeIncreasePercent = -18.4,
                rsi = 44,
                macdSignal = "Neutral Bearish",
                aiScore = 55,
                recommendation = "HOLD",
                high24h = 3268.00,
                low24h = 3215.00,
                volume = 1100000L,
                prices = generateInitialHistory(3225.00)
            )
        )
        _stocksList.value = initialList
    }

    private fun generateInitialHistory(basePrice: Double): List<Double> {
        val list = mutableListOf<Double>()
        var current = basePrice - 30.0
        for (i in 0 until 10) {
            current += Random.nextDouble(-15.0, 20.0)
            list.add(current)
        }
        return list
    }

    private fun startLiveStockPriceTicker() {
        viewModelScope.launch {
            while (true) {
                delay(3500) // Ticking frequency
                val currentList = _stocksList.value
                if (currentList.isNotEmpty()) {
                    val updatedList = currentList.map { stock ->
                        // Simulate fluctuation of -0.8% to +0.8%
                        val pctChange = Random.nextDouble(-0.008, 0.008)
                        val oldPrice = stock.currentPrice
                        val newPrice = oldPrice * (1 + pctChange)

                        // Update high and low
                        val newHigh = if (newPrice > stock.high24h) newPrice else stock.high24h
                        val newLow = if (newPrice < stock.low24h) newPrice else stock.low24h

                        // Volumetric update
                        val volumeAddition = Random.nextLong(1000, 10000)
                        val newVolume = stock.volume + volumeAddition

                        // Fluctuate RSI slightly
                        val rsiChange = Random.nextInt(-1, 2)
                        val newRsi = (stock.rsi + rsiChange).coerceIn(10, 95)

                        // Recompute gains relative to standard prevClose
                        val changePrice = newPrice - stock.prevClose
                        val changePercent = (changePrice / stock.prevClose) * 100

                        // Append new price to history, drop oldest
                        val updatedPrices = stock.prices.drop(1) + newPrice

                        // Trigger alarm evaluator
                        evaluatePriceAlertsForStock(stock.symbol, newPrice)

                        stock.copy(
                            currentPrice = newPrice,
                            high24h = newHigh,
                            low24h = newLow,
                            volume = newVolume,
                            rsi = newRsi,
                            changePrice = changePrice,
                            changePercent = changePercent,
                            prices = updatedPrices
                        )
                    }
                    _stocksList.value = updatedList

                    // Sync current selected stock's details
                    val selected = _selectedStock.value
                    if (selected != null) {
                        _selectedStock.value = updatedList.firstOrNull { it.symbol == selected.symbol }
                    }

                    // Fluctuate overall market sentiment slightly (45 to 88)
                    val sentimentChange = Random.nextInt(-2, 3)
                    _marketSentiment.value = (_marketSentiment.value + sentimentChange).coerceIn(40, 92)
                }
            }
        }
    }

    private fun evaluatePriceAlertsForStock(symbol: String, currentPrice: Double) {
        val alerts = priceAlerts.value
        val triggeredThisTick = mutableListOf<PriceAlert>()

        alerts.forEach { alert ->
            if (alert.isActive && !alert.isTriggered && alert.symbol == symbol) {
                val isTriggered = if (alert.condition == "Above") {
                    currentPrice >= alert.targetPrice
                } else {
                    currentPrice <= alert.targetPrice
                }

                if (isTriggered) {
                    val updatedAlert = alert.copy(isTriggered = true, isActive = false)
                    viewModelScope.launch {
                        repository.updatePriceAlert(updatedAlert)
                    }
                    triggeredThisTick.add(updatedAlert)
                }
            }
        }

        if (triggeredThisTick.isNotEmpty()) {
            _triggeredAlerts.value = _triggeredAlerts.value + triggeredThisTick
        }
    }

    fun dismissTriggeredAlert(alertId: Int) {
        _triggeredAlerts.value = _triggeredAlerts.value.filterNot { it.id == alertId }
    }

    fun selectStock(stock: StockDetail) {
        _selectedStock.value = stock
    }

    fun clearSelectedStock() {
        _selectedStock.value = null
    }

    fun updateApiKey(newKey: String) {
        _apiKey.value = newKey
    }

    fun isApiKeyConfigured(): Boolean {
        val key = _apiKey.value
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    private fun getValidApiKey(): String? {
        val key = _apiKey.value
        return if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") key else null
    }

    fun clearResult() {
        _activeScannerReport.value = null
        _activeNewsReport.value = null
        _activeAnalysisReport.value = null
        _errorMessage.value = null
    }

    // --- Quota Management ---
    private fun useScanQuota(): Boolean {
        if (_isPremium.value) return true
        val currentQuota = _freeScanCount.value
        return if (currentQuota > 0) {
            _freeScanCount.value = currentQuota - 1
            true
        } else {
            _errorMessage.value = "You have reached your daily limit of 3 free AI scans. Please upgrade to Premium for unlimited scans!"
            false
        }
    }

    fun buyPremiumUpgrade() {
        _isPremium.value = true
        _freeScanCount.value = 9999
        _errorMessage.value = null
    }

    fun resetFreeQuota() {
        _freeScanCount.value = 3
        _isPremium.value = false
    }

    // --- AI REST API Operations ---

    fun scanStocksWithAI(scannerType: String) {
        val key = getValidApiKey()
        if (key == null) {
            _errorMessage.value = "Gemini API Key is not configured. Please add your key in the Settings tab."
            return
        }

        if (!useScanQuota()) return

        viewModelScope.launch {
            _isLoading.value = true
            _activeScannerReport.value = null
            _errorMessage.value = null
            try {
                val sentimentText = if (_marketSentiment.value > 65) "Bullish Mood" else "Consolidating/Neutral Mood"
                val result = repository.scanMarketOverview(key, scannerType, sentimentText)
                _activeScannerReport.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to generate AI scan."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchSectorNewsAI(sector: String) {
        val key = getValidApiKey()
        if (key == null) {
            _errorMessage.value = "Gemini API Key is not configured. Please add your key in the Settings tab."
            return
        }

        if (!useScanQuota()) return

        viewModelScope.launch {
            _isLoading.value = true
            _activeNewsReport.value = null
            _errorMessage.value = null
            try {
                val result = repository.getStockNewsSentimentAnalysis(key, sector)
                _activeNewsReport.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to load sector financial news."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun runStockDeepAnalysis(stock: StockDetail) {
        val key = getValidApiKey()
        if (key == null) {
            _errorMessage.value = "Gemini API Key is not configured. Please add your key in the Settings tab."
            return
        }

        if (!useScanQuota()) return

        viewModelScope.launch {
            _isLoading.value = true
            _activeAnalysisReport.value = null
            _errorMessage.value = null
            try {
                val result = repository.getStockPredictionAnalysis(
                    apiKey = key,
                    symbol = stock.symbol,
                    name = stock.name,
                    price = stock.currentPrice,
                    rsi = stock.rsi,
                    macd = stock.macdSignal,
                    volumePercent = stock.volumeIncreasePercent
                )
                _activeAnalysisReport.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to run deep prediction scanner."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- Watchlist DB Operations ---

    fun toggleWatchlist(stock: StockDetail, isInWatchlist: Boolean) {
        viewModelScope.launch {
            if (isInWatchlist) {
                repository.removeFromWatchlist(stock.symbol)
            } else {
                val watchlistItem = WatchlistItem(
                    symbol = stock.symbol,
                    name = stock.name,
                    price = stock.currentPrice,
                    changePercent = stock.changePercent,
                    isBullish = stock.changePercent >= 0
                )
                repository.addToWatchlist(watchlistItem)
            }
        }
    }

    fun deleteWatchlistItemBySymbol(symbol: String) {
        viewModelScope.launch {
            repository.removeFromWatchlist(symbol)
        }
    }

    // --- Price Alert DB Operations ---

    fun createPriceAlert(symbol: String, name: String, targetPrice: Double, condition: String) {
        viewModelScope.launch {
            val alert = PriceAlert(
                symbol = symbol,
                name = name,
                targetPrice = targetPrice,
                condition = condition
            )
            repository.addPriceAlert(alert)
        }
    }

    fun deletePriceAlertItem(id: Int) {
        viewModelScope.launch {
            repository.deletePriceAlert(id)
        }
    }

    // --- Portfolio DB Operations ---

    fun purchasePortfolioHolding(symbol: String, name: String, qty: Double, buyPrice: Double) {
        viewModelScope.launch {
            val holding = PortfolioHolding(
                symbol = symbol,
                name = name,
                quantity = qty,
                buyPrice = buyPrice
            )
            repository.addPortfolioHolding(holding)
        }
    }

    fun deletePortfolioHoldingItem(id: Int) {
        viewModelScope.launch {
            repository.deletePortfolioHolding(id)
        }
    }
}
