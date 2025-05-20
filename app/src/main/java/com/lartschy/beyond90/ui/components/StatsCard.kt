package com.lartschy.beyond90.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.lartschy.beyond90.data.model.TeamStats

@Composable
fun StatsCard(stats: TeamStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "vs ${stats.opponentName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Wins: ${stats.wins}",
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Draws: ${stats.draws}",
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Losses: ${stats.losses}",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            PieChart(
                wins = stats.wins,
                draws = stats.draws,
                losses = stats.losses,
                modifier = Modifier
                    .size(80.dp)
                    .padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun PieChart(
    wins: Int,
    draws: Int,
    losses: Int,
    modifier: Modifier = Modifier
) {
    val total = wins + draws + losses
    if (total == 0) return

    val proportions = listOf(wins, draws, losses).map { it.toFloat() / total }
    val colors = listOf(Color.White, Color.Gray, Color.Black)

    Canvas(modifier = modifier) {
        val strokeWidth = 8f
        val radius = size.minDimension / 2

        // Draw pie slices
        var startAngle = -90f
        proportions.forEachIndexed { index, proportion ->
            val sweep = proportion * 360f
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true,
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                topLeft = androidx.compose.ui.geometry.Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                )
            )
            startAngle += sweep
        }

        drawCircle(
            color = Color.Black,
            style = Stroke(width = strokeWidth),
            radius = radius,
            center = center
        )
    }
}
