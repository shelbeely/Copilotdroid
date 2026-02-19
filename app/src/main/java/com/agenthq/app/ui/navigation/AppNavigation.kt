package com.agenthq.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.agenthq.app.ui.auth.LoginScreen
import com.agenthq.app.ui.sessions.SessionDetailScreen
import com.agenthq.app.ui.sessions.SessionFeedScreen
import com.agenthq.app.ui.settings.SettingsScreen

object Routes {
    const val LOGIN = "login"
    const val SESSION_FEED = "session_feed"
    const val SESSION_DETAIL = "session_detail/{prId}"
    const val SETTINGS = "settings"

    fun sessionDetail(prId: Long) = "session_detail/$prId"
}

@Composable
fun AppNavigation(
    isAuthenticated: Boolean,
    navController: NavHostController = rememberNavController()
) {
    val startDestination = if (isAuthenticated) Routes.SESSION_FEED else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.SESSION_FEED) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.SESSION_FEED) {
            SessionFeedScreen(
                onSessionClick = { prId ->
                    navController.navigate(Routes.sessionDetail(prId))
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }
        composable(
            route = Routes.SESSION_DETAIL,
            arguments = listOf(navArgument("prId") { type = NavType.LongType })
        ) {
            SessionDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
