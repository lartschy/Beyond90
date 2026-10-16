package com.lartschy.beyond90.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.ui.screen.PredictionScreen
import com.lartschy.beyond90.ui.screens.FixturesScreen
import com.lartschy.beyond90.ui.screens.HomeScreen
import com.lartschy.beyond90.ui.screens.LeaguePredictionScreen

import com.lartschy.beyond90.ui.screens.LeagueScreen
import com.lartschy.beyond90.ui.screens.RegistrationScreen
import com.lartschy.beyond90.ui.screens.TeamScreen
import com.lartschy.beyond90.ui.screens.UpcomingScreen
import com.lartschy.beyond90.viewmodel.FixtureViewModel
import com.lartschy.beyond90.viewmodel.LeagueViewModel
import com.lartschy.beyond90.viewmodel.UpcomingViewModel
import com.lartschy.beyond90.ui.screens.ProfileScreen
import com.lartschy.beyond90.viewmodel.PredictionViewModel

@SuppressLint("StateFlowValueCalledInComposition", "ViewModelConstructorInComposable")
@Composable
fun FootballAppUI() {
    val navController = rememberNavController()

    val auth = FirebaseAuth.getInstance()

    val startDestination = if (auth.currentUser != null) {
        "leagues"
    } else {
        "home"
    }

    Scaffold(
        bottomBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route

            if (currentRoute != "home" && currentRoute != "registration") {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "leagues"
            ) {
                composable("home") {
                    HomeScreen(navController)
                }

                composable("registration") {
                    RegistrationScreen(navController)
                }

                composable("leagues") {
                    val viewModel: LeagueViewModel = hiltViewModel()
                    LeagueScreen(navController, viewModel)
                }

                composable("teams/{leagueId}") { backStackEntry ->
                    val leagueId = backStackEntry.arguments?.getString("leagueId") ?: ""
                    TeamScreen(navController, leagueId)
                }

                composable("fixtures") {
                    FixturesScreen(navController = navController)
                }

                composable(
                    route = "upcoming/{homeTeam}/{awayTeam}",
                    arguments = listOf(
                        navArgument("homeTeam") { type = NavType.StringType },
                        navArgument("awayTeam") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val homeTeam = backStackEntry.arguments?.getString("homeTeam") ?: ""
                    val awayTeam = backStackEntry.arguments?.getString("awayTeam") ?: ""

                    UpcomingScreen(homeTeam = homeTeam, awayTeam = awayTeam)
                }

                composable("league_prediction/{leagueId}") { backStackEntry ->
                    val leagueId = backStackEntry.arguments?.getString("leagueId") ?: ""

                    val viewModel: LeagueViewModel = hiltViewModel()

                    LeaguePredictionScreen(
                        leagueId = leagueId,
                        viewModel = viewModel
                    )
                }

                composable("prediction") {
                    PredictionScreen(
                        navController = navController,
                        viewModel = hiltViewModel()
                    )
                }

                composable("profile") {
                    ProfileScreen(navController)
                }
            }
        }
    }
}
