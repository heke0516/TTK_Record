package com.sanguosha.record.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 古风装饰分隔线 —— ◆———— 标题 ————◆
 */
@Composable
fun OrnamentDivider(
    title: String,
    modifier: Modifier = Modifier
) {
    val lineColor = MaterialTheme.colorScheme.outlineVariant
    val accentColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val centerY = size.height / 2
                val lineStroke = 1.dp.toPx()
                // 左侧线
                drawLine(lineColor, Offset(0f, centerY), Offset(size.width * 0.3f, centerY), lineStroke)
                // 右侧线
                drawLine(lineColor, Offset(size.width * 0.7f, centerY), Offset(size.width, centerY), lineStroke)
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "◆ ",
            color = accentColor,
            fontSize = 10.sp
        )
        Text(
            text = title,
            color = accentColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            text = " ◆",
            color = accentColor,
            fontSize = 10.sp
        )
    }
}

/**
 * 三国杀风格卡片标题
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧竖线装饰
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraSmall
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 1.sp
        )
    }
}

/**
 * 古风渐变背景刷
 */
@Composable
fun sanguoshaBackgroundBrush(): Brush {
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)

    return Brush.verticalGradient(
        colors = listOf(
            primary,
            surface,
            surfaceVariant.copy(alpha = 0.3f),
            surface
        )
    )
}

/**
 * 主页大标题装饰
 */
@Composable
fun AppTitleBanner(modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val gold = GoldAccent

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部装饰线
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(2.dp)
                .background(gold, shape = MaterialTheme.shapes.extraSmall)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "三 国 杀",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = primary,
            letterSpacing = 8.sp
        )
        Text(
            text = "对 局 记 录",
            style = MaterialTheme.typography.titleMedium,
            color = primary.copy(alpha = 0.7f),
            letterSpacing = 6.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        // 底部装饰线
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(gold.copy(alpha = 0.6f))
            )
            Text(
                text = " ◇ ",
                color = gold,
                fontSize = 8.sp
            )
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(gold.copy(alpha = 0.6f))
            )
        }
    }
}

/**
 * 身份角标装饰
 */
@Composable
fun IdentityBadge(
    identityName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color.copy(alpha = 0.12f),
                shape = MaterialTheme.shapes.extraSmall
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = identityName,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

/**
 * 比分/数据装饰数字
 */
@Composable
fun StatNumber(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            letterSpacing = 1.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
    }
}
