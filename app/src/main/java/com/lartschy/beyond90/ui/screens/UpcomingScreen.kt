package com.lartschy.beyond90.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lartschy.beyond90.viewmodel.FixtureViewModel
import com.lartschy.beyond90.viewmodel.UpcomingViewModel



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.PredictionResponse
import com.lartschy.beyond90.ui.components.PredictionCard

@Composable
fun UpcomingScreen(
    homeTeam: String,
    awayTeam: String,
    viewModel: UpcomingViewModel = hiltViewModel()
) {

    LaunchedEffect(homeTeam, awayTeam) {
        viewModel.load(homeTeam, awayTeam)
    }

    val match by viewModel.match.collectAsState()
    val prediction by viewModel.prediction.collectAsState()
    val isTraining by viewModel.isTraining.collectAsState()

    // LOADING
    if (isTraining) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator()

                Spacer(modifier = Modifier.height(16.dp))

                Text("Calculating prediction...")
            }
        }

        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // MATCH TITLE
        match?.let {

            item {
                Text(
                    "${it.strHomeTeam?.replace("+", " ")} vs ${it.strAwayTeam?.replace("+", " ")}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }

        // PREDICTION
        prediction?.let { pred ->

            item {
                PredictionCard(pred)
            }
        }

        // EMPTY STATE
        if (match == null && prediction == null) {

            item {
                Text("No data available")
            }
        }
    }
}