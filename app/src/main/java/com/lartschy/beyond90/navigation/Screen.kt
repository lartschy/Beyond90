package com.lartschy.beyond90.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Main : Screen("main")
    object Leagues : Screen("leagues")
    object Fixtures : Screen("fixtures")
    object Profile : Screen("profile")
}
