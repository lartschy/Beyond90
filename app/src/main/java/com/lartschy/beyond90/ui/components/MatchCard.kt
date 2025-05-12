package com.lartschy.beyond90.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lartschy.beyond90.data.model.Match

@Composable
fun MatchCard(match: Match) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = match.strEvent ?: "Unknown Event",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            match.strLeague?.let {
                Text(
                    text = "League: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Date: ${match.dateEvent ?: "Unknown"} | Time: ${match.strTime ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall
            )

            match.strVenue?.let {
                Text(
                    text = "Venue: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            match.strStatus?.let {
                Text(
                    text = "Status: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (match.intHomeScore != null && match.intAwayScore != null) {
                Text(
                    text = "Score: ${match.intHomeScore} - ${match.intAwayScore}",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = "${match.strHomeTeam} vs ${match.strAwayTeam}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
