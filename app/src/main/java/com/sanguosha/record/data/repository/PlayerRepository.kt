package com.sanguosha.record.data.repository

import com.sanguosha.record.data.dao.GamePlayerDao
import com.sanguosha.record.data.dao.HeroStats
import com.sanguosha.record.data.dao.IdentityStats
import com.sanguosha.record.data.dao.PlayerDao
import com.sanguosha.record.data.dao.PlayerGameDetail
import com.sanguosha.record.data.entity.Player
import kotlinx.coroutines.flow.Flow

class PlayerRepository(
    private val playerDao: PlayerDao,
    private val gamePlayerDao: GamePlayerDao
) {

    fun getAllPlayers(): Flow<List<Player>> = playerDao.getAllPlayers()

    suspend fun getPlayerById(id: Long): Player? = playerDao.getPlayerById(id)

    suspend fun getPlayerByName(name: String): Player? = playerDao.getPlayerByName(name)

    suspend fun insertPlayer(player: Player): Long = playerDao.insert(player)

    suspend fun updatePlayer(player: Player) = playerDao.update(player)

    suspend fun deletePlayer(player: Player) = playerDao.delete(player)

    suspend fun deletePlayerById(id: Long) = playerDao.deleteById(id)

    suspend fun getPlayerTotalGames(playerId: Long): Int =
        gamePlayerDao.getPlayerTotalGames(playerId)

    suspend fun getPlayerWins(playerId: Long): Int =
        gamePlayerDao.getPlayerWins(playerId)

    suspend fun getPlayerWinRate(playerId: Long): Float {
        val total = getPlayerTotalGames(playerId)
        if (total == 0) return 0f
        val wins = getPlayerWins(playerId)
        return wins.toFloat() / total
    }

    suspend fun getPlayerHeroStats(playerId: Long, limit: Int = 5): List<HeroStats> =
        gamePlayerDao.getPlayerHeroStats(playerId, limit)

    suspend fun getPlayerIdentityStats(playerId: Long): List<IdentityStats> =
        gamePlayerDao.getPlayerIdentityStats(playerId)

    suspend fun getPlayerRecentGames(playerId: Long, limit: Int = 20): List<PlayerGameDetail> =
        gamePlayerDao.getPlayerRecentGames(playerId, limit)
}
