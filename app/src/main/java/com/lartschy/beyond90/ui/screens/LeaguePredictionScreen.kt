package com.lartschy.beyond90.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lartschy.beyond90.data.model.PredictedTeam
import com.lartschy.beyond90.ui.components.PredictionRow
import com.lartschy.beyond90.viewmodel.LeagueViewModel

@Composable
fun LeaguePredictionScreen(
    leagueId: String,
    viewModel: LeagueViewModel
) {
    val prediction by viewModel.prediction.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchLeaguePrediction(leagueId)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "League Prediction",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        when {
            prediction == null -> {
                Text("Loading...", modifier = Modifier.padding(16.dp))
            }

            else -> {
                // HEADER
                PredictionHeader()

                LazyColumn {
                    items(prediction!!.predicted_table) { team ->
                        PredictionRowStyled(team)
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionRowStyled(team: PredictedTeam) {

    val rankDisplay = when (team.predicted_rank) {
        1 -> "🏆"
        2 -> "🥈"
        3 -> "🥉"
        else -> "${team.predicted_rank}"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Rank / Emoji
            Text(
                text = rankDisplay,
                modifier = Modifier.width(40.dp),
                fontSize = 18.sp
            )

            // Team név
            Text(
                text = team.team,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium
            )

            // Pontok
            Text(
                text = String.format("%.1f", team.predicted_points),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
fun PredictionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Rank", fontWeight = FontWeight.Bold)
        Text("Team", fontWeight = FontWeight.Bold)
        Text("Points", fontWeight = FontWeight.Bold)
    }
}