package edu.gvsu.cis.final_exam_owen_farrey.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object CoinApi {
    private const val BASE_URL = "https://api.coinlore.net/api"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getTickers(start: Int = 0, limit: Int = 100): TickerResponse {
        return client.get("$BASE_URL/tickers/") {
            parameter("start", start)
            parameter("limit", limit)
        }.body()
    }
}