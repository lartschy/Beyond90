package com.lartschy.beyond90.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.lartschy.beyond90.data.model.League
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.Team
import com.lartschy.beyond90.ui.components.MatchCard
import com.lartschy.beyond90.viewmodel.FixtureViewModel
import com.lartschy.beyond90.viewmodel.MatchFilter
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun FixturesScreen(
    navController: NavController,
    viewModel: FixtureViewModel = hiltViewModel()
) {
    val leagues by viewModel.leagues.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val matches by viewModel.matchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val selectedLeague by viewModel.selectedLeague.collectAsState()
    val selectedTeam1 by viewModel.selectedTeam1.collectAsState()
    val selectedTeam2 by viewModel.selectedTeam2.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF2E7D32), Color(0xFF66BB6A)))
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectorWhiteText(
                    placeholder = "Select League",
                    options = leagues.map { it.strLeague },
                    selected = selectedLeague?.strLeague,
                    onSelected = { league ->
                        leagues.find { it.strLeague == league }?.let {
                            viewModel.selectedLeague.value = it
                            viewModel.fetchTeamsForLeague(it)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                FilterDropdownWhite(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { viewModel.selectedFilter.value = it; viewModel.applyFilter() },
                    modifier = Modifier.width(120.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectorWhiteText(
                    placeholder = if (selectedLeague == null) "League first" else "Select Team",
                    options = if (selectedLeague != null) teams.map { it.strTeam } else emptyList(),
                    selected = selectedTeam1?.strTeam,
                    onSelected = { team ->
                        teams.find { it.strTeam == team }?.let { viewModel.selectedTeam1.value = it }
                    },
                    modifier = Modifier.weight(1f)
                )

                SelectorWhiteText(
                    placeholder = when {
                        selectedLeague == null -> "League first"
                        selectedTeam1 == null -> "Team first"
                        else -> "Select Opponent"
                    },
                    options = if (selectedTeam1 != null) teams.filter { it != selectedTeam1 }.map { it.strTeam } else emptyList(),
                    selected = selectedTeam2?.strTeam,
                    onSelected = { team ->
                        teams.find { it.strTeam == team }?.let { viewModel.selectedTeam2.value = it }
                    },
                    modifier = Modifier.weight(1f)
                )
            }


        }
        GradientButtonWhite(
            text = "Search Matches",
            enabled = selectedTeam1 != null && selectedTeam2 != null
        ) { viewModel.fetchMatches() }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        val matches by viewModel.matchResults.collectAsState()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            items(matches) { match ->
                MatchCard(match = match) { selectedMatch ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("matchId", selectedMatch.idEvent)
                    navController.navigate(
                        "upcoming/${Uri.encode(match.strHomeTeam)}/${Uri.encode(match.strAwayTeam)}"
                    )

                }


            }

        }


    }
}

@Composable
fun SelectorWhiteText(
    placeholder: String,
    options: List<String>,
    selected: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.height(48.dp)) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selected ?: placeholder,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF2E7D32))
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = { onSelected(option); expanded = false },
                    text = { Text(option, color = Color(0xFF2E7D32)) }
                )
            }
        }
    }
}

@Composable
fun FilterDropdownWhite(
    selectedFilter: MatchFilter,
    onFilterSelected: (MatchFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(MatchFilter.ALL, MatchFilter.UPCOMING, MatchFilter.HISTORY)
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.height(48.dp)) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (selectedFilter) {
                        MatchFilter.ALL -> "All"
                        MatchFilter.UPCOMING -> "Upcoming"
                        MatchFilter.HISTORY -> "History"
                    },
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF2E7D32))
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { filter ->
                DropdownMenuItem(
                    onClick = { onFilterSelected(filter); expanded = false },
                    text = {
                        Text(
                            text = when (filter) {
                                MatchFilter.ALL -> "All"
                                MatchFilter.UPCOMING -> "Upcoming"
                                MatchFilter.HISTORY -> "History"
                            },
                            color = Color(0xFF2E7D32)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun GradientButtonWhite(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, Color(0xFF2E7D32)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF2E7D32),
            disabledContentColor = Color.LightGray,
            disabledContainerColor = Color.White
        ),
        modifier = Modifier
            .padding(horizontal = 40.dp, vertical = 12.dp)
            .height(44.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) Color(0xFF2E7D32) else Color.LightGray
        )
    }
}
