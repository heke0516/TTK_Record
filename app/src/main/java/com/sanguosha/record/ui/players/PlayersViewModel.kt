package com.sanguosha.record.ui.players

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanguosha.record.SanguoshaApp
import com.sanguosha.record.data.dao.HeroStats
import com.sanguosha.record.data.dao.IdentityStats
import com.sanguosha.record.data.dao.PlayerGameDetail
import com.sanguosha.record.data.entity.Player
import com.sanguosha.record.data.repository.PlayerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlayerDetailState(
    val player: Player? = null,
    val totalGames: Int = 0,
    val wins: Int = 0,
    val winRate: Float = 0f,
    val heroStats: List<HeroStats> = emptyList(),
    val identityStats: List<IdentityStats> = emptyList(),
    val recentGames: List<PlayerGameDetail> = emptyList()
)

data class PlayerWithStats(
    val player: Player,
    val totalGames: Int = 0,
    val wins: Int = 0,
    val winRate: Float = 0f
)

class PlayersViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as SanguoshaApp).database
    private val repository = PlayerRepository(database.playerDao(), database.gamePlayerDao())

    val players: StateFlow<List<Player>> = repository.getAllPlayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _playerDetail = MutableStateFlow(PlayerDetailState())
    val playerDetail: StateFlow<PlayerDetailState> = _playerDetail.asStateFlow()

    private val _newPlayerName = MutableStateFlow("")
    val newPlayerName: StateFlow<String> = _newPlayerName.asStateFlow()

    fun updateNewPlayerName(name: String) {
        _newPlayerName.value = name
    }

    fun addPlayer(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertPlayer(Player(name = name.trim()))
            _newPlayerName.value = ""
        }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            repository.deletePlayer(player)
        }
    }

    fun updatePlayer(player: Player, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.updatePlayer(player.copy(name = newName.trim()))
        }
    }

    fun loadPlayerDetail(playerId: Long) {
        viewModelScope.launch {
            val player = repository.getPlayerById(playerId)
            val totalGames = repository.getPlayerTotalGames(playerId)
            val wins = repository.getPlayerWins(playerId)
            val winRate = if (totalGames > 0) wins.toFloat() / totalGames else 0f
            val heroStats = repository.getPlayerHeroStats(playerId)
            val identityStats = repository.getPlayerIdentityStats(playerId)
            val recentGames = repository.getPlayerRecentGames(playerId)

            _playerDetail.value = PlayerDetailState(
                player = player,
                totalGames = totalGames,
                wins = wins,
                winRate = winRate,
                heroStats = heroStats,
                identityStats = identityStats,
                recentGames = recentGames
            )
        }
    }

    fun getPlayerWithStatsFlow(player: Player): Flow<PlayerWithStats> {
        return flow {
            val totalGames = repository.getPlayerTotalGames(player.id)
            val wins = repository.getPlayerWins(player.id)
            val winRate = if (totalGames > 0) wins.toFloat() / totalGames else 0f
            emit(PlayerWithStats(player, totalGames, wins, winRate))
        }
    }
}
