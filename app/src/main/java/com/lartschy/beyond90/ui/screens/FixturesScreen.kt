package com.lartschy.beyond90.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lartschy.beyond90.ui.components.BottomNavigationBar
import com.lartschy.beyond90.viewmodel.FootballViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.lartschy.beyond90.data.model.Event

@Composable
fun FixturesScreen(
    navController: NavController,
    leagueId: String // leagueId is passed as a parameter
) {
    // Observe the matches list from the ViewModel
    val footballViewModel: FootballViewModel = hiltViewModel()

    // Collect the matches list from the ViewModel
    val matches by footballViewModel.matches.collectAsState()

    // Show a loading indicator until data is fetched
    val isLoading = matches.isEmpty()

    LaunchedEffect(leagueId) {
        // Fetch matches when the screen is first loaded
        footballViewModel.fetchMatches(leagueId) // Use the passed league ID
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Show loading indicator if the data is being fetched
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            // Display the list of matches
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(matches) { match ->
                    FixturesScreen(match) // Display each match
                }
            }
        }
    }
}

@Composable
fun FixturesScreen(match: com.lartschy.beyond90.data.model.Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = match.matchName, style = MaterialTheme.typography.titleLarge)
            Text(text = "Date: ${match.date} - Time: ${match.time}")
        }
    }
}

