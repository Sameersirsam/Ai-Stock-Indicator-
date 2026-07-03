package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PriceAlert
import com.example.data.model.StockDetail
import com.example.data.model.WatchlistItem
import com.example.data.model.PortfolioHolding
import com.example.ui.viewmodel.CreatorViewModel
import androidx.compose.ui.zIndex
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

// --- Sleek Custom Stock Colors (Gold Highlights + High Contrast Dark Theme) ---
val JetBlack = Color(0xFF040406)
val DeepCharcoal = Color(0xFF101016)
val LightGlass = Color(0x1AFFFFFF)
val GlassBorder = Color(0x33FFFFFF)
val GoldGold = Color(0xFFFFD700)
val GoldMuted = Color(0xFFC5A02B)
val BullishGreen = Color(0xFF00E676)
val BearishRed = Color(0xFFFF1744)
val TechCyan = Color(0xFF00E5FF)
val TextGrayMuted = Color(0xFFA0A0A8)

enum class StockTab(val title: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "tab_home"),
    SCANNERS("Scanners", Icons.Default.QueryStats, "tab_scanners"),
    WATCHLIST("Watchlist", Icons.Default.Star, "tab_watchlist"),
    PORTFOLIO("Portfolio", Icons.Default.Wallet, "tab_portfolio"),
    ALERTS("Alerts", Icons.Default.Notifications, "tab_alerts"),
    SETTINGS("Settings", Icons.Default.Settings, "tab_settings")
}

@Composable
fun CreatorApp(viewModel: CreatorViewModel) {
    val context = LocalContext.current

    var currentTab by remember { mutableStateOf(StockTab.HOME) }
    val stocksList by viewModel.stocksList.collectAsState()
    val marketSentiment by viewModel.marketSentiment.collectAsState()
    val selectedStock by viewModel.selectedStock.collectAsState()

    val watchlist by viewModel.watchlist.collectAsState()
    val priceAlerts by viewModel.priceAlerts.collectAsState()
    val portfolioHoldings by viewModel.portfolioHoldings.collectAsState()

    val activeScannerReport by viewModel.activeScannerReport.collectAsState()
    val activeNewsReport by viewModel.activeNewsReport.collectAsState()
    val activeAnalysisReport by viewModel.activeAnalysisReport.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val freeScanCount by viewModel.freeScanCount.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val triggeredAlerts by viewModel.triggeredAlerts.collectAsState()

    // Dialog state for buying Premium
    var showPremiumModal by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DeepCharcoal,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .testTag("navigation_bar")
                    .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                StockTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title, modifier = Modifier.size(24.dp)) },
                        label = { Text(tab.title, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = JetBlack,
                            selectedTextColor = GoldGold,
                            unselectedIconColor = TextGrayMuted,
                            unselectedTextColor = TextGrayMuted,
                            indicatorColor = GoldGold
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        },
        containerColor = JetBlack
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isExpanded = maxWidth > 600.dp
            val modifierWithWidth = if (isExpanded) {
                Modifier
                    .fillMaxHeight()
                    .widthIn(max = 850.dp)
                    .align(Alignment.TopCenter)
            } else {
                Modifier.fillMaxSize()
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = modifierWithWidth.padding(16.dp)) {
                    // Header layout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(GoldGold, GoldMuted)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = JetBlack,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "AI STOCK SCANNER",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (isPremium) {
                                        Box(
                                            modifier = Modifier
                                                .background(GoldGold, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                "PREMIUM",
                                                color = JetBlack,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Predictive Indian Market Intelligence (NSE/BSE)",
                                    fontSize = 10.sp,
                                    color = TextGrayMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Scan quota pill clickable to purchase premium
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(LightGlass)
                                .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(12.dp))
                                .clickable {
                                    if (!isPremium) {
                                        showPremiumModal = true
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = GoldGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPremium) "UNLIMITED" else "$freeScanCount SCANS LEFT",
                                    color = GoldGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Notification Bar
                    if (errorMessage != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable {
                                    if (errorMessage!!.contains("limit")) {
                                        showPremiumModal = true
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0x3DFF1744)),
                            border = BorderStroke(1.dp, BearishRed.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = "Error", tint = BearishRed)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errorMessage!!,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.clearResult() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Render Active Tab Panels
                    Box(modifier = Modifier.weight(1f)) {
                        when (currentTab) {
                            StockTab.HOME -> HomePanel(
                                viewModel = viewModel,
                                stocks = stocksList,
                                sentiment = marketSentiment,
                                onStockSelected = { stock -> viewModel.selectStock(stock) }
                            )
                            StockTab.SCANNERS -> ScannersPanel(
                                viewModel = viewModel,
                                isLoading = isLoading,
                                activeScannerReport = activeScannerReport,
                                activeNewsReport = activeNewsReport,
                                onStockSelected = { stock -> viewModel.selectStock(stock) }
                            )
                            StockTab.WATCHLIST -> WatchlistPanel(
                                viewModel = viewModel,
                                watchlist = watchlist,
                                allStocks = stocksList,
                                onStockSelected = { stock -> viewModel.selectStock(stock) }
                            )
                            StockTab.PORTFOLIO -> PortfolioPanel(
                                viewModel = viewModel,
                                holdings = portfolioHoldings,
                                allStocks = stocksList
                            )
                            StockTab.ALERTS -> AlertsPanel(
                                viewModel = viewModel,
                                priceAlerts = priceAlerts,
                                allStocks = stocksList
                            )
                            StockTab.SETTINGS -> SettingsPanel(
                                viewModel = viewModel,
                                apiKey = viewModel.apiKey.collectAsState().value,
                                isPremium = isPremium,
                                freeScanCount = freeScanCount
                            )
                        }
                    }
                }

                // Alert triggered floating notifications
                AnimatedVisibility(
                    visible = triggeredAlerts.isNotEmpty(),
                    enter = slideInVertically { -it } + fadeIn(),
                    exit = slideOutVertically { -it } + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .zIndex(2f)
                ) {
                    val alert = triggeredAlerts.firstOrNull()
                    if (alert != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(BorderStroke(1.5.dp, GoldGold), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.NotificationsActive,
                                        contentDescription = "Alert Triggered",
                                        tint = GoldGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "PRICE ALERT TRIGGERED!",
                                            color = GoldGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            "${alert.symbol} crossed Target ₹${alert.targetPrice} (${alert.condition})",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.dismissTriggeredAlert(alert.id) },
                                    modifier = Modifier
                                        .background(LightGlass, CircleShape)
                                        .size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Slide Up Stock detail modal / overlay
                AnimatedVisibility(
                    visible = selectedStock != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                ) {
                    val stock = selectedStock
                    if (stock != null) {
                        StockDetailModal(
                            viewModel = viewModel,
                            stock = stock,
                            watchlist = watchlist,
                            priceAlerts = priceAlerts,
                            portfolioHoldings = portfolioHoldings,
                            isLoading = isLoading,
                            activeAnalysisReport = activeAnalysisReport,
                            onClose = {
                                viewModel.clearSelectedStock()
                                viewModel.clearResult()
                            }
                        )
                    }
                }

                // Purchase Premium upgrade dialog modal
                if (showPremiumModal) {
                    PremiumUpgradeModal(
                        onDismiss = { showPremiumModal = false },
                        onUpgrade = {
                            viewModel.buyPremiumUpgrade()
                            showPremiumModal = false
                            Toast.makeText(context, "Successfully Upgraded to Premium!", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }
}

// zIndex is handled by direct import from androidx.compose.ui.draw.zIndex

// ======================== PANELS IMPLEMENTATIONS ========================

@Composable
fun HomePanel(
    viewModel: CreatorViewModel,
    stocks: List<StockDetail>,
    sentiment: Int,
    onStockSelected: (StockDetail) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Market Sentiment curved gauge
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "MARKET SENTIMENT INDICATOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Drawing Custom Gauge
                        Box(
                            modifier = Modifier.size(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 12.dp.toPx()
                                // Background semi-circular track
                                drawArc(
                                    color = LightGlass,
                                    startAngle = 180f,
                                    sweepAngle = 180f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                // Highlighted filled track representing the sentiment percentage
                                val sweep = (sentiment.toFloat() / 100f) * 180f
                                drawArc(
                                    brush = Brush.linearGradient(
                                        listOf(BearishRed, GoldGold, BullishGreen)
                                    ),
                                    startAngle = 180f,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$sentiment%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = if (sentiment > 65) "BULLISH" else if (sentiment > 45) "NEUTRAL" else "BEARISH",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sentiment > 65) BullishGreen else if (sentiment > 45) GoldGold else BearishRed
                                )
                            }
                        }

                        // Data values
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            SentimentMetricRow("AI Volume Multiplier", "3.4x Average", GoldGold)
                            SentimentMetricRow("Advance/Decline Ratio", "11:3 Bullish", BullishGreen)
                            SentimentMetricRow("Breakout Confidence Score", "92% Probability", TechCyan)
                        }
                    }
                }
            }
        }

        // Today's Top Gainers (Predicted)
        item {
            StockSliderSection(
                title = "🚀 Today's Top Gainers (Predicted)",
                stocks = stocks.sortedByDescending { it.changePercent }.take(5),
                onStockClick = onStockSelected
            )
        }

        // Breakout Stocks
        item {
            StockSliderSection(
                title = "🔥 Breakout Stocks",
                stocks = stocks.filter { it.rsi >= 68 }.sortedByDescending { it.volumeIncreasePercent }.take(5),
                onStockClick = onStockSelected
            )
        }

        // High Volume Stocks
        item {
            StockSliderSection(
                title = "📈 High Volume Stocks",
                stocks = stocks.sortedByDescending { it.volume }.take(5),
                onStockClick = onStockSelected
            )
        }

        // Intraday scanner banner CTA
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, GoldMuted.copy(alpha = 0.5f)), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = JetBlack),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(GoldMuted.copy(alpha = 0.15f), JetBlack)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "INTELLIGENT SWING SCREENER",
                                color = GoldGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Scan over 20+ indicators and technical news signals using AI. Let the advisor predict upcoming breakthroughs.",
                            color = TextGrayMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SentimentMetricRow(label: String, valStr: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: ",
            color = TextGrayMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = valStr,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StockSliderSection(
    title: String,
    stocks: List<StockDetail>,
    onStockClick: (StockDetail) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(stocks) { stock ->
                StockGlassCard(stock = stock, onClick = { onStockClick(stock) })
            }
        }
    }
}

@Composable
fun StockGlassCard(
    stock: StockDetail,
    onClick: () -> Unit
) {
    val isUp = stock.changePercent >= 0
    Card(
        modifier = Modifier
            .width(180.dp)
            .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("stock_card_${stock.symbol}"),
        colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Ticker & AI rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        stock.symbol,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        stock.name,
                        fontSize = 8.sp,
                        color = TextGrayMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(100.dp)
                    )
                }

                // AI confidence Score
                Box(
                    modifier = Modifier
                        .background(GoldGold.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        "⭐ ${stock.aiScore}%",
                        color = GoldGold,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current price and trend indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        "₹${String.format(Locale.US, "%.2f", stock.currentPrice)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isUp) Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (isUp) BullishGreen else BearishRed,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${if (isUp) "+" else ""}${String.format(Locale.US, "%.2f", stock.changePercent)}%",
                            color = if (isUp) BullishGreen else BearishRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Small sparkline Canvas
                Canvas(
                    modifier = Modifier
                        .width(50.dp)
                        .height(24.dp)
                ) {
                    if (stock.prices.size >= 2) {
                        val maxPrice = stock.prices.maxOrNull() ?: 1.0
                        val minPrice = stock.prices.minOrNull() ?: 0.0
                        val delta = if (maxPrice == minPrice) 1.0 else (maxPrice - minPrice)

                        val points = stock.prices.mapIndexed { idx, price ->
                            val x = (idx.toFloat() / (stock.prices.size - 1)) * size.width
                            val y = size.height - (((price - minPrice) / delta).toFloat() * size.height)
                            Offset(x, y)
                        }

                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = if (isUp) BullishGreen else BearishRed,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick technical status tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Risk: ${stock.riskLevel}",
                    fontSize = 8.sp,
                    color = if (stock.riskLevel == "Low") BullishGreen else if (stock.riskLevel == "Medium") GoldGold else BearishRed,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .background(
                            if (stock.recommendation == "BUY") BullishGreen.copy(alpha = 0.15f)
                            else if (stock.recommendation == "HOLD") GoldGold.copy(alpha = 0.15f)
                            else BearishRed.copy(alpha = 0.15f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        stock.recommendation,
                        color = if (stock.recommendation == "BUY") BullishGreen else if (stock.recommendation == "HOLD") GoldGold else BearishRed,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

// --- SCANNER PANEL ---
@Composable
fun ScannersPanel(
    viewModel: CreatorViewModel,
    isLoading: Boolean,
    activeScannerReport: String?,
    activeNewsReport: String?,
    onStockSelected: (StockDetail) -> Unit
) {
    var activeTab by remember { mutableStateOf("Intraday Scanner") }
    var selectedNewsSector by remember { mutableStateOf("Technology") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepCharcoal, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            listOf("Intraday Scanner", "Swing Scanner", "AI Stock News").forEach { tab ->
                val selected = activeTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) GoldGold else Color.Transparent)
                        .clickable { activeTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (selected) JetBlack else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = GoldGold, modifier = Modifier.size(50.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Triggering AI Scanner Neural Nets...", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Gemini is sweeping volume, RSI levels, and MACD indicators...",
                        color = TextGrayMuted,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (activeTab == "AI Stock News") {
            // Sector Select Dropdown and News layout
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Sector News Catalyst",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Selection buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Technology", "Banking", "Metals", "Auto").forEach { sec ->
                            val isSel = selectedNewsSector == sec
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) GoldGold.copy(alpha = 0.2f) else LightGlass)
                                    .border(BorderStroke(0.5.dp, if (isSel) GoldGold else GlassBorder), RoundedCornerShape(8.dp))
                                    .clickable { selectedNewsSector = sec }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(sec, color = if (isSel) GoldGold else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (activeNewsReport != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
                    ) {
                        LazyColumn(modifier = Modifier.padding(16.dp)) {
                            item {
                                Text(
                                    text = activeNewsReport!!,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.clearResult() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LightGlass),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear News Results", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldGold, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Run Sector-specific AI Catalyst Scan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Gemini will summarize news catalogs & predict high volatility movers.",
                            color = TextGrayMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.fetchSectorNewsAI(selectedNewsSector) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("run_news_scan_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JetBlack)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan $selectedNewsSector Sector Catalyst", color = JetBlack, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        } else {
            // Intraday / Swing Scanner Report output
            if (activeScannerReport != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
                    ) {
                        LazyColumn(modifier = Modifier.padding(16.dp)) {
                            item {
                                Text(
                                    text = activeScannerReport!!,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.clearResult() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LightGlass),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Scan Another Criteria", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.QueryStats, contentDescription = null, tint = GoldGold, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (activeTab == "Intraday Scanner") "AI Intraday Scanner" else "AI Swing Breakout Screener",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (activeTab == "Intraday Scanner")
                            "Generates high momentum breakouts for intraday traders based on live 15-minute volume spikes & MACD reversals."
                        else
                            "Generates multi-day swing trend predictions using support-resistance breakdowns and daily RSI oscillators.",
                        color = TextGrayMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.scanStocksWithAI(activeTab) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .testTag("execute_ai_scan_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JetBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Execute AI Gainer Scan",
                            color = JetBlack,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

// --- WATCHLIST PANEL ---
@Composable
fun WatchlistPanel(
    viewModel: CreatorViewModel,
    watchlist: List<WatchlistItem>,
    allStocks: List<StockDetail>,
    onStockSelected: (StockDetail) -> Unit
) {
    if (watchlist.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = GoldGold.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Your Watchlist is Empty", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Tap on any stock from the Home Screen and click 'Add Watchlist' to observe real-time predictions.",
                color = TextGrayMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
                lineHeight = 16.sp
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "WATCHED TICKERS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldGold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(watchlist) { item ->
                    // Find latest in-memory price
                    val matchedStock = allStocks.firstOrNull { it.symbol == item.symbol }
                    val currentPrice = matchedStock?.currentPrice ?: item.price
                    val currentChange = matchedStock?.changePercent ?: item.changePercent
                    val isUp = currentChange >= 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(12.dp))
                            .clickable {
                                if (matchedStock != null) {
                                    onStockSelected(matchedStock)
                                }
                            }
                            .testTag("watchlist_item_${item.symbol}"),
                        colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(LightGlass, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.symbol.take(2),
                                        color = GoldGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(item.symbol, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(item.name, color = TextGrayMuted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(130.dp))
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "₹${String.format(Locale.US, "%.2f", currentPrice)}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        "${if (isUp) "+" else ""}${String.format(Locale.US, "%.2f", currentChange)}%",
                                        color = if (isUp) BullishGreen else BearishRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(
                                    onClick = { viewModel.deleteWatchlistItemBySymbol(item.symbol) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(BearishRed.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BearishRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PORTFOLIO PANEL ---
@Composable
fun PortfolioPanel(
    viewModel: CreatorViewModel,
    holdings: List<com.example.data.model.PortfolioHolding>,
    allStocks: List<StockDetail>
) {
    var showAddHoldingDialog by remember { mutableStateOf(false) }

    // Aggregate portfolio returns
    var totalCost = 0.0
    var totalCurrentValue = 0.0

    holdings.forEach { holding ->
        val latestPrice = allStocks.firstOrNull { it.symbol == holding.symbol }?.currentPrice ?: holding.buyPrice
        totalCost += holding.buyPrice * holding.quantity
        totalCurrentValue += latestPrice * holding.quantity
    }

    val totalProfit = totalCurrentValue - totalCost
    val profitPercent = if (totalCost > 0) (totalProfit / totalCost) * 100 else 0.0
    val isProfit = totalProfit >= 0

    Column(modifier = Modifier.fillMaxSize()) {
        // Portfolio overall valuation card with gold highlighting
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.5.dp, GoldGold), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(GoldMuted.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "MY AI PORTFOLIO VALUE",
                        color = GoldGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(GoldGold, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("VIRTUAL", color = JetBlack, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "₹${String.format(Locale.US, "%,.2f", totalCurrentValue)}",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Floating Gains", color = TextGrayMuted, fontSize = 9.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isProfit) Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (isProfit) BullishGreen else BearishRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "₹${String.format(Locale.US, "%.2f", totalProfit)} (${if (isProfit) "+" else ""}${String.format(Locale.US, "%.2f", profitPercent)}%)",
                                color = if (isProfit) BullishGreen else BearishRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Invested Cost", color = TextGrayMuted, fontSize = 9.sp)
                        Text(
                            "₹${String.format(Locale.US, "%,.2f", totalCost)}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "HOLDINGS LIST",
                color = GoldGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Button(
                onClick = { showAddHoldingDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldGold),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("add_holding_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = JetBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Trade", color = JetBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (holdings.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Wallet, contentDescription = null, tint = TextGrayMuted.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("No Registered Holdings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Simulate paper trading! Click 'Log Trade' to add a custom stock investment and monitor live profits.",
                    color = TextGrayMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    lineHeight = 16.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(holdings) { holding ->
                    val latestStock = allStocks.firstOrNull { it.symbol == holding.symbol }
                    val currentPrice = latestStock?.currentPrice ?: holding.buyPrice
                    val holdingTotalCost = holding.buyPrice * holding.quantity
                    val holdingCurrentVal = currentPrice * holding.quantity
                    val holdingProfit = holdingCurrentVal - holdingTotalCost
                    val holdingProfitPct = (holdingProfit / holdingTotalCost) * 100
                    val holdingIsProfit = holdingProfit >= 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(12.dp))
                            .testTag("holding_item_${holding.symbol}"),
                        colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(holding.symbol, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Qty: ${holding.quantity}", color = TextGrayMuted, fontSize = 11.sp)
                                }
                                Text("Avg Buy Price: ₹${String.format(Locale.US, "%.2f", holding.buyPrice)}", color = TextGrayMuted, fontSize = 10.sp)
                                Text("Live Price: ₹${String.format(Locale.US, "%.2f", currentPrice)}", color = TextGrayMuted, fontSize = 10.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "₹${String.format(Locale.US, "%.2f", holdingCurrentVal)}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (holdingIsProfit) Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.TrendingDown,
                                            contentDescription = null,
                                            tint = if (holdingIsProfit) BullishGreen else BearishRed,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${if (holdingIsProfit) "+" else ""}${String.format(Locale.US, "%.2f", holdingProfitPct)}%",
                                            color = if (holdingIsProfit) BullishGreen else BearishRed,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(
                                    onClick = { viewModel.deletePortfolioHoldingItem(holding.id) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(BearishRed.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BearishRed, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddHoldingDialog) {
        AddHoldingDialog(
            stocks = allStocks,
            onDismiss = { showAddHoldingDialog = false },
            onConfirm = { symbol, name, qty, buyPrice ->
                viewModel.purchasePortfolioHolding(symbol, name, qty, buyPrice)
                showAddHoldingDialog = false
            }
        )
    }
}

// --- ALERTS PANEL ---
@Composable
fun AlertsPanel(
    viewModel: CreatorViewModel,
    priceAlerts: List<PriceAlert>,
    allStocks: List<StockDetail>
) {
    var showCreateAlertModal by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "REAL-TIME PRICE ALERTS",
                color = GoldGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Button(
                onClick = { showCreateAlertModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldGold),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("create_alert_button")
            ) {
                Icon(Icons.Default.AddAlert, contentDescription = null, tint = JetBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Set Alert", color = JetBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (priceAlerts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = TextGrayMuted.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("No Configured Price Alerts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Avoid constantly checking charts! Set target price conditions, and get instantaneous simulated push alarms.",
                    color = TextGrayMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    lineHeight = 16.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(priceAlerts) { alert ->
                    val latestPrice = allStocks.firstOrNull { it.symbol == alert.symbol }?.currentPrice ?: 0.0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(12.dp))
                            .testTag("alert_item_${alert.id}"),
                        colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (alert.isTriggered) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (alert.isTriggered) GoldGold else TextGrayMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(alert.symbol, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = "Target Price: ₹${alert.targetPrice} (${alert.condition})",
                                        color = TextGrayMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (alert.isTriggered) "TRIGGERED" else "PENDING",
                                        color = if (alert.isTriggered) GoldGold else TechCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Live: ₹${String.format(Locale.US, "%.2f", latestPrice)}",
                                        color = TextGrayMuted,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(
                                    onClick = { viewModel.deletePriceAlertItem(alert.id) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(BearishRed.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BearishRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateAlertModal) {
        CreateAlertDialog(
            stocks = allStocks,
            onDismiss = { showCreateAlertModal = false },
            onConfirm = { symbol, name, target, cond ->
                viewModel.createPriceAlert(symbol, name, target, cond)
                showCreateAlertModal = false
            }
        )
    }
}

// --- SETTINGS PANEL ---
@Composable
fun SettingsPanel(
    viewModel: CreatorViewModel,
    apiKey: String,
    isPremium: Boolean,
    freeScanCount: Int
) {
    var keyInput by remember { mutableStateOf(apiKey) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Gemini REST key section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = GoldGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI GEMINI CONFIGURE",
                            color = GoldGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "Input your Gemini API key from AI Studio to activate intelligent stock scanning, pattern recognitions, news summaries, and predictive breakouts.",
                        color = TextGrayMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        label = { Text("Gemini API Key", color = TextGrayMuted) },
                        placeholder = { Text("Enter API key starting with AIza...", color = TextGrayMuted.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldGold,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = GoldGold
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_key_text_field")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.updateApiKey(keyInput)
                            Toast.makeText(context, "API Key Saved!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_api_key_button")
                    ) {
                        Text("Save Configuration", color = JetBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quota control simulation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "QUOTA SIMULATOR",
                        color = GoldGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        "Simulate quota refreshes or reset subscription status for testing the limits & upgrade walls.",
                        color = TextGrayMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.resetFreeQuota()
                                Toast.makeText(context, "Quota reset to 3 free scans!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LightGlass),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset Quota", color = Color.White, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.buyPremiumUpgrade()
                                Toast.makeText(context, "Upgraded to Premium (Unlimited)!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Upgrade Premium", color = JetBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Broker Referrals / Disclaimer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "BROKER CONNECT (REFERRAL)",
                        color = GoldGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        "Open a free trading account with Zerodha, Groww, or AngelOne using our affiliate partnership links to receive ₹200 brokerage credit!",
                        color = TextGrayMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            Toast.makeText(context, "Redirecting to Broker Partner...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Connect Broker Account", color = JetBlack, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "INVESTMENT DISCLAIMER",
                        color = BearishRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        "Markets are highly volatile and unpredictable. Predictions, scanners, and indicators supplied by AI Stock Scanner are for educational and simulation use cases only. They do NOT constitute guaranteed SEBI or SEC financial advice. Perform your own deep research or query registered advisers before investing capital.",
                        color = TextGrayMuted,
                        fontSize = 10.sp,
                        lineHeight = 15.sp,
                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    )
                }
            }
        }
    }
}

// ======================== MODALS & DIALOGS IMPLEMENTATIONS ========================

@Composable
fun StockDetailModal(
    viewModel: CreatorViewModel,
    stock: StockDetail,
    watchlist: List<WatchlistItem>,
    priceAlerts: List<PriceAlert>,
    portfolioHoldings: List<com.example.data.model.PortfolioHolding>,
    isLoading: Boolean,
    activeAnalysisReport: String?,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val isWatched = watchlist.any { it.symbol == stock.symbol }
    val isUp = stock.changePercent >= 0

    // Form inputs for creating price alert or buying holdings inside detailed modal
    var targetPriceInput by remember { mutableStateOf(String.format(Locale.US, "%.2f", stock.currentPrice * 1.05)) }
    var alertCondition by remember { mutableStateOf("Above") }

    var portfolioQty by remember { mutableStateOf("10") }
    var portfolioPrice by remember { mutableStateOf(String.format(Locale.US, "%.2f", stock.currentPrice)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JetBlack.copy(alpha = 0.92f))
            .clickable(enabled = true, onClick = onClose) // tap backdrop to close
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .align(Alignment.BottomCenter)
                .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clickable(enabled = false) {}, // prevent click-through
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header of detail
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                stock.symbol,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Toggle Watchlist star
                            IconButton(
                                onClick = {
                                    viewModel.toggleWatchlist(stock, isWatched)
                                    val msg = if (isWatched) "Removed from Watchlist" else "Added to Watchlist"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(LightGlass, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isWatched) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Watchlist Toggle",
                                    tint = GoldGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(stock.name, fontSize = 11.sp, color = TextGrayMuted)
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .background(LightGlass, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "₹${String.format(Locale.US, "%.2f", stock.currentPrice)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isUp) Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (isUp) BullishGreen else BearishRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (isUp) "+" else ""}${String.format(Locale.US, "%.2f", stock.changePercent)}%",
                                color = if (isUp) BullishGreen else BearishRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Score Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = JetBlack),
                        border = BorderStroke(1.dp, GoldGold.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("AI CONFIDENCE", color = GoldGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Text("${stock.aiScore}%", color = GoldGold, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Custom Candlestick Drawing
                InteractiveCandleChart(stock = stock)

                Spacer(modifier = Modifier.height(12.dp))

                // Scrolling container for inputs & AI analysis
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Custom technical statistics row
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JetBlack),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(0.5.dp, GlassBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                TechStatBox("Predicted Target", "₹${stock.predictedTarget}", GoldGold)
                                TechStatBox("Stop Loss", "₹${stock.stopLoss}", BearishRed)
                                TechStatBox("RSI (14)", "${stock.rsi}", if (stock.rsi >= 70) BearishRed else if (stock.rsi <= 30) BullishGreen else TechCyan)
                                TechStatBox("Risk Meter", stock.riskLevel, if (stock.riskLevel == "Low") BullishGreen else if (stock.riskLevel == "Medium") GoldGold else BearishRed)
                            }
                        }
                    }

                    // Interactive tool form drawers
                    item {
                        var openToolSegment by remember { mutableStateOf("Alert") }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JetBlack, RoundedCornerShape(8.dp))
                                .padding(2.dp)
                        ) {
                            listOf("Alert", "Paper Trade").forEach { tool ->
                                val active = openToolSegment == tool
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) GoldGold else Color.Transparent)
                                        .clickable { openToolSegment = tool }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        tool,
                                        color = if (active) JetBlack else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (openToolSegment == "Alert") {
                            // Alert creator
                            Card(
                                colors = CardDefaults.cardColors(containerColor = JetBlack),
                                border = BorderStroke(0.5.dp, GlassBorder),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("SET PRICE TARGET TRIGGER", color = GoldGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = targetPriceInput,
                                            onValueChange = { targetPriceInput = it },
                                            label = { Text("Price", color = TextGrayMuted, fontSize = 10.sp) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = GoldGold,
                                                unfocusedBorderColor = GlassBorder,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Condition switcher
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(LightGlass)
                                                .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(8.dp))
                                                .clickable {
                                                    alertCondition = if (alertCondition == "Above") "Below" else "Above"
                                                }
                                                .padding(horizontal = 12.dp, vertical = 14.dp)
                                        ) {
                                            Text(
                                                "Crosses $alertCondition",
                                                color = GoldGold,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                val tgt = targetPriceInput.toDoubleOrNull()
                                                if (tgt != null) {
                                                    viewModel.createPriceAlert(stock.symbol, stock.name, tgt, alertCondition)
                                                    Toast.makeText(context, "Alert configured successfully!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldGold),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(48.dp)
                                        ) {
                                            Text("Save Alert", color = JetBlack, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            // Paper trading creator
                            Card(
                                colors = CardDefaults.cardColors(containerColor = JetBlack),
                                border = BorderStroke(0.5.dp, GlassBorder),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("LOG SIMULATED PURCHASE", color = GoldGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = portfolioQty,
                                            onValueChange = { portfolioQty = it },
                                            label = { Text("Qty", color = TextGrayMuted, fontSize = 10.sp) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = GoldGold,
                                                unfocusedBorderColor = GlassBorder,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(0.8f)
                                        )

                                        OutlinedTextField(
                                            value = portfolioPrice,
                                            onValueChange = { portfolioPrice = it },
                                            label = { Text("Buy Price (₹)", color = TextGrayMuted, fontSize = 10.sp) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = GoldGold,
                                                unfocusedBorderColor = GlassBorder,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1.2f)
                                        )

                                        Button(
                                            onClick = {
                                                val q = portfolioQty.toDoubleOrNull()
                                                val p = portfolioPrice.toDoubleOrNull()
                                                if (q != null && p != null) {
                                                    viewModel.purchasePortfolioHolding(stock.symbol, stock.name, q, p)
                                                    Toast.makeText(context, "Added transaction to Portfolio!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldGold),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(48.dp)
                                        ) {
                                            Text("Log Trade", color = JetBlack, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // AI Generation Trigger or Report Displays
                    item {
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = GoldGold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Generating deep scanner prediction...", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        } else if (activeAnalysisReport != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = JetBlack),
                                border = BorderStroke(0.5.dp, GlassBorder),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("AI DEEP SCANNED REPORT", color = GoldGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldGold, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = activeAnalysisReport!!,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { viewModel.runStockDeepAnalysis(stock) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("run_deep_ai_analysis_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JetBlack)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ask Gemini AI Deep Prediction", color = JetBlack, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TechStatBox(label: String, valStr: String, valColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGrayMuted, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(valStr, color = valColor, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun InteractiveCandleChart(stock: StockDetail) {
    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(-1) }

    val prices = stock.prices
    if (prices.size < 2) return

    val maxPrice = prices.maxOrNull() ?: 1.0
    val minPrice = prices.minOrNull() ?: 0.0
    val delta = if (maxPrice == minPrice) 1.0 else (maxPrice - minPrice)

    Column {
        if (selectedIndex != -1 && selectedIndex < prices.size) {
            val hP = prices[selectedIndex]
            Text(
                text = "Tracking Price Point: ₹${String.format(Locale.US, "%.2f", hP)} | RSI: ${stock.rsi}",
                color = GoldGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )
        } else {
            Text(
                text = "Interactive Chart (Drag/Tap candles to track details)",
                color = TextGrayMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = JetBlack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                val w = size.width
                                val step = w / prices.size
                                val idx = (offset.x / step).toInt().coerceIn(0, prices.size - 1)
                                selectedIndex = idx
                            }
                        )
                    }
                    .padding(vertical = 12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val paddingHorizontal = 10.dp.toPx()
                    val graphWidth = w - (paddingHorizontal * 2)
                    val step = graphWidth / (prices.size - 1)

                    // Draw grid lines
                    for (i in 1..3) {
                        val yLine = (h / 4) * i
                        drawLine(
                            color = GlassBorder.copy(alpha = 0.2f),
                            start = Offset(0f, yLine),
                            end = Offset(w, yLine),
                            strokeWidth = 1f
                        )
                    }

                    // Construct simulated candles
                    prices.forEachIndexed { i, p ->
                        val x = paddingHorizontal + (i * step)
                        val y = h - (((p - minPrice) / delta).toFloat() * h)

                        // Draw candlestick body and wick
                        val candleColor = if (i > 0 && prices[i] >= prices[i - 1]) BullishGreen else BearishRed
                        val wickHeight = h * 0.15f
                        val topWick = y - wickHeight
                        val bottomWick = y + wickHeight

                        // Wick
                        drawLine(
                            color = candleColor,
                            start = Offset(x, topWick.coerceIn(0f, h)),
                            end = Offset(x, bottomWick.coerceIn(0f, h)),
                            strokeWidth = 2f
                        )

                        // Body
                        val bodyH = max(10f, h * 0.2f)
                        drawRect(
                            color = candleColor,
                            topLeft = Offset(x - 8f, y - (bodyH / 2)),
                            size = Size(16f, bodyH)
                        )

                        // Selected Candle Tracker Highlight
                        if (selectedIndex == i) {
                            drawLine(
                                color = GoldGold,
                                start = Offset(x, 0f),
                                end = Offset(x, h),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumUpgradeModal(
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onUpgrade,
                colors = ButtonDefaults.buttonColors(containerColor = GoldGold)
            ) {
                Text("Activate Premium (₹199/m)", color = JetBlack, fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later", color = Color.White)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upgrade to AI Premium", color = Color.White, fontWeight = FontWeight.Black)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "You have exhausted your free daily scanning quota. Upgrade today to unlock elite features:",
                    color = TextGrayMuted,
                    fontSize = 13.sp
                )

                BulletItem("Unlimited AI Swing Screener Predictions")
                BulletItem("Unlimited Sector-specific Financial Catalyst Scans")
                BulletItem("Instant real-time simulated Price Alarm Alerts")
                BulletItem("SEBI Analyst simulator & Advanced Candlestick HUD")

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "Billed monthly at only ₹199 (Standard) or ₹499 (Advanced). Unsubscribe anytime with zero hidden charges.",
                    color = GoldGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = DeepCharcoal,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.border(BorderStroke(1.dp, GoldGold), RoundedCornerShape(20.dp))
    )
}

@Composable
fun BulletItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Check, contentDescription = null, tint = BullishGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AddHoldingDialog(
    stocks: List<StockDetail>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double) -> Unit
) {
    var selectedStockIdx by remember { mutableStateOf(0) }
    var qtyInput by remember { mutableStateOf("10") }
    var priceInput by remember { mutableStateOf(if (stocks.isNotEmpty()) String.format(Locale.US, "%.2f", stocks[0].currentPrice) else "100.00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val s = stocks.getOrNull(selectedStockIdx)
                    val q = qtyInput.toDoubleOrNull()
                    val p = priceInput.toDoubleOrNull()
                    if (s != null && q != null && p != null) {
                        onConfirm(s.symbol, s.name, q, p)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldGold)
            ) {
                Text("Log Trade", color = JetBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        title = { Text("Log Trade Transaction", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Stock Ticker", color = TextGrayMuted, fontSize = 11.sp)

                // Select ticker simple chooser
                Row(modifier = Modifier.fillMaxWidth()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(stocks.size) { index ->
                            val s = stocks[index]
                            val isSel = selectedStockIdx == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) GoldGold else LightGlass)
                                    .clickable {
                                        selectedStockIdx = index
                                        priceInput = String.format(Locale.US, "%.2f", s.currentPrice)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(s.symbol, color = if (isSel) JetBlack else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = qtyInput,
                    onValueChange = { qtyInput = it },
                    label = { Text("Quantity", color = TextGrayMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldGold,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    label = { Text("Purchase Price (₹)", color = TextGrayMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldGold,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        containerColor = DeepCharcoal,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun CreateAlertDialog(
    stocks: List<StockDetail>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String) -> Unit
) {
    var selectedIdx by remember { mutableStateOf(0) }
    var targetInput by remember { mutableStateOf(if (stocks.isNotEmpty()) String.format(Locale.US, "%.2f", stocks[0].currentPrice * 1.05) else "100.00") }
    var conditionInput by remember { mutableStateOf("Above") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val s = stocks.getOrNull(selectedIdx)
                    val t = targetInput.toDoubleOrNull()
                    if (s != null && t != null) {
                        onConfirm(s.symbol, s.name, t, conditionInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldGold)
            ) {
                Text("Set Alert", color = JetBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        title = { Text("Configure Price Alarm", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Stock Ticker", color = TextGrayMuted, fontSize = 11.sp)

                Row(modifier = Modifier.fillMaxWidth()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(stocks.size) { index ->
                            val s = stocks[index]
                            val isSel = selectedIdx == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) GoldGold else LightGlass)
                                    .clickable {
                                        selectedIdx = index
                                        targetInput = String.format(Locale.US, "%.2f", s.currentPrice * 1.05)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(s.symbol, color = if (isSel) JetBlack else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = targetInput,
                    onValueChange = { targetInput = it },
                    label = { Text("Target Price Trigger (₹)", color = TextGrayMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldGold,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Text("Condition Trigger", color = TextGrayMuted, fontSize = 11.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Above", "Below").forEach { cond ->
                        val active = conditionInput == cond
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) GoldGold else LightGlass)
                                .clickable { conditionInput = cond }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Price Crosses $cond",
                                color = if (active) JetBlack else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = DeepCharcoal,
        shape = RoundedCornerShape(16.dp)
    )
}
