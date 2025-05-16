package com.lartschy.beyond90.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.lartschy.beyond90.ui.screens.FixtureScreen
import com.lartschy.beyond90.ui.screens.HomeScreen
import com.lartschy.beyond90.ui.screens.LeagueScreen
import com.lartschy.beyond90.ui.screens.ProfileScreen
import com.lartschy.beyond90.ui.screens.RegistrationScreen
import com.lartschy.beyond90.ui.screens.TeamScreen
import com.lartschy.beyond90.viewmodel.FixtureViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FootballAppUI() {
    val navController = rememberNavController()
    val fixtureViewModel: FixtureViewModel = viewModel()

    Scaffold(
        bottomBar = {
            if (navController.currentBackStackEntryAsState().value?.destination?.route != "home" && navController.currentBackStackEntryAsState().value?.destination?.route != "registration") {
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
                startDestination = "home"
            ) {
                composable("home") { HomeScreen(navController) }

                composable("registration") { RegistrationScreen(navController) }

                composable("leagues") { LeagueScreen(navController) }

                composable("teams/{leagueName}") { backStackEntry ->
                    val leagueName = backStackEntry.arguments?.getString("leagueName") ?: ""
                    TeamScreen(leagueName = leagueName)
                }

                composable("fixtures") {
                    FixtureScreen(fixtureViewModel)
                }

                composable("profile") { ProfileScreen(navController) }
            }

        }
    }
}