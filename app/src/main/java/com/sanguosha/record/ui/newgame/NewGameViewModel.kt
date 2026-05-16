package com.sanguosha.record.ui.newgame

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationServices
import androidx.lifecycle.viewModelScope
import com.sanguosha.record.SanguoshaApp
import com.sanguosha.record.data.entity.Game
import com.sanguosha.record.data.entity.GamePlayer
import com.sanguosha.record.data.entity.Hero
import com.sanguosha.record.data.entity.Player
import com.sanguosha.record.data.repository.GameRepository
import com.sanguosha.record.data.repository.HeroRepository
import com.sanguosha.record.data.repository.PlayerRepository
import com.sanguosha.record.model.Identity
import com.sanguosha.record.util.DateUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class GamePhase {
    SETUP,      // 设置阶段：选择玩家、武将
    PLAYING,    // 对局中：计时进行中
    RESULT      // 记录结果：设置身份 → 选择获胜方
}

data class PlayerSlot(
    val player: Player? = null,
    val hero: Hero? = null,
    val identity: Identity? = null   // 对局结束后才设置
)

data class NewGameUiState(
    val phase: GamePhase = GamePhase.SETUP,
    val setupStep: Int = 1,           // SETUP 子步骤 (1=选玩家, 2=选武将)
    val resultStep: Int = 1,          // RESULT 子步骤 (1=设身份, 2=选获胜方)
    val location: String = "",
    val allPlayers: List<Player> = emptyList(),
    val allHeroes: List<Hero> = emptyList(),
    val selectedPlayerIds: Set<Long> = emptySet(),
    val playerSlots: List<PlayerSlot> = emptyList(),
    // 计时相关
    val startTime: Long = 0L,
    val elapsedSeconds: Long = 0L,
    val isTimerRunning: Boolean = false,
    // 结果相关
    val winnerIdentity: Identity? = null,
    val newPlayerName: String = "",
    val note: String = "",
    val isFetchingLocation: Boolean = false,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
) {
    /** 所有玩家是否都已设置身份 */
    val allIdentitiesSet: Boolean
        get() = playerSlots.isNotEmpty() && playerSlots.all { it.identity != null }

    /** 所有玩家是否都已选择武将 */
    val allHeroesSet: Boolean
        get() = playerSlots.isNotEmpty() && playerSlots.all { it.hero != null }
}

class NewGameViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as SanguoshaApp).database
    private val gameRepo = GameRepository(database.gameDao(), database.gamePlayerDao())
    private val playerRepo = PlayerRepository(database.playerDao(), database.gamePlayerDao())
    private val heroRepo = HeroRepository(database.heroDao())

    private val _uiState = MutableStateFlow(NewGameUiState())
    val uiState: StateFlow<NewGameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                playerRepo.getAllPlayers(),
                heroRepo.getAllHeroes()
            ) { players, heroes ->
                Pair(players, heroes)
            }.collect { (players, heroes) ->
                _uiState.update {
                    it.copy(allPlayers = players, allHeroes = heroes)
                }
            }
        }
    }

    // ========== Setup Phase ==========

    fun updateLocation(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    @Suppress("MissingPermission")
    fun fetchLocation() {
        _uiState.update { it.copy(isFetchingLocation = true) }
        val client = LocationServices.getFusedLocationProviderClient(getApplication())
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val geocoder = Geocoder(getApplication(), java.util.Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val address = addresses?.firstOrNull()
                    val text = when {
                        address == null -> "${location.latitude}, ${location.longitude}"
                        else -> {
                            buildString {
                                address.locality?.let { append(it) }
                                address.subLocality?.let { append(it) }
                                address.featureName?.let {
                                    if (isNotEmpty()) append(" ")
                                    append(it)
                                }
                            }.ifEmpty { "${location.latitude}, ${location.longitude}" }
                        }
                    }
                    _uiState.update { it.copy(location = text, isFetchingLocation = false) }
                } catch (_: Exception) {
                    _uiState.update {
                        it.copy(
                            location = "${location.latitude}, ${location.longitude}",
                            isFetchingLocation = false
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isFetchingLocation = false) }
            }
        }.addOnFailureListener {
            _uiState.update { it.copy(isFetchingLocation = false) }
        }
    }

    fun updateNewPlayerName(name: String) {
        _uiState.update { it.copy(newPlayerName = name) }
    }

    fun togglePlayerSelection(playerId: Long) {
        _uiState.update { state ->
            val newSelected = if (playerId in state.selectedPlayerIds) {
                state.selectedPlayerIds - playerId
            } else {
                state.selectedPlayerIds + playerId
            }
            state.copy(selectedPlayerIds = newSelected)
        }
    }

    fun addNewPlayer(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val existing = playerRepo.getPlayerByName(name.trim())
            if (existing == null) {
                val id = playerRepo.insertPlayer(Player(name = name.trim()))
                _uiState.update {
                    it.copy(
                        selectedPlayerIds = it.selectedPlayerIds + id,
                        newPlayerName = ""
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        selectedPlayerIds = it.selectedPlayerIds + existing.id,
                        newPlayerName = ""
                    )
                }
            }
        }
    }

    /** 从选玩家 → 选武将 */
    fun nextSetupStep() {
        _uiState.update { state ->
            if (state.setupStep == 1) {
                val slots = state.selectedPlayerIds.map { pid ->
                    state.playerSlots.find { it.player?.id == pid }
                        ?: PlayerSlot(player = state.allPlayers.find { it.id == pid })
                }
                state.copy(setupStep = 2, playerSlots = slots)
            } else {
                state
            }
        }
    }

    fun prevSetupStep() {
        _uiState.update { it.copy(setupStep = (it.setupStep - 1).coerceAtLeast(1)) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun updatePlayerHero(index: Int, hero: Hero) {
        _uiState.update { state ->
            // 检测武将是否已被其他玩家选择
            val duplicateIndex = state.playerSlots.indexOfFirst {
                it.hero?.id == hero.id && state.playerSlots.indexOf(it) != index
            }
            if (duplicateIndex >= 0) {
                val otherPlayer = state.playerSlots[duplicateIndex].player?.name ?: "其他玩家"
                return@update state.copy(errorMessage = "${hero.name} 已被 $otherPlayer 选择")
            }
            val newSlots = state.playerSlots.toMutableList()
            if (index in newSlots.indices) {
                newSlots[index] = newSlots[index].copy(hero = hero)
            }
            state.copy(playerSlots = newSlots)
        }
    }

    // ========== Playing Phase (Timer) ==========

    fun startGame() {
        val now = DateUtils.now()
        _uiState.update {
            it.copy(
                phase = GamePhase.PLAYING,
                startTime = now,
                elapsedSeconds = 0,
                isTimerRunning = true
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uiState.update { state ->
                    if (state.isTimerRunning) {
                        state.copy(elapsedSeconds = state.elapsedSeconds + 1)
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun endGame() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                phase = GamePhase.RESULT,
                isTimerRunning = false,
                resultStep = 1
            )
        }
    }

    // ========== Result Phase ==========

    /** 设置某玩家的身份 */
    fun setPlayerIdentity(index: Int, identity: Identity) {
        _uiState.update { state ->
            // 主公身份只能选择一次
            if (identity == Identity.LORD) {
                val existingLordIndex = state.playerSlots.indexOfFirst {
                    it.identity == Identity.LORD && state.playerSlots.indexOf(it) != index
                }
                if (existingLordIndex >= 0) {
                    val lordPlayer = state.playerSlots[existingLordIndex].player?.name ?: "其他玩家"
                    return@update state.copy(errorMessage = "主公身份已被 $lordPlayer 选择")
                }
            }
            val newSlots = state.playerSlots.toMutableList()
            if (index in newSlots.indices) {
                newSlots[index] = newSlots[index].copy(identity = identity)
            }
            state.copy(playerSlots = newSlots)
        }
    }

    /** 从设身份 → 选获胜方 */
    fun nextResultStep() {
        _uiState.update { it.copy(resultStep = 2) }
    }

    fun prevResultStep() {
        _uiState.update { it.copy(resultStep = (it.resultStep - 1).coerceAtLeast(1)) }
    }

    fun setWinnerIdentity(identity: Identity) {
        _uiState.update { it.copy(winnerIdentity = identity) }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun saveGame() {
        val state = _uiState.value
        if (state.winnerIdentity == null) return
        if (!state.allIdentitiesSet) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val game = Game(
                datetime = state.startTime,
                location = state.location.ifBlank { null },
                playerCount = state.playerSlots.size,
                winnerIdentity = state.winnerIdentity.name,
                durationSeconds = state.elapsedSeconds,
                note = state.note.ifBlank { null }
            )

            val gamePlayers = state.playerSlots.map { slot ->
                val isWinner = slot.identity!!.camp == state.winnerIdentity.camp
                GamePlayer(
                    gameId = 0,
                    playerId = slot.player!!.id,
                    heroId = slot.hero!!.id,
                    identity = slot.identity.name,
                    isWinner = isWinner
                )
            }

            gameRepo.saveCompleteGame(game, gamePlayers)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }

    fun resetState() {
        timerJob?.cancel()
        _uiState.value = NewGameUiState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
