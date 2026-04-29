package edu.gvsu.cis.final_exam_owen_farrey.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.gvsu.cis.final_exam_owen_farrey.viewmodel.MainViewModel

@Composable
fun StarredScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val starredIds by viewModel.starredIds.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val starredCoins = uiState.allCoins.filter { starredIds.contains(it.id) }

    // Handle system back button
    BackHandler {
        navController.popBackStack()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Simple back button at top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⭐ Starred Coins", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (starredCoins.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No starred coins yet. Go back and star some!")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(starredCoins) { coin ->
                    CoinListItem(
                        coin = coin,
                        isStarred = true,
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