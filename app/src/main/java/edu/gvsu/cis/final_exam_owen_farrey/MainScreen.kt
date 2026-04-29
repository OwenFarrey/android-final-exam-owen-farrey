package edu.gvsu.cis.final_exam_owen_farrey.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.gvsu.cis.final_exam_owen_farrey.data.network.CoinTicker
import edu.gvsu.cis.final_exam_owen_farrey.viewmodel.MainViewModel

@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val starredIds by viewModel.starredIds.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Price range search
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.minPrice,
                onValueChange = { viewModel.updatePriceRange(it, uiState.maxPrice) },
                label = { Text("Min Price") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.maxPrice,
                onValueChange = { viewModel.updatePriceRange(uiState.minPrice, it) },
                label = { Text("Max Price") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(onClick = { viewModel.search() }) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sort buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { viewModel.sortByRank() }, modifier = Modifier.weight(1f)) {
                Text("Sort by Rank")
            }
            Button(onClick = { viewModel.sortByPrice() }, modifier = Modifier.weight(1f)) {
                Text("Sort by Price ↓")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to go to starred list (third screen)
        Button(
            onClick = { navController.navigate("starred") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("⭐ View Starred Coins")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredCoins) { coin ->
                        CoinListItem(
                            coin = coin,
                            isStarred = starredIds.contains(coin.id),
                            onClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("selectedCoinId", coin.id)
                                navController.navigate("detail")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CoinListItem(coin: CoinTicker, isStarred: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isStarred) Color(0xFFFFFFCC) else Color.White
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // EXTRA CREDIT #1: Crypto logo
            AsyncImage(
                model = "https://c2.coinlore.com/img/25x25/${coin.id}.png",
                contentDescription = "${coin.name} logo",
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(text = "${coin.name} (${coin.symbol})", fontSize = 16.sp)
                Text(text = "Rank: ${coin.rank}", fontSize = 12.sp, color = Color.Gray)
            }
            Text(text = "$${coin.price_usd}", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            if (isStarred) {
                Icon(Icons.Default.Star, contentDescription = "Starred", tint = Color.Yellow)
            }
        }
    }
}