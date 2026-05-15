package com.sanguosha.record.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanguosha.record.SanguoshaApp
import com.sanguosha.record.data.entity.Game
import com.sanguosha.record.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as SanguoshaApp).database
    private val repository = GameRepository(database.gameDao(), database.gamePlayerDao())

    val recentGames: StateFlow<List<Game>> = repository.getRecentGames(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalGames: StateFlow<Int> = repository.getTotalGamesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun deleteGame(game: Game) {
        viewModelScope.launch { repository.deleteGame(game) }
    }
}
