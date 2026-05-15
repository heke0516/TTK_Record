package com.sanguosha.record.data.dao

import androidx.room.*
import com.sanguosha.record.data.entity.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games ORDER BY datetime DESC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games ORDER BY datetime DESC LIMIT :limit")
    fun getRecentGames(limit: Int): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameById(id: Long): Game?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game): Long

    @Update
    suspend fun update(game: Game)

    @Delete
    suspend fun delete(game: Game)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getTotalGames(): Int

    @Query("SELECT COUNT(*) FROM games")
    fun getTotalGamesFlow(): Flow<Int>
}
