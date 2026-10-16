package com.lartschy.beyond90.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.MatchPrediction

@Composable
fun MatchPredictionCard(
    match: Match,
    modifier: Modifier = Modifier,
    savedPrediction: MatchPrediction? = null,
    isSaving: Boolean = false,
    isLoadingSavedPrediction: Boolean = false,
    saveError: String? = null,
    onSave: (Int, Int) -> Unit = { _, _ -> }
) {
    var homeScore by remember(match.idEvent) {
        mutableStateOf("")
    }

    var awayScore by remember(match.idEvent) {
        mutableStateOf("")
    }

    LaunchedEffect(match.idEvent, savedPrediction) {
        homeScore = savedPrediction?.homeScore?.toString() ?: ""
        awayScore = savedPrediction?.awayScore?.toString() ?: ""
    }

    val predictionSaved = savedPrediction != null

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F0E9)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = match.strLeague ?: "Unknown league",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Text(
                text = match.actualDate ?: "",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = match.strHomeTeam ?: "",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (!predictionSaved) {
                when {
                    isLoadingSavedPrediction -> {
                        Text(
                            text = "Checking saved prediction...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    !predictionSaved -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                value = homeScore,
                                onValueChange = {
                                    if (it.length <= 2 && it.all(Char::isDigit)) {
                                        homeScore = it
                                    }
                                },
                                modifier = Modifier.width(64.dp),
                                singleLine = true
                            )

                            Text(
                                text = ":",
                                modifier = Modifier.padding(horizontal = 8.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = awayScore,
                                onValueChange = {
                                    if (it.length <= 2 && it.all(Char::isDigit)) {
                                        awayScore = it
                                    }
                                },
                                modifier = Modifier.width(64.dp),
                                singleLine = true
                            )
                        }
                    }

                    else -> {
                        Text(
                            text = "${savedPrediction!!.homeScore} : ${savedPrediction.awayScore}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Text(
                    text = "${savedPrediction?.homeScore} : ${savedPrediction?.awayScore}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = match.strAwayTeam ?: "",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!predictionSaved) {
                Button(
                    enabled = !isSaving &&
                            homeScore.isNotEmpty() &&
                            awayScore.isNotEmpty(),
                    onClick = {
                        val home = homeScore.toIntOrNull()
                        val away = awayScore.toIntOrNull()

                        if (home != null && away != null) {
                            onSave(home, away)
                        }
                    }
                ) {
                    Text(if (isSaving) "SAVING..." else "SAVE")
                }

                if (saveError != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = saveError,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            } else {
                Text(
                    text = "✓ Prediction saved",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}