package com.sanguosha.record.ui.gamedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanguosha.record.SanguoshaApp
import com.sanguosha.record.data.dao.GamePlayerInfo
import com.sanguosha.record.data.entity.Game
import com.sanguosha.record.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameDetailUiState(
    val game: Game? = null,
    val players: List<GamePlayerInfo> = emptyList(),
    val isLoading: Boolean = true
)

class GameDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as SanguoshaApp).database
    private val repository = GameRepository(database.gameDao(), database.gamePlayerDao())

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    fun loadGame(gameId: Long) {
        viewModelScope.launch {
            val game = repository.getGameById(gameId)
            val players = repository.getGamePlayerInfo(gameId)
            _uiState.value = GameDetailUiState(
                game = game,
                players = players,
                isLoading = false
            )
        }
    }
}
