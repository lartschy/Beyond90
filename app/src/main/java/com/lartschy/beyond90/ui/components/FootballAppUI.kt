package com.lartschy.beyond90.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*
import com.lartschy.beyond90.ui.screens.FixturesScreen
import com.lartschy.beyond90.ui.screens.HomeScreen
import com.lartschy.beyond90.ui.screens.LeaguesScreen
import com.lartschy.beyond90.ui.screens.ProfileScreen
import com.lartschy.beyond90.ui.screens.TeamsScreen
import com.lartschy.beyond90.ui.screens.UpcomingMatchesScreen

@Composable
fun FootballAppUI() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "fixtures/{leagueId}" // Expecting a leagueId parameter in the route
            ) {
                composable("fixtures/{leagueId}") { backStackEntry ->
                    // Retrieve leagueId from the back stack entry arguments
                    val leagueId = backStackEntry.arguments?.getString("leagueId") ?: "default_league_id"
                    FixturesScreen(navController = navController, leagueId = leagueId)
                }
                composable("leagues") { LeaguesScreen() }
                composable("teams") { TeamsScreen() }
                composable("profile") { ProfileScreen() }
            }
        }
    }
}
