package com.lartschy.beyond90.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.lartschy.beyond90.data.model.Team
import com.lartschy.beyond90.ui.components.MatchCard
import com.lartschy.beyond90.viewmodel.FixtureViewModel

@Composable
fun FixtureScreen(
    fixtureViewModel: FixtureViewModel = hiltViewModel()
) {
    val leagues by fixtureViewModel.leagues.collectAsState()
    val teams by fixtureViewModel.teamsForSelectedLeague.collectAsState()
    val isLoading by fixtureViewModel.isLoading.collectAsState()
    val matchResults by fixtureViewModel.matchResults.collectAsState()

    val selectedLeague = remember { mutableStateOf<String?>(null) }
    val selectedTeam1 = remember { mutableStateOf<Team?>(null) }
    val selectedTeam2 = remember { mutableStateOf<Team?>(null) }

    val expandedLeague = remember { mutableStateOf(false) }
    val expandedTeam1 = remember { mutableStateOf(false) }
    val expandedTeam2 = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // League selection (Full width with horizontal padding)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            OutlinedButton(
                onClick = { expandedLeague.value = !expandedLeague.value },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = selectedLeague.value ?: "Select League")
            }
            DropdownMenu(
                expanded = expandedLeague.value,
                onDismissRequest = { expandedLeague.value = false }
            ) {
                leagues.forEach { league ->
                    DropdownMenuItem(
                        text = { Text(league.strLeague) },
                        onClick = {
                            selectedLeague.value = league.strLeague
                            fixtureViewModel.fetchTeamsForLeague(league.strLeague)
                            selectedTeam1.value = null
                            selectedTeam2.value = null
                            expandedLeague.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Teams selection
        if (selectedLeague.value != null) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Team 1 Selector
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { expandedTeam1.value = !expandedTeam1.value },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = selectedTeam1.value?.strTeam ?: "Select Team 1")
                        }
                        DropdownMenu(
                            expanded = expandedTeam1.value,
                            onDismissRequest = { expandedTeam1.value = false }
                        ) {
                            teams.forEach { team ->
                                DropdownMenuItem(
                                    text = { Text(team.strTeam) },
                                    onClick = {
                                        selectedTeam1.value = team
                                        fixtureViewModel.setSelectedTeam1(team)
                                        if (selectedTeam2.value?.strTeam == team.strTeam) {
                                            selectedTeam2.value = null
                                        }
                                        expandedTeam1.value = false
                                    }
                                )
                            }
                        }
                    }

                    // Team 2 Selector
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { expandedTeam2.value = !expandedTeam2.value },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = selectedTeam2.value?.strTeam ?: "Select Team 2")
                        }
                        DropdownMenu(
                            expanded = expandedTeam2.value,
                            onDismissRequest = { expandedTeam2.value = false }
                        ) {
                            teams.forEach { team ->
                                if (team.strTeam != selectedTeam1.value?.strTeam) {
                                    DropdownMenuItem(
                                        text = { Text(team.strTeam) },
                                        onClick = {
                                            selectedTeam2.value = team
                                            fixtureViewModel.setSelectedTeam2(team)
                                            expandedTeam2.value = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Fetch button
        Button(
            onClick = { fixtureViewModel.fetchMatchDataIfReady() },
            enabled = selectedTeam1.value != null && selectedTeam2.value != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(text = "Fetch Matches")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results
        if (matchResults.isNotEmpty()) {
            Text(
                text = "Matches found: ${matchResults.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                items(matchResults) { match ->
                    MatchCard(match)
                }
            }
        } else if (!isLoading && selectedTeam1.value != null && selectedTeam2.value != null) {
            Text(
                text = "No matches found.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
            )
        }
    }
}



