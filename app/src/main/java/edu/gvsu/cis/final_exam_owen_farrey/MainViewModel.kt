package edu.gvsu.cis.final_exam_owen_farrey.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.final_exam_owen_farrey.data.database.AppDatabase
import edu.gvsu.cis.final_exam_owen_farrey.data.network.CoinTicker
import edu.gvsu.cis.final_exam_owen_farrey.data.repository.CoinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repository = CoinRepository(db)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _starredIds = MutableStateFlow<Set<String>>(emptySet())
    val starredIds: StateFlow<Set<String>> = _starredIds.asStateFlow()

    init {
        loadCoins()
        loadStarredStatus()
    }

    private fun loadStarredStatus() {
        viewModelScope.launch {
            repository.getAllStarred().collect { starredList ->
                _starredIds.value = starredList.map { it.coinId }.toSet()
                _uiState.value = _uiState.value.copy(starredCoinIds = _starredIds.value)
            }
        }
    }

    fun loadCoins() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val coins = repository.fetchCoins()
                _uiState.value = _uiState.value.copy(
                    allCoins = coins,
                    filteredCoins = applyFilterAndSort(coins, _uiState.value),
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updatePriceRange(min: String, max: String) {
        val newState = _uiState.value.copy(minPrice = min, maxPrice = max)
        _uiState.value = newState
        applyFiltersAndSort()
    }

    fun search() {
        applyFiltersAndSort()
    }

    fun sortByRank() {
        val newState = _uiState.value.copy(sortType = SortType.RANK)
        _uiState.value = newState
        applyFiltersAndSort()
    }

    fun sortByPrice() {
        val newState = _uiState.value.copy(sortType = SortType.PRICE_DESC)
        _uiState.value = newState
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        val current = _uiState.value
        val filtered = applyFilterAndSort(current.allCoins, current)
        _uiState.value = current.copy(filteredCoins = filtered)
    }

    private fun applyFilterAndSort(coins: List<CoinTicker>, state: MainUiState): List<CoinTicker> {
        var result = coins
        val min = state.minPrice.toDoubleOrNull()
        val max = state.maxPrice.toDoubleOrNull()
        if (min != null || max != null) {
            result = result.filter { coin ->
                val price = coin.price_usd.toDoubleOrNull() ?: return@filter false
                (min == null || price >= min) && (max == null || price <= max)
            }
        }
        result = when (state.sortType) {
            SortType.RANK -> result.sortedBy { it.rank }
            SortType.PRICE_DESC -> result.sortedByDescending { it.price_usd.toDoubleOrNull() ?: 0.0 }
        }
        return result
    }

    fun toggleStar(coin: CoinTicker) {
        viewModelScope.launch {
            if (_starredIds.value.contains(coin.id)) {
                repository.removeStarred(coin.id)
            } else {
                repository.addStarred(coin)
            }
        }
    }
}

data class MainUiState(
    val allCoins: List<CoinTicker> = emptyList(),
    val filteredCoins: List<CoinTicker> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val minPrice: String = "",
    val maxPrice: String = "",
    val sortType: SortType = SortType.RANK,
    val starredCoinIds: Set<String> = emptySet()
)

enum class SortType {
    RANK, PRICE_DESC
}