package com.sanguosha.record.ui.players

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanguosha.record.model.Camp
import com.sanguosha.record.model.Identity
import com.sanguosha.record.ui.theme.*
import com.sanguosha.record.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    playerId: Long,
    onBack: () -> Unit,
    viewModel: PlayersViewModel = viewModel()
) {
    val detail by viewModel.playerDetail.collectAsState()

    LaunchedEffect(playerId) {
        viewModel.loadPlayerDetail(playerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(detail.player?.name ?: "武将档案") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (detail.player == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(sanguoshaBackgroundBrush()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 总览卡片
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                                text = detail.player?.name ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 3.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatNumber("${detail.totalGames}", "总场次")
                                StatNumber("${detail.wins}", "胜场")
                                StatNumber(
                                    "${(detail.winRate * 100).toInt()}%",
                                    "胜率",
                                    valueColor = when {
                                        detail.winRate >= 0.6f -> WinColor
                                        detail.winRate >= 0.4f -> MaterialTheme.colorScheme.onSurface
                                        else -> LoseColor
                                    }
                                )
                            }
                        }
                    }
                }

                // 常用武将
                if (detail.heroStats.isNotEmpty()) {
                    item {
                        OrnamentDivider(
                            title = "常用武将",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    items(detail.heroStats) { stat ->
                        val heroWinRate = if (stat.gameCount > 0)
                            (stat.winCount.toFloat() / stat.gameCount * 100).toInt() else 0
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = stat.heroName.take(1),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stat.heroName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${stat.gameCount}场  ·  ${stat.winCount}胜  ·  胜率${heroWinRate}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 身份统计
                if (detail.identityStats.isNotEmpty()) {
                    item {
                        OrnamentDivider(
                            title = "阵营统计",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    items(detail.identityStats) { stat ->
                        val identity = try {
                            Identity.fromName(stat.identity)
                        } catch (_: Exception) {
                            null
                        }
                        val color = when (identity?.camp) {
                            Camp.LORD_CAMP -> LordColor
                            Camp.REBEL_CAMP -> RebelColor
                            Camp.SPY_CAMP -> SpyColor
                            null -> MaterialTheme.colorScheme.onSurface
                        }
                        val identityWinRate = if (stat.gameCount > 0)
                            (stat.winCount.toFloat() / stat.gameCount * 100).toInt() else 0

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp, color.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IdentityBadge(
                                    identityName = identity?.displayName ?: stat.identity,
                                    color = color
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${stat.gameCount}场  ·  ${stat.winCount}胜  ·  胜率${identityWinRate}%",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { if (stat.gameCount > 0) stat.winCount.toFloat() / stat.gameCount else 0f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp),
                                        color = color,
                                        trackColor = color.copy(alpha = 0.12f)
                                    )
                                }
                            }
                        }
                    }
                }

                // 最近对局
                if (detail.recentGames.isNotEmpty()) {
                    item {
                        OrnamentDivider(
                            title = "近期战报",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    items(detail.recentGames) { game ->
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (game.isWinner) Icons.Default.EmojiEvents else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (game.isWinner) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = DateUtils.formatDateTime(game.datetime),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (game.durationSeconds > 0) {
                                            Text(
                                                text = "  ·  ${DateUtils.formatDuration(game.durationSeconds)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${game.heroName}  ·  ${try { Identity.fromName(game.identity).displayName } catch (_: Exception) { game.identity }}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = if (game.isWinner) "胜" else "败",
                                    color = if (game.isWinner) WinColor else LoseColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}
