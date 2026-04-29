package edu.gvsu.cis.final_exam_owen_farrey.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.gvsu.cis.final_exam_owen_farrey.viewmodel.MainViewModel

@Composable
fun DetailScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val coinId = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedCoinId")
    val uiState by viewModel.uiState.collectAsState()
    val starredIds by viewModel.starredIds.collectAsState()

    val coin = uiState.allCoins.find { it.id == coinId }

    if (coin == null || coinId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Coin not found")
        }
        return
    }

    val isStarred = starredIds.contains(coin.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "${coin.name} (${coin.symbol})", fontSize = 24.sp)
        Text(text = "Current Price: $${coin.price_usd}", fontSize = 20.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ChangeWithIcon(label = "1h", change = coin.percent_change_1h)
            ChangeWithIcon(label = "24h", change = coin.percent_change_24h)
            ChangeWithIcon(label = "7d", change = coin.percent_change_7d)
        }

        HorizontalDivider()

        Text(text = "Supply", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Text(text = "Circulating: ${coin.csupply ?: "N/A"}")
        Text(text = "Total: ${coin.tsupply ?: "N/A"}")
        Text(text = "Maximum: ${if (coin.msupply.isNullOrBlank()) "∞" else coin.msupply}")

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { viewModel.toggleStar(coin) }, modifier = Modifier.weight(1f)) {
                Text(if (isStarred) "Unstar" else "Star")
            }
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                Text("Back")
            }
        }
    }
}

@Composable
fun ChangeWithIcon(label: String, change: String) {
    val value = change.toDoubleOrNull() ?: 0.0
    val color = if (value >= 0) Color.Green else Color.Red
    // EXTRA CREDIT #2: Different icons for positive/negative
    val icon = if (value >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
            Text(text = "$change%", fontSize = 16.sp, color = color)
        }
    }
}