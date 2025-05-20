package com.lartschy.beyond90.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.Team
import com.lartschy.beyond90.data.model.TeamStats
import com.lartschy.beyond90.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FavouriteTeamViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    private val _matchesAgainstLeague = MutableStateFlow<List<Match>>(emptyList())
    val matchesAgainstLeague: StateFlow<List<Match>> = _matchesAgainstLeague

    private val _teamStats = MutableStateFlow<List<TeamStats>>(emptyList())
    val teamStats: StateFlow<List<TeamStats>> = _teamStats

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchLeagueMatchesForFavouriteTeam(favouriteTeam: Team) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val leagueName = favouriteTeam.strLeague ?: return@launch

                val leagueTeams = repository.getTeamsByLeagueName(leagueName)
                    .filter { it.idTeam != favouriteTeam.idTeam }

                val allMatches = mutableListOf<Match>()
                val statsList = mutableListOf<TeamStats>()

                for (opponent in leagueTeams) {
                    val query = "${favouriteTeam.strTeam} vs ${opponent.strTeam}"
                    val result = repository.searchMatch(query)
                    val matches = result?.event ?: emptyList()

                    val formattedMatches = matches.map {
                        it.copy(strTime = it.strTime?.let { time -> formatTimeToHourMinute(time) })
                    }

                    allMatches.addAll(formattedMatches)

                    var wins = 0
                    var draws = 0
                    var losses = 0

                    formattedMatches.forEach { match ->
                        val favIsHome = match.strHomeTeam == favouriteTeam.strTeam
                        val favScore = if (favIsHome) match.intHomeScore else match.intAwayScore
                        val oppScore = if (favIsHome) match.intAwayScore else match.intHomeScore

                        if (favScore != null && oppScore != null) {
                            when {
                                favScore > oppScore -> wins++
                                favScore < oppScore -> losses++
                                else -> draws++
                            }
                        }
                    }

                    statsList.add(
                        TeamStats(
                            opponentName = opponent.strTeam ?: "Unknown",
                            wins = wins,
                            draws = draws,
                            losses = losses
                        )
                    )
                }

                _matchesAgainstLeague.value = allMatches
                _teamStats.value = statsList.sortedByDescending { it.wins }

            } catch (e: Exception) {
                Log.e("FavouriteTeamVM", "Error fetching league matches", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatTimeToHourMinute(time: String): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(time)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            time
        }
    }
}
