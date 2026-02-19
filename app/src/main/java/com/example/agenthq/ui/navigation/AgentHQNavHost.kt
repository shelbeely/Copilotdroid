package com.example.agenthq.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.agenthq.ui.screens.analytics.AnalyticsScreen
import com.example.agenthq.ui.screens.dashboard.DashboardScreen
import com.example.agenthq.ui.screens.pullrequest.PullRequestsScreen
import com.example.agenthq.ui.screens.pullrequest.PullRequestDetailScreen
import com.example.agenthq.ui.screens.steering.SteeringScreen

@Composable
fun AgentHQNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
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
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToPRs = {
                        navController.navigate(Screen.PullRequests.route)
                    }
                )
            }
            composable(Screen.PullRequests.route) {
                PullRequestsScreen(
                    onPRClick = { owner, repo, number ->
                        navController.navigate(
                            Screen.PullRequestDetail.createRoute(owner, repo, number)
                        )
                    }
                )
            }
            composable(Screen.PullRequestDetail.route) { backStackEntry ->
                val owner = backStackEntry.arguments?.getString("owner") ?: ""
                val repo = backStackEntry.arguments?.getString("repo") ?: ""
                val number = backStackEntry.arguments?.getString("number")?.toIntOrNull() ?: 0
                PullRequestDetailScreen(
                    owner = owner,
                    repo = repo,
                    prNumber = number,
                    onNavigateToSteering = {
                        navController.navigate(
                            Screen.Steering.createRoute(owner, repo, number)
                        )
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Steering.route) { backStackEntry ->
                val owner = backStackEntry.arguments?.getString("owner") ?: ""
                val repo = backStackEntry.arguments?.getString("repo") ?: ""
                val number = backStackEntry.arguments?.getString("number")?.toIntOrNull() ?: 0
                SteeringScreen(
                    owner = owner,
                    repo = repo,
                    prNumber = number,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
        }
    }
}
