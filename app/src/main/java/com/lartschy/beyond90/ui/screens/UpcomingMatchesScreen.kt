package com.lartschy.beyond90.ui.screens

import com.lartschy.beyond90.viewmodel.FootballViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun UpcomingMatchesScreen(viewModel: FootballViewModel = viewModel()) {
    val matches by viewModel.matches.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMatches("4328") // Example league ID
    }

    LazyColumn {
        items(matches) { match ->
            MatchItem(match)
        }
    }
}

@Composable
fun MatchItem(match: com.lartschy.beyond90.data.model.Event) {
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
