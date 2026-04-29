package edu.gvsu.cis.final_exam_owen_farrey.data.network

import kotlinx.serialization.Serializable

@Serializable
data class TickerResponse(
    val data: List<CoinTicker>,
    val info: Info
)

@Serializable
data class CoinTicker(
    val id: String,
    val symbol: String,
    val name: String,
    val nameid: String? = null,
    val rank: Int,
    val price_usd: String,
    val percent_change_1h: String,
    val percent_change_24h: String,
    val percent_change_7d: String,
    val csupply: String?,      // now nullable
    val tsupply: String?,      // now nullable
    val msupply: String? = null
)

@Serializable
data class Info(
    val coins_num: Int,
    val time: Long
)