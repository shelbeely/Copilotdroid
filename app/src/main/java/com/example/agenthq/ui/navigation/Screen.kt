package com.example.agenthq.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object PullRequests : Screen("pull_requests")
    data object PullRequestDetail : Screen("pull_request/{owner}/{repo}/{number}") {
        fun createRoute(owner: String, repo: String, number: Int) =
            "pull_request/$owner/$repo/$number"
    }
    data object Steering : Screen("steering/{owner}/{repo}/{number}") {
        fun createRoute(owner: String, repo: String, number: Int) =
            "steering/$owner/$repo/$number"
    }
    data object Analytics : Screen("analytics")
    data object SessionDetail : Screen("session/{sessionId}") {
        fun createRoute(sessionId: Long) = "session/$sessionId"
    }
    data object RepositoryPicker : Screen("repository_picker")
    data object Settings : Screen("settings")
}
