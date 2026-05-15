package com.sanguosha.record.data.dao

import androidx.room.*
import com.sanguosha.record.data.entity.GamePlayer
import kotlinx.coroutines.flow.Flow

data class HeroStats(
    val heroId: Long,
    val heroName: String,
    val gameCount: Int,
    val winCount: Int
)

data class IdentityStats(
    val identity: String,
    val gameCount: Int,
    val winCount: Int
)

data class PlayerGameDetail(
    val gamePlayerId: Long,
    val gameId: Long,
    val datetime: Long,
    val location: String?,
    val heroName: String,
    val identity: String,
    val isWinner: Boolean,
    val winnerIdentity: String,
    val durationSeconds: Long
)

data class GamePlayerInfo(
    val playerId: Long,
    val playerName: String,
    val heroName: String,
    val identity: String,
    val isWinner: Boolean
)

@Dao
interface GamePlayerDao {

    @Query("SELECT * FROM game_players WHERE gameId = :gameId")
    suspend fun getGamePlayersByGameId(gameId: Long): List<GamePlayer>

    @Query("""
        SELECT gp.playerId, p.name as playerName, h.name as heroName,
               gp.identity, gp.isWinner
        FROM game_players gp
        INNER JOIN players p ON gp.playerId = p.id
        INNER JOIN heroes h ON gp.heroId = h.id
        WHERE gp.gameId = :gameId
    """)
    suspend fun getGamePlayerInfoByGameId(gameId: Long): List<GamePlayerInfo>

    @Query("SELECT * FROM game_players WHERE gameId = :gameId")
    fun getGamePlayersByGameIdFlow(gameId: Long): Flow<List<GamePlayer>>

    @Query("SELECT * FROM game_players WHERE playerId = :playerId")
    fun getGamePlayersByPlayerId(playerId: Long): Flow<List<GamePlayer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gamePlayer: GamePlayer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gamePlayers: List<GamePlayer>)

    @Delete
    suspend fun delete(gamePlayer: GamePlayer)

    @Query("DELETE FROM game_players WHERE gameId = :gameId")
    suspend fun deleteByGameId(gameId: Long)

    // 玩家总场次
    @Query("SELECT COUNT(*) FROM game_players WHERE playerId = :playerId")
    suspend fun getPlayerTotalGames(playerId: Long): Int

    // 玩家胜场
    @Query("SELECT COUNT(*) FROM game_players WHERE playerId = :playerId AND isWinner = 1")
    suspend fun getPlayerWins(playerId: Long): Int

    // 玩家常用武将 TOP N
    @Query("""
        SELECT gp.heroId, h.name as heroName, COUNT(*) as gameCount,
               SUM(CASE WHEN gp.isWinner = 1 THEN 1 ELSE 0 END) as winCount
        FROM game_players gp
        INNER JOIN heroes h ON gp.heroId = h.id
        WHERE gp.playerId = :playerId
        GROUP BY gp.heroId
        ORDER BY gameCount DESC
        LIMIT :limit
    """)
    suspend fun getPlayerHeroStats(playerId: Long, limit: Int = 5): List<HeroStats>

    // 玩家各身份统计
    @Query("""
        SELECT identity,
               COUNT(*) as gameCount,
               SUM(CASE WHEN isWinner = 1 THEN 1 ELSE 0 END) as winCount
        FROM game_players
        WHERE playerId = :playerId
        GROUP BY identity
    """)
    suspend fun getPlayerIdentityStats(playerId: Long): List<IdentityStats>

    // 玩家最近对局详情
    @Query("""
        SELECT gp.id as gamePlayerId, gp.gameId, g.datetime, g.location,
               h.name as heroName, gp.identity, gp.isWinner, g.winnerIdentity,
               g.durationSeconds
        FROM game_players gp
        INNER JOIN games g ON gp.gameId = g.id
        INNER JOIN heroes h ON gp.heroId = h.id
        WHERE gp.playerId = :playerId
        ORDER BY g.datetime DESC
        LIMIT :limit
    """)
    suspend fun getPlayerRecentGames(playerId: Long, limit: Int = 20): List<PlayerGameDetail>

    // 武将使用次数统计（全局）
    @Query("""
        SELECT gp.heroId, h.name as heroName, COUNT(*) as gameCount,
               SUM(CASE WHEN gp.isWinner = 1 THEN 1 ELSE 0 END) as winCount
        FROM game_players gp
        INNER JOIN heroes h ON gp.heroId = h.id
        GROUP BY gp.heroId
        ORDER BY gameCount DESC
        LIMIT :limit
    """)
    suspend fun getGlobalHeroStats(limit: Int = 20): List<HeroStats>

    // 身份胜率统计（全局，按对局数而非玩家数统计）
    @Query("""
        SELECT identity,
               COUNT(DISTINCT gameId) as gameCount,
               COUNT(DISTINCT CASE WHEN isWinner = 1 THEN gameId END) as winCount
        FROM game_players
        GROUP BY identity
    """)
    suspend fun getGlobalIdentityStats(): List<IdentityStats>
}
