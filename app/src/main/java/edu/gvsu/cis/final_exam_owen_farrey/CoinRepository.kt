package edu.gvsu.cis.final_exam_owen_farrey.data.repository

import edu.gvsu.cis.final_exam_owen_farrey.data.database.AppDatabase
import edu.gvsu.cis.final_exam_owen_farrey.data.database.StarredCoin
import edu.gvsu.cis.final_exam_owen_farrey.data.network.CoinApi
import edu.gvsu.cis.final_exam_owen_farrey.data.network.CoinTicker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CoinRepository(private val db: AppDatabase) {
    suspend fun fetchCoins(): List<CoinTicker> {
        return CoinApi.getTickers(limit = 100).data
    }

    suspend fun addStarred(coin: CoinTicker) {
        val starred = StarredCoin(
            coinId = coin.id,
            name = coin.name,
            symbol = coin.symbol,
            priceUsd = coin.price_usd,
            rank = coin.rank
        )
        db.starredDao().insert(starred)
    }

    suspend fun removeStarred(coinId: String) {
        val coin = db.starredDao().getAll().find { it.coinId == coinId }
        coin?.let { db.starredDao().delete(it) }
    }

    fun getAllStarred(): Flow<List<StarredCoin>> = flow {
        while (true) {
            emit(db.starredDao().getAll())
            kotlinx.coroutines.delay(300)
        }
    }
}