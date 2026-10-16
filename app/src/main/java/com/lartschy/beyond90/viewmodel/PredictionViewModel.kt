package com.lartschy.beyond90.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lartschy.beyond90.data.model.League
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.MatchPrediction
import com.lartschy.beyond90.data.repository.FootballRepository
import com.lartschy.beyond90.data.repository.PredictionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val repository: FootballRepository,
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _leagues = MutableStateFlow<List<League>>(emptyList())
    val leagues: StateFlow<List<League>> = _leagues

    private val _allMatches = MutableStateFlow<List<Match>>(emptyList())

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    private val _selectedLeague = MutableStateFlow<League?>(null)
    val selectedLeague: StateFlow<League?> = _selectedLeague

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _savedPredictions =
        MutableStateFlow<Map<String, MatchPrediction>>(emptyMap())
    val savedPredictions: StateFlow<Map<String, MatchPrediction>> =
        _savedPredictions

    private val _savingMatchIds = MutableStateFlow<Set<String>>(emptySet())
    val savingMatchIds: StateFlow<Set<String>> = _savingMatchIds

    private val _predictionErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val predictionErrors: StateFlow<Map<String, String>> = _predictionErrors

    private val _isLoadingSavedPredictions = MutableStateFlow(false)
    val isLoadingSavedPredictions: StateFlow<Boolean> =
        _isLoadingSavedPredictions

    init {
        loadLeagues()
    }

    private fun loadLeagues() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val loadedLeagues = repository.getAllLeagues()
                _leagues.value = loadedLeagues

                Log.d(
                    "PredictionVM",
                    "Loaded ${loadedLeagues.size} leagues"
                )

                loadAllMatches(loadedLeagues)
            } catch (e: Exception) {
                Log.e("PredictionVM", "Error loading leagues", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadAllMatches(leagues: List<League>) {
        try {
            val allMatches = mutableListOf<Match>()

            for (league in leagues) {
                val leagueMatches =
                    repository.getUpcomingMatches(league.idLeague)

                Log.d(
                    "PredictionVM",
                    "${league.strLeague}: ${leagueMatches.size} matches"
                )

                allMatches.addAll(leagueMatches)
            }

            _allMatches.value = allMatches.distinctBy { it.idEvent }

            Log.d(
                "PredictionVM",
                "ALL MATCHES SIZE = ${_allMatches.value.size}"
            )

            filterMatches()
        } catch (e: Exception) {
            Log.e("PredictionVM", "Error loading matches", e)
            _allMatches.value = emptyList()
            _matches.value = emptyList()
            _savedPredictions.value = emptyMap()
        }
    }

    fun selectLeague(league: League?) {
        _selectedLeague.value = league
        filterMatches()
    }

    private fun filterMatches() {
        // Monday through Sunday of the current calendar week.
        val today = LocalDate.now()
        val startDate = today.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        )
        val endDate = startDate.plusDays(6)

        val selectedLeagueId = _selectedLeague.value?.idLeague

        val filteredMatches = _allMatches.value.filter { match ->
            val matchDate = match.actualDate?.let { dateString ->
                try {
                    LocalDate.parse(dateString.substring(0, 10))
                } catch (e: Exception) {
                    null
                }
            }

            val matchesDate =
                matchDate != null && matchDate in startDate..endDate

            val matchesLeague =
                selectedLeagueId == null ||
                        match.idLeague == selectedLeagueId

            matchesDate && matchesLeague
        }.sortedBy { it.actualDate }

        // Start checking Firestore before exposing the matches to the UI
        loadSavedPredictions(filteredMatches)

        _matches.value = filteredMatches

        Log.d(
            "PredictionVM",
            "Filtered ${filteredMatches.size} matches " +
                    "for current week: $startDate -> $endDate"
        )
    }

    private fun loadSavedPredictions(matches: List<Match>) {
        _isLoadingSavedPredictions.value = true

        viewModelScope.launch {
            try {
                val predictions = predictionRepository.getPredictions(matches)
                _savedPredictions.value = predictions

                Log.d(
                    "PredictionVM",
                    "Loaded ${predictions.size} saved predictions"
                )
            } catch (e: Exception) {
                Log.e(
                    "PredictionVM",
                    "Error loading saved predictions",
                    e
                )
            } finally {
                _isLoadingSavedPredictions.value = false
            }
        }
    }

    fun savePrediction(
        match: Match,
        homeScore: Int,
        awayScore: Int
    ) {
        val eventId = match.idEvent

        Log.d(
            "PredictionVM",
            "SAVE BUTTON: eventId=${match.idEvent}, score=$homeScore:$awayScore"
        )

        if (eventId.isNullOrBlank()) {
            Log.e("PredictionVM", "Cannot save prediction: missing match ID")
            return
        }

        if (_isLoadingSavedPredictions.value) {
            Log.d("PredictionVM", "Cannot save while predictions are loading")
            return
        }

        if (_savedPredictions.value.containsKey(eventId)) {
            Log.d("PredictionVM", "Prediction already exists for $eventId")
            return
        }

        if (homeScore < 0 || awayScore < 0) return

        viewModelScope.launch {
            _savingMatchIds.value = _savingMatchIds.value + eventId
            _predictionErrors.value = _predictionErrors.value - eventId

            try {
                val today = LocalDate.now()
                val weekStart = today.with(
                    TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                ).toString()

                predictionRepository.savePrediction(
                    match = match,
                    homeScore = homeScore,
                    awayScore = awayScore,
                    weekStart = weekStart
                )

                _savedPredictions.value =
                    _savedPredictions.value + (
                            eventId to MatchPrediction(
                                eventId = eventId,
                                homeScore = homeScore,
                                awayScore = awayScore
                            )
                            )

                Log.d(
                    "PredictionVM",
                    "Prediction saved for match $eventId"
                )
            } catch (e: Exception) {
                Log.e(
                    "PredictionVM",
                    "Error saving prediction for $eventId",
                    e
                )

                _predictionErrors.value = _predictionErrors.value +
                        (eventId to (e.localizedMessage ?: "Could not save prediction."))
            } finally {
                _savingMatchIds.value = _savingMatchIds.value - eventId
            }
        }
    }
}