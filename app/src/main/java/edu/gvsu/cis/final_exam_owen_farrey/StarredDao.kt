package edu.gvsu.cis.final_exam_owen_farrey.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StarredDao {
    @Query("SELECT * FROM starred_coins")
    suspend fun getAll(): List<StarredCoin>

    @Query("SELECT EXISTS(SELECT 1 FROM starred_coins WHERE coinId = :coinId)")
    suspend fun isStarred(coinId: String): Boolean

    @Insert
    suspend fun insert(coin: StarredCoin)

    @Delete
    suspend fun delete(coin: StarredCoin)
}