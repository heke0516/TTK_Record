package com.sanguosha.record.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "主页", Icons.Default.Home)
    data object NewGame : Screen("new_game", "新建对局", Icons.Default.Add)
    data object Players : Screen("players", "玩家", Icons.Default.People)
    data object Stats : Screen("stats", "统计", Icons.Default.BarChart)
}

sealed class SubScreen(val route: String) {
    data object PlayerDetail : SubScreen("player_detail/{playerId}") {
        fun createRoute(playerId: Long) = "player_detail/$playerId"
    }
    data object HeroList : SubScreen("hero_list")
    data object GameDetail : SubScreen("game_detail/{gameId}") {
        fun createRoute(gameId: Long) = "game_detail/$gameId"
    }
}

val bottomNavItems = listOf(Screen.Home, Screen.Players, Screen.Stats)
