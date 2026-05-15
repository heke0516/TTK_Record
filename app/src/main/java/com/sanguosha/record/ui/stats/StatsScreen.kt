package com.sanguosha.record.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(sanguoshaBackgroundBrush()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "战 绩 统 计",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 4.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 玩家胜率排行
        if (uiState.playerRankings.isNotEmpty()) {
            item {
                OrnamentDivider(
                    title = "玩家榜",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            itemsIndexed(uiState.playerRankings.filter { it.totalGames > 0 }) { index, ranking ->
                val rankColor = when (index) {
                    0 -> GoldAccent
                    1 -> MaterialTheme.colorScheme.onSurfaceVariant
                    2 -> MaterialTheme.colorScheme.tertiary
                    else -> null
                }
                val rankLabel = when (index) {
                    0 -> "壹"
                    1 -> "贰"
                    2 -> "叁"
                    else -> "${index + 1}"
                }

                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    border = if (index < 3) androidx.compose.foundation.BorderStroke(
                        1.dp, rankColor!!.copy(alpha = 0.3f)
                    ) else CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 排名
                        Surface(
                            color = rankColor?.copy(alpha = 0.12f)
                                ?: MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = rankLabel,
                                    color = rankColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ranking.player.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${ranking.totalGames}场  ·  ${ranking.wins}胜",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // 胜率
                        Text(
                            text = "${(ranking.winRate * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                ranking.winRate >= 0.6f -> WinColor
                                ranking.winRate >= 0.4f -> MaterialTheme.colorScheme.onSurface
                                else -> LoseColor
                            }
                        )
                    }
                }
            }
        }

        // 武将使用排行
        if (uiState.heroStats.isNotEmpty()) {
            item {
                OrnamentDivider(
                    title = "武将榜",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(uiState.heroStats.take(8)) { stat ->
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
                        // 武将首字
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
                                text = "${stat.gameCount}场  ·  ${stat.winCount}胜",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${heroWinRate}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 身份胜率统计
        if (uiState.identityStats.isNotEmpty()) {
            item {
                OrnamentDivider(
                    title = "阵营榜",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(uiState.identityStats) { stat ->
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
                        1.dp, color.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 阵营标识
                        Surface(
                            color = color.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = identity?.displayName ?: stat.identity,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = color,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${stat.gameCount}场  ·  ${stat.winCount}胜",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            LinearProgressIndicator(
                                progress = { if (stat.gameCount > 0) stat.winCount.toFloat() / stat.gameCount else 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .padding(top = 4.dp),
                                color = color,
                                trackColor = color.copy(alpha = 0.15f)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${identityWinRate}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                }
            }
        }

        // 空状态
        if (uiState.playerRankings.isEmpty() && uiState.heroStats.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⚔",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "尚无战绩",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "完成对局后将在此展示统计",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
