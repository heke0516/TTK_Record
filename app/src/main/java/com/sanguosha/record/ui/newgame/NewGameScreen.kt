package com.sanguosha.record.ui.newgame

import androidx.compose.animation.AnimatedContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanguosha.record.data.entity.Hero
import com.sanguosha.record.data.entity.Player
import com.sanguosha.record.model.Camp
import com.sanguosha.record.model.Identity
import com.sanguosha.record.ui.theme.*
import com.sanguosha.record.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScreen(
    onBack: () -> Unit,
    viewModel: NewGameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetState() }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (uiState.phase) {
                            GamePhase.SETUP -> "新建对局"
                            GamePhase.PLAYING -> "对局进行中"
                            GamePhase.RESULT -> "记录结果"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = uiState.phase,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            label = "phase"
        ) { phase ->
            when (phase) {
                GamePhase.SETUP -> SetupPhase(uiState = uiState, viewModel = viewModel)
                GamePhase.PLAYING -> PlayingPhase(
                    uiState = uiState,
                    onEndGame = viewModel::endGame
                )
                GamePhase.RESULT -> ResultPhase(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}

// ==================== Setup Phase ====================

@Composable
private fun SetupPhase(
    uiState: NewGameUiState,
    viewModel: NewGameViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Step indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepChip(1, "选择玩家", uiState.setupStep)
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            StepChip(2, "选择武将", uiState.setupStep)
        }

        when (uiState.setupStep) {
            1 -> SetupStep1Players(
                allPlayers = uiState.allPlayers,
                selectedIds = uiState.selectedPlayerIds,
                location = uiState.location,
                newPlayerName = uiState.newPlayerName,
                isFetchingLocation = uiState.isFetchingLocation,
                onTogglePlayer = viewModel::togglePlayerSelection,
                onLocationChange = viewModel::updateLocation,
                onNewPlayerNameChange = viewModel::updateNewPlayerName,
                onAddNewPlayer = viewModel::addNewPlayer,
                onFetchLocation = viewModel::fetchLocation,
                onNext = viewModel::nextSetupStep
            )
            2 -> SetupStep2Heroes(
                playerSlots = uiState.playerSlots,
                allHeroes = uiState.allHeroes,
                onUpdateHero = viewModel::updatePlayerHero,
                allHeroesSet = uiState.allHeroesSet,
                onStartGame = viewModel::startGame
            )
        }
    }
}

@Composable
private fun StepChip(step: Int, label: String, currentStep: Int) {
    val isActive = step == currentStep
    val isDone = step < currentStep
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = when {
                isActive -> MaterialTheme.colorScheme.primary
                isDone -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = if (isDone) "✓" else "$step",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                color = if (isActive || isDone) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SetupStep1Players(
    allPlayers: List<Player>,
    selectedIds: Set<Long>,
    location: String,
    newPlayerName: String,
    isFetchingLocation: Boolean,
    onTogglePlayer: (Long) -> Unit,
    onLocationChange: (String) -> Unit,
    onNewPlayerNameChange: (String) -> Unit,
    onAddNewPlayer: (String) -> Unit,
    onFetchLocation: () -> Unit,
    onNext: () -> Unit
) {
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onFetchLocation()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                label = { Text("地点（可选）") },
                placeholder = { Text("如：宿舍、活动室") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                trailingIcon = {
                    if (isFetchingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = {
                            locationPermissionLauncher.launch(
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        }) {
                            Icon(
                                Icons.Default.MyLocation,
                                contentDescription = "获取当前位置",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newPlayerName,
                    onValueChange = onNewPlayerNameChange,
                    label = { Text("快速添加玩家") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledTonalButton(
                    onClick = { onAddNewPlayer(newPlayerName) },
                    enabled = newPlayerName.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }

        item {
            Text(
                text = "选择参战玩家（已选 ${selectedIds.size} 人）",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        if (allPlayers.isEmpty()) {
            item {
                Text(
                    text = "还没有玩家，请先添加",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(allPlayers) { player ->
            val selected = player.id in selectedIds
            FilterChip(
                selected = selected,
                onClick = { onTogglePlayer(player.id) },
                label = {
                    Text(
                        player.name,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = if (selected) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedIds.size >= 2
            ) {
                Text("下一步：选择武将")
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupStep2Heroes(
    playerSlots: List<PlayerSlot>,
    allHeroes: List<Hero>,
    onUpdateHero: (Int, Hero) -> Unit,
    allHeroesSet: Boolean,
    onStartGame: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 可滚动的玩家列表
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "为每位玩家选择武将",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            itemsIndexed(playerSlots) { index, slot ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = slot.player?.name?.take(1) ?: "?",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = slot.player?.name ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        var heroExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = heroExpanded,
                            onExpandedChange = { heroExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = slot.hero?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("武将") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = heroExpanded)
                                },
                                modifier = Modifier
                                    .width(160.dp)
                                    .menuAnchor(),
                                singleLine = true
                            )
                            ExposedDropdownMenu(
                                expanded = heroExpanded,
                                onDismissRequest = { heroExpanded = false }
                            ) {
                                allHeroes.forEach { hero ->
                                    DropdownMenuItem(
                                        text = { Text(hero.name) },
                                        onClick = {
                                            onUpdateHero(index, hero)
                                            heroExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // 固定在底部的按钮
        Button(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = allHeroesSet,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "开始对局",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==================== Playing Phase ====================

@Composable
private fun PlayingPhase(
    uiState: NewGameUiState,
    onEndGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(sanguoshaBackgroundBrush())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 计时器 - 古风卷轴样式
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, GoldAccent.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚔  对 局 中  ⚔",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = DateUtils.formatDuration(uiState.elapsedSeconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 56.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
                if (uiState.location.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "@ ${uiState.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(title = "参战武将")
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.playerSlots) { slot ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 武将首字头像
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = slot.hero?.name?.take(1) ?: "?",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = slot.player?.name ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = slot.hero?.name ?: "",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // 小型计时指示
                        Text(
                            text = DateUtils.formatDuration(uiState.elapsedSeconds),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 结束按钮
        Button(
            onClick = onEndGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "鸣金收兵",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ==================== Result Phase ====================

@Composable
private fun ResultPhase(
    uiState: NewGameUiState,
    viewModel: NewGameViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Step indicator for result phase
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepChip(1, "设置身份", uiState.resultStep)
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            StepChip(2, "选择胜方", uiState.resultStep)
        }

        // Duration summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "持续时间",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = DateUtils.formatDuration(uiState.elapsedSeconds),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "参战人数",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${uiState.playerSlots.size}人",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (uiState.resultStep) {
            1 -> ResultStep1Identities(
                playerSlots = uiState.playerSlots,
                onSetIdentity = viewModel::setPlayerIdentity,
                allIdentitiesSet = uiState.allIdentitiesSet,
                onNext = viewModel::nextResultStep
            )
            2 -> ResultStep2Winner(
                playerSlots = uiState.playerSlots,
                winnerIdentity = uiState.winnerIdentity,
                note = uiState.note,
                onSelectWinner = viewModel::setWinnerIdentity,
                onUpdateNote = viewModel::updateNote,
                isSaving = uiState.isSaving,
                onSave = viewModel::saveGame
            )
        }
    }
}

// ---------- Result Step 1: 每人设置身份 ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultStep1Identities(
    playerSlots: List<PlayerSlot>,
    onSetIdentity: (Int, Identity) -> Unit,
    allIdentitiesSet: Boolean,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "为每位玩家设置身份",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "根据对局中的实际身份选择",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(playerSlots) { index, slot ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = slot.player?.name ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = slot.hero?.name ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        var expanded by remember { mutableStateOf(false) }
                        val currentColor = when (slot.identity?.camp) {
                            Camp.LORD_CAMP -> LordColor
                            Camp.REBEL_CAMP -> RebelColor
                            Camp.SPY_CAMP -> SpyColor
                            null -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            Surface(
                                color = currentColor.copy(alpha = 0.15f),
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.menuAnchor()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clickable { expanded = true }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = slot.identity?.displayName ?: "选择身份",
                                        color = currentColor,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = currentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                Identity.entries.forEach { identity ->
                                    val color = when (identity) {
                                        Identity.LORD -> LordColor
                                        Identity.LOYALIST -> LoyalistColor
                                        Identity.REBEL -> RebelColor
                                        Identity.SPY -> SpyColor
                                    }
                                    DropdownMenuItem(
                                        text = {
                                            Surface(
                                                color = color,
                                                shape = MaterialTheme.shapes.extraSmall
                                            ) {
                                                Text(
                                                    text = identity.displayName,
                                                    modifier = Modifier.padding(
                                                        horizontal = 12.dp,
                                                        vertical = 4.dp
                                                    ),
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },
                                        onClick = {
                                            onSetIdentity(index, identity)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // 固定底部按钮
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                enabled = allIdentitiesSet
            ) {
                Text("下一步：选择胜方")
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

// ---------- Result Step 2: 选择获胜阵营 ----------

@Composable
private fun ResultStep2Winner(
    playerSlots: List<PlayerSlot>,
    winnerIdentity: Identity?,
    note: String,
    onSelectWinner: (Identity) -> Unit,
    onUpdateNote: (String) -> Unit,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    var showNoteDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "选择胜利方",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "选择胜利方阵营，同一阵营的玩家自动标记为胜利",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 3 camp options
            val campOptions = listOf(
                Camp.LORD_CAMP to "主公阵营胜利（主公+忠臣）",
                Camp.REBEL_CAMP to "反贼阵营胜利",
                Camp.SPY_CAMP to "内奸胜利"
            )
            items(campOptions) { (camp, label) ->
                val isSelected = winnerIdentity?.camp == camp
                val campColor = when (camp) {
                    Camp.LORD_CAMP -> LordColor
                    Camp.REBEL_CAMP -> RebelColor
                    Camp.SPY_CAMP -> SpyColor
                }
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val identity = when (camp) {
                                Camp.LORD_CAMP -> Identity.LORD
                                Camp.REBEL_CAMP -> Identity.REBEL
                                Camp.SPY_CAMP -> Identity.SPY
                            }
                            onSelectWinner(identity)
                        },
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) campColor.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, campColor)
                    else CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                val identity = when (camp) {
                                    Camp.LORD_CAMP -> Identity.LORD
                                    Camp.REBEL_CAMP -> Identity.REBEL
                                    Camp.SPY_CAMP -> Identity.SPY
                                }
                                onSelectWinner(identity)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Result preview
            if (winnerIdentity != null) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "对局结果",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(playerSlots) { slot ->
                    val isWinner = slot.identity?.camp == winnerIdentity.camp
                    val identityColor = when (slot.identity?.camp) {
                        Camp.LORD_CAMP -> LordColor
                    Camp.REBEL_CAMP -> RebelColor
                    Camp.SPY_CAMP -> SpyColor
                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isWinner) WinColor.copy(alpha = 0.06f)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isWinner) Icons.Default.EmojiEvents else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isWinner) WinColor
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = slot.player?.name ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = identityColor.copy(alpha = 0.15f),
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text(
                                        text = slot.identity?.displayName ?: "",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        color = identityColor,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = slot.hero?.name ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = if (isWinner) "胜" else "败",
                            color = if (isWinner) WinColor else LoseColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // 固定底部按钮
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 备注选项
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showNoteDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Notes,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "备注",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (note.isNotBlank()) note else "点击添加对局备注",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (note.isNotBlank())
                                MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    if (note.isNotBlank()) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 保存按钮
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = winnerIdentity != null && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    "保存对局记录",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 备注输入对话框
        if (showNoteDialog) {
            var tempNote by remember { mutableStateOf(note) }
            AlertDialog(
                onDismissRequest = { showNoteDialog = false },
                title = { Text("对局备注") },
                text = {
                    OutlinedTextField(
                        value = tempNote,
                        onValueChange = { tempNote = it },
                        placeholder = { Text("记录本局的精彩瞬间、特殊规则等") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        maxLines = 5
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onUpdateNote(tempNote)
                        showNoteDialog = false
                    }) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoteDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}
