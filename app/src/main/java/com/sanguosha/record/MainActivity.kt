package com.sanguosha.record

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sanguosha.record.ui.gamedetail.GameDetailScreen
import com.sanguosha.record.ui.heroes.HeroListScreen
import com.sanguosha.record.ui.home.HomeScreen
import com.sanguosha.record.ui.navigation.Screen
import com.sanguosha.record.ui.navigation.SubScreen
import com.sanguosha.record.ui.navigation.bottomNavItems
import com.sanguosha.record.ui.newgame.NewGameScreen
import com.sanguosha.record.ui.players.PlayerDetailScreen
import com.sanguosha.record.ui.players.PlayerListScreen
import com.sanguosha.record.ui.stats.StatsScreen
import com.sanguosha.record.ui.theme.SanguoshaRecordTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SanguoshaRecordTheme {
                MainApp()
            }
        }
    }
}

@Composable
private fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = navBackStackEntry?.destination?.hierarchy?.any {
                                it.route == screen.route
                            } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNewGame = { navController.navigate(Screen.NewGame.route) },
                    onGameClick = { gameId ->
                        navController.navigate(SubScreen.GameDetail.createRoute(gameId))
                    }
                )
            }

            composable(Screen.NewGame.route) {
                NewGameScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Players.route) {
                PlayerListScreen(
                    onPlayerClick = { playerId ->
                        navController.navigate(SubScreen.PlayerDetail.createRoute(playerId))
                    },
                    onHeroManagement = {
                        navController.navigate(SubScreen.HeroList.route)
                    }
                )
            }

            composable(Screen.Stats.route) {
                StatsScreen()
            }

            composable(
                route = SubScreen.PlayerDetail.route,
                arguments = listOf(navArgument("playerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playerId = backStackEntry.arguments?.getLong("playerId") ?: return@composable
                PlayerDetailScreen(
                    playerId = playerId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(SubScreen.HeroList.route) {
                HeroListScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = SubScreen.GameDetail.route,
                arguments = listOf(navArgument("gameId") { type = NavType.LongType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
                GameDetailScreen(
                    gameId = gameId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
