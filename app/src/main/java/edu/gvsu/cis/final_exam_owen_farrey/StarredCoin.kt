package edu.gvsu.cis.final_exam_owen_farrey.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "starred_coins")
data class StarredCoin(
    @PrimaryKey
    val coinId: String,
    val name: String,
    val symbol: String,
    val priceUsd: String,
    val rank: Int
)