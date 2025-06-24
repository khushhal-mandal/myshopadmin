package com.example.myshoppingadminapp.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myshoppingadminapp.presentation.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreenUI(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavController
) {
    val analyticsResult = viewModel.analyticsResult.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.fetchAnalytics()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Analytics", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            analyticsResult.totalOrders == 0 -> {
                Text(
                    text = "No orders available for analytics.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            else -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AnalyticsCard("Most Sold Category", analyticsResult.mostSoldCategory)
                    AnalyticsCard("Most Profitable Category", analyticsResult.mostProfitableCategory)
                    AnalyticsCard("Total Orders", analyticsResult.totalOrders.toString())
                    AnalyticsCard("Total Revenue", "₹${analyticsResult.totalRevenue}")
                    AnalyticsCard("Avg Order Value", "₹${analyticsResult.averageOrderValue}")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Top 5 Products by Quantity
                Text("Top 5 Products (Quantity)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                analyticsResult.topProductsByQuantity.forEach {
                    AnalyticsCard(it.first, "${it.second} units sold")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Top 5 Products by Revenue
                Text("Top 5 Products (Revenue)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                analyticsResult.topProductsByRevenue.forEach {
                    AnalyticsCard(it.first, "₹${it.second} earned")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Orders Per Day (simple text-based visualization)
                Text("Orders Per Day", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OrdersPerDayChart(ordersPerDay = analyticsResult.ordersPerDay)
            }

        }
    }
}

@Composable
fun AnalyticsCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun OrdersPerDayChart(
    ordersPerDay: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    if (ordersPerDay.isEmpty()) return

    val sortedData = ordersPerDay.toSortedMap()
    val maxOrders = sortedData.values.maxOrNull() ?: 1
    val barColor = MaterialTheme.colorScheme.primary

    val barLabels = sortedData.keys.toList()
    val barValues = sortedData.values.toList()

    val labelCount = barLabels.size
    val labelAngle = if (labelCount > 4) 45f else 0f // Rotate if many labels

    Column(modifier = modifier.padding(8.dp)) {
        Text("Orders Per Day", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)) {

            val barWidth = size.width / (labelCount * 1.5f)
            val xStart = 0f
            val yMax = size.height

            for (i in barValues.indices) {
                val barHeight = (barValues[i] / maxOrders.toFloat()) * yMax
                val left = xStart + i * (barWidth * 1.5f)
                val top = yMax - barHeight

                // Draw bar
                drawRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight)
                )

                // Y-axis line
                drawLine(
                    color = Color.LightGray,
                    start = Offset(left + barWidth / 2, yMax),
                    end = Offset(left + barWidth / 2, top),
                    strokeWidth = 2f
                )
            }

            // Draw Y-axis labels (optional)
            for (i in 0..maxOrders step (maxOrders / 4).coerceAtLeast(1)) {
                val yPos = yMax - (i / maxOrders.toFloat()) * yMax
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        i.toString(),
                        0f,
                        yPos,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 24f
                        }
                    )
                }
            }
        }

        // X-axis labels
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            barLabels.forEach { label ->
                Text(
                    text = label.takeLast(4),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.rotate(labelAngle)
                )
            }
        }
    }
}

