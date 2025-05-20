package com.lartschy.beyond90.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lartschy.beyond90.data.model.Team
import com.lartschy.beyond90.ui.components.MatchCard
import com.lartschy.beyond90.ui.components.StatsCard
import com.lartschy.beyond90.viewmodel.FavouriteTeamViewModel

@Composable
fun FavouriteTeamScreen(team: Team) {
    val viewModel: FavouriteTeamViewModel = hiltViewModel()
    val matches by viewModel.matchesAgainstLeague.collectAsState()
    val stats by viewModel.teamStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchLeagueMatchesForFavouriteTeam(team)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = team.strTeam ?: "Favourite Team",
            style = MaterialTheme.typography.headlineMedium
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 20.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                item {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Color Legend
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem(color = Color.White, label = "Wins")
                        LegendItem(color = Color.Gray, label = "Draws")
                        LegendItem(color = Color.Black, label = "Losses")
                    }
                }


                items(stats) { stat ->
                    StatsCard(stats = stat)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .border(
                    width = if (color == Color.White) 2.dp else 0.dp,
                    color = if (color == Color.White) Color.Black else Color.Transparent,
                    shape = CircleShape
                )
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label)
    }
}

