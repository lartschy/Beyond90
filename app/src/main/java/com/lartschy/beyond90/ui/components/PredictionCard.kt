package com.lartschy.beyond90.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lartschy.beyond90.data.model.PredictionResponse


@Composable
fun PredictionCard(prediction: PredictionResponse) {

    // ---------------- SAFE DATA ----------------
    val team1 = prediction.team1 ?: "Home"
    val team2 = prediction.team2 ?: "Away"

    val probabilities = prediction.probabilities ?: emptyMap()
    val stats = prediction.stats ?: emptyMap()
    val matchesUsed = prediction.matches_used ?: emptyMap()

    val t1 = team1.lowercase()
    val t2 = team2.lowercase()

    val h2hStats = stats["h2h"] as? Map<String, Any>

    // ---------------- DEBUG (ideiglenes) ----------------
    Log.d("PREDICT_RAW", prediction.toString())
    Log.d("PROBS_RAW", probabilities.toString())

    // ---------------- UI ----------------
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // HEADER
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🤖 AI Prediction",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // TEAMS
        Text(
            text = "🏟️ $team1 VS ✈️ $team2",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // PROBABILITIES
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            ProbabilityBar(
                "🏟️ $team1",
                probabilities["home_win"],   // 🔥 FIXED KEY
                Color(0xFFAED581)
            )

            ProbabilityBar(
                "🤝 Draw",
                probabilities["draw"],       // 🔥 FIXED KEY
                Color(0xFFFFF176)
            )

            ProbabilityBar(
                "✈️ $team2",
                probabilities["away_win"],   // 🔥 FIXED KEY
                Color(0xFFEF5350)
            )
        }

        Divider(color = Color.White.copy(alpha = 0.3f))

        // H2H
        Text(
            "Head-to-Head Stats",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {

            StatCircle(
                label = "🏟️ Wins",
                value = h2hStats
                    ?.get("${t1}_wins")
                    ?.toString()
                    ?.toDoubleOrNull()
            )

            StatCircle(
                label = "🤝 Draws",
                value = h2hStats
                    ?.get("draws")
                    ?.toString()
                    ?.toDoubleOrNull()
            )

            StatCircle(
                label = "✈️ Wins",
                value = h2hStats
                    ?.get("${t2}_wins")
                    ?.toString()
                    ?.toDoubleOrNull()
            )
        }

        Divider(color = Color.White.copy(alpha = 0.3f))

        // MATCHES USED
        Text(
            "Matches Used",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Head-to-head: ${matchesUsed["h2h"] ?: 0}",
            color = Color.White
        )

        Text(
            "$team1 last matches: ${matchesUsed["team1_last"] ?: 0}",
            color = Color.White
        )

        Text(
            "$team2 last matches: ${matchesUsed["team2_last"] ?: 0}",
            color = Color.White
        )

        Text(
            "Total: ${matchesUsed["total"] ?: 0}",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ProbabilityBar(label: String, percent: Double?, color: Color) {
    Column {
        Text("$label: ${percent ?: 0.0}%", color = Color.White, fontWeight = FontWeight.SemiBold)
        androidx.compose.material3.LinearProgressIndicator(
            progress = ((percent ?: 0.0) / 100).toFloat(),
            color = color,
            trackColor = Color.White.copy(alpha = 0.2f),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun StatCircle(label: String, value: Double?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("${value?.toInt() ?: 0}", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}



@Composable
fun ProbabilityRow(label: String, percent: Double?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White)
        Text("${percent ?: 0.0}%", color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}
