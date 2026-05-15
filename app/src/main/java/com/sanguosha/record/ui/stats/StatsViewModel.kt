package com.sanguosha.record.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanguosha.record.SanguoshaApp
import com.sanguosha.record.data.dao.HeroStats
import com.sanguosha.record.data.dao.IdentityStats
import com.sanguosha.record.data.entity.Player
import com.sanguosha.record.data.repository.GameRepository
import com.sanguosha.record.data.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class PlayerRanking(
    val player: Player,
    val totalGames: Int,
    val wins: Int,
    val winRate: Float
)

data class StatsUiState(
    val playerRankings: List<PlayerRanking> = emptyList(),
    val heroStats: List<HeroStats> = emptyList(),
    val identityStats: List<IdentityStats> = emptyList(),
    val isLoading: Boolean = true
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as SanguoshaApp).database
    private val gameRepo = GameRepository(database.gameDao(), database.gamePlayerDao())
    private val playerRepo = PlayerRepository(database.playerDao(), database.gamePlayerDao())

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val players = mutableListOf<PlayerRanking>()

            // Get all players and their stats
            playerRepo.getAllPlayers().collect { playerList ->
                players.clear()
                for (player in playerList) {
                    val total = playerRepo.getPlayerTotalGames(player.id)
                    val wins = playerRepo.getPlayerWins(player.id)
                    val rate = if (total > 0) wins.toFloat() / total else 0f
                    players.add(PlayerRanking(player, total, wins, rate))
                }

                val heroStats = gameRepo.getGlobalHeroStats(20)
                val identityStats = gameRepo.getGlobalIdentityStats()

                _uiState.value = StatsUiState(
                    playerRankings = players.sortedByDescending { it.winRate },
                    heroStats = heroStats.sortedByDescending {
                        if (it.gameCount > 0) it.winCount.toFloat() / it.gameCount else 0f
                    },
                    identityStats = identityStats,
                    isLoading = false
                )
            }
        }
    }
}
