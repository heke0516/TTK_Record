package com.sanguosha.record.data.repository

import com.sanguosha.record.data.dao.GameDao
import com.sanguosha.record.data.dao.GamePlayerDao
import com.sanguosha.record.data.entity.Game
import com.sanguosha.record.data.entity.GamePlayer
import com.sanguosha.record.data.dao.GamePlayerInfo
import com.sanguosha.record.data.dao.HeroStats
import com.sanguosha.record.data.dao.IdentityStats
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val gameDao: GameDao,
    private val gamePlayerDao: GamePlayerDao
) {

    fun getAllGames(): Flow<List<Game>> = gameDao.getAllGames()

    fun getRecentGames(limit: Int = 10): Flow<List<Game>> = gameDao.getRecentGames(limit)

    suspend fun getGameById(id: Long): Game? = gameDao.getGameById(id)

    suspend fun insertGame(game: Game): Long = gameDao.insert(game)

    suspend fun updateGame(game: Game) = gameDao.update(game)

    suspend fun deleteGame(game: Game) = gameDao.delete(game)

    suspend fun deleteGameById(id: Long) = gameDao.deleteById(id)

    suspend fun getTotalGames(): Int = gameDao.getTotalGames()

    fun getTotalGamesFlow(): Flow<Int> = gameDao.getTotalGamesFlow()

    // GamePlayer operations
    suspend fun getGamePlayers(gameId: Long): List<GamePlayer> =
        gamePlayerDao.getGamePlayersByGameId(gameId)

    fun getGamePlayersFlow(gameId: Long): Flow<List<GamePlayer>> =
        gamePlayerDao.getGamePlayersByGameIdFlow(gameId)

    suspend fun getGamePlayerInfo(gameId: Long): List<GamePlayerInfo> =
        gamePlayerDao.getGamePlayerInfoByGameId(gameId)

    suspend fun insertGamePlayer(gamePlayer: GamePlayer): Long =
        gamePlayerDao.insert(gamePlayer)

    suspend fun insertGamePlayers(gamePlayers: List<GamePlayer>) =
        gamePlayerDao.insertAll(gamePlayers)

    suspend fun deleteGamePlayersByGameId(gameId: Long) =
        gamePlayerDao.deleteByGameId(gameId)

    suspend fun saveCompleteGame(
        game: Game,
        gamePlayers: List<GamePlayer>
    ): Long {
        val gameId = gameDao.insert(game.copy(playerCount = gamePlayers.size))
        val playersWithGameId = gamePlayers.map { it.copy(gameId = gameId) }
        gamePlayerDao.insertAll(playersWithGameId)
        return gameId
    }

    // Stats
    suspend fun getGlobalHeroStats(limit: Int = 20): List<HeroStats> =
        gamePlayerDao.getGlobalHeroStats(limit)

    suspend fun getGlobalIdentityStats(): List<IdentityStats> =
        gamePlayerDao.getGlobalIdentityStats()
}
