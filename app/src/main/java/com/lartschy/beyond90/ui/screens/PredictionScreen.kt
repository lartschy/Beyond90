package com.lartschy.beyond90.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lartschy.beyond90.data.model.League
import com.lartschy.beyond90.ui.components.MatchPredictionCard
import com.lartschy.beyond90.viewmodel.PredictionViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

private data class RankingEntry(
    val username: String,
    val points: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(
    navController: NavController,
    viewModel: PredictionViewModel = hiltViewModel()
) {
    val matches by viewModel.matches.collectAsState()
    val leagues by viewModel.leagues.collectAsState()
    val selectedLeague by viewModel.selectedLeague.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var currentMatchIndex by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(matches) {
        currentMatchIndex = 0
    }

    val today = LocalDate.now()
    val startDate = today.with(
        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
    )
    val endDate = startDate.plusDays(6)

    val dateFormatter = DateTimeFormatter.ofPattern(
        "MMM d",
        Locale.ENGLISH
    )

    val weekText =
        "${startDate.format(dateFormatter)} – ${endDate.format(dateFormatter)}"

    val savedPredictions by viewModel.savedPredictions.collectAsState()
    val savingMatchIds by viewModel.savingMatchIds.collectAsState()
    val predictionErrors by viewModel.predictionErrors.collectAsState()

    val isLoadingSavedPredictions by
    viewModel.isLoadingSavedPredictions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Predict")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // LEAGUE FILTER
            LeagueDropdown(
                leagues = leagues,
                selectedLeague = selectedLeague,
                onLeagueSelected = { league ->
                    viewModel.selectLeague(league)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CURRENT WEEK - NO WEEK NAVIGATION
            Text(
                text = "This week: $weekText",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // MATCH PREDICTIONS
            if (isLoading) {
                Text(
                    text = "Loading matches...",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else if (matches.isEmpty()) {
                Text(
                    text = "No matches for this week.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                val safeMatchIndex =
                    currentMatchIndex.coerceIn(0, matches.lastIndex)

                val currentMatch = matches[safeMatchIndex]

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentMatchIndex > 0) {
                                currentMatchIndex--
                            }
                        },
                        enabled = currentMatchIndex > 0
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Previous match"
                        )
                    }

                    MatchPredictionCard(
                        match = currentMatch,
                        modifier = Modifier.weight(1f),
                        savedPrediction = currentMatch.idEvent?.let {
                            savedPredictions[it]
                        },
                        isSaving = currentMatch.idEvent in savingMatchIds,
                        isLoadingSavedPrediction = isLoadingSavedPredictions,
                        saveError = currentMatch.idEvent?.let {
                            predictionErrors[it]
                        },
                        onSave = { homeScore, awayScore ->
                            viewModel.savePrediction(
                                match = currentMatch,
                                homeScore = homeScore,
                                awayScore = awayScore
                            )
                        }
                    )

                    IconButton(
                        onClick = {
                            if (currentMatchIndex < matches.lastIndex) {
                                currentMatchIndex++
                            }
                        },
                        enabled = currentMatchIndex < matches.lastIndex
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowForward,
                            contentDescription = "Next match"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${safeMatchIndex + 1} / ${matches.size}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // WEEKLY RANKING - TEMPORARY SAMPLE DATA
            WeeklyRankingSection()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WeeklyRankingSection() {
    val rankingEntries = listOf(
        RankingEntry(username = "janedoe", points = 3),
        RankingEntry(username = "johndoe", points = 1),
        RankingEntry(username = "alex", points = 1)
    ).sortedByDescending { it.points }

    Text(
        text = "Weekly Ranking",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(12.dp))

    // TABLE HEADER
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Rank",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Name",
            modifier = Modifier.weight(2.5f),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Points",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }

    rankingEntries.forEachIndexed { index, entry ->
        val rank = rankingEntries
            .indexOfFirst { it.points == entry.points } + 1

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rank.toString(),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "@${entry.username}",
                modifier = Modifier.weight(2.5f)
            )

            Text(
                text = entry.points.toString(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueDropdown(
    leagues: List<League>,
    selectedLeague: League?,
    onLeagueSelected: (League?) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedLeague?.strLeague ?: "All leagues",
            onValueChange = {},
            readOnly = true,
            label = {
                Text("League")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text("All leagues")
                },
                onClick = {
                    onLeagueSelected(null)
                    expanded = false
                }
            )

            leagues.forEach { league ->
                DropdownMenuItem(
                    text = {
                        Text(league.strLeague ?: "Unknown league")
                    },
                    onClick = {
                        onLeagueSelected(league)
                        expanded = false
                    }
                )
            }
        }
    }
}
