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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

enum class MatchFilter { ALL, UPCOMING, HISTORY }

@HiltViewModel
class FixtureViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    val leagues = MutableStateFlow<List<League>>(emptyList())
    val teams = MutableStateFlow<List<Team>>(emptyList())
    private val allMatches = MutableStateFlow<List<Match>>(emptyList()) // unfiltered
    val matchResults = MutableStateFlow<List<Match>>(emptyList()) // filtered
    val isLoading = MutableStateFlow(false)

    var selectedLeague = MutableStateFlow<League?>(null)
    var selectedTeam1 = MutableStateFlow<Team?>(null)
    var selectedTeam2 = MutableStateFlow<Team?>(null)
    var selectedFilter = MutableStateFlow(MatchFilter.ALL)

    init { fetchLeagues() }

    private fun fetchLeagues() = viewModelScope.launch {
        isLoading.value = true
        leagues.value = repository.getAllLeagues()
        isLoading.value = false
    }

    fun fetchTeamsForLeague(league: League) = viewModelScope.launch {
        isLoading.value = true
        teams.value = repository.getTeamsByLeague(league.strLeague)
        selectedTeam1.value = null
        selectedTeam2.value = null
        isLoading.value = false
    }

    fun fetchMatches() = viewModelScope.launch {
        val team1 = selectedTeam1.value ?: return@launch
        val team2 = selectedTeam2.value ?: return@launch

        isLoading.value = true
        val matches = repository.searchMatch(team1.strTeam, team2.strTeam) +
                repository.searchMatch(team2.strTeam, team1.strTeam)
        allMatches.value = matches
        applyFilter()
        isLoading.value = false
    }

    fun applyFilter() {
        val filter = selectedFilter.value
        val today = LocalDate.now()

        matchResults.value = when (filter) {
            MatchFilter.ALL -> allMatches.value
            MatchFilter.UPCOMING -> allMatches.value.filter { isUpcoming(it, today) }
            MatchFilter.HISTORY -> allMatches.value.filter { !isUpcoming(it, today) }
        }
    }

    fun isUpcoming(match: Match, today: LocalDate = LocalDate.now()): Boolean {
        val raw = match.dateEvent ?: match.dateEventLocal ?: match.strDate ?: return false

        return try {
            val date = LocalDate.parse(raw.substring(0, 10))
            date >= today
        } catch (e: Exception) {
            false
        }
    }


    fun getMatchById(id: String): Match? {
        return matchResults.value.find { it.idEvent == id }
    }


}
