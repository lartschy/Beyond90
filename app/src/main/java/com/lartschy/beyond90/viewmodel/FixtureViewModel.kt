package com.lartschy.beyond90.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lartschy.beyond90.data.model.League
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.Team
import com.lartschy.beyond90.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FixtureViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _leagues = MutableStateFlow<List<League>>(emptyList())
    val leagues: StateFlow<List<League>> = _leagues

    private val _teamsForSelectedLeague = MutableStateFlow<List<Team>>(emptyList())
    val teamsForSelectedLeague: StateFlow<List<Team>> = _teamsForSelectedLeague

    private val _selectedTeam1 = MutableStateFlow<Team?>(null)
    val selectedTeam1: StateFlow<Team?> = _selectedTeam1

    private val _selectedTeam2 = MutableStateFlow<Team?>(null)
    val selectedTeam2: StateFlow<Team?> = _selectedTeam2

    private val _matchResults = MutableStateFlow<List<Match>>(emptyList())
    val matchResults: StateFlow<List<Match>> = _matchResults

    init {
        fetchLeagues() // fetch leagues on initialization
    }

    private fun fetchLeagues() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAllLeagues()
                _leagues.value = result
            } catch (e: Exception) {
                Log.e("FixtureViewModel", "Error fetching leagues", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTeamsForLeague(league: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val teams = repository.getTeamsByLeagueName(league)
                _teamsForSelectedLeague.value = teams
                if (teams.isNotEmpty()) {
                    _selectedTeam1.value = teams.first()
                    Log.d("FixtureViewModel", "Selected team1: ${teams.first().strTeam}")
                } else {
                    Log.w("FixtureViewModel", "No teams found for league: $league")
                }
            } catch (e: Exception) {
                Log.e("FixtureViewModel", "Error fetching teams", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedTeam1(team: Team) {
        _selectedTeam1.value = team
    }

    fun setSelectedTeam2(team: Team) {
        _selectedTeam2.value = team
    }

    fun fetchMatchDataIfReady() {
        val team1 = _selectedTeam1.value
        val team2 = _selectedTeam2.value

        if (team1 == null || team2 == null) {
            Log.w("FixtureViewModel", "Cannot fetch match: one or both teams not selected.")
            return
        }

        viewModelScope.launch {
            try {
                val query = "${team1.strTeam} vs ${team2.strTeam}"
                Log.d("FixtureViewModel", "Fetching match data for query: $query")
                val result = repository.searchMatch(query)
                val matches = result?.event ?: emptyList()
                Log.d("FixtureViewModel", "Match search result: ${matches.size} events found.")

                val formattedMatches = matches.map { match ->
                    match.copy(
                        strTime = match.strTime?.let { formatTimeToHourMinute(it) }
                    )
                }
                _matchResults.value = formattedMatches

            } catch (e: Exception) {
                Log.e("FixtureViewModel", "Error fetching match data", e)
            }
        }
    }
}

private fun formatTimeToHourMinute(time: String): String {
    return try {
        val inputFormat = DateTimeFormatter.ofPattern("HH:mm:ss")
        val outputFormat = DateTimeFormatter.ofPattern("HH:mm")
        LocalTime.parse(time, inputFormat).format(outputFormat)
    } catch (e: Exception) {
        time
    }
}
