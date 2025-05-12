package com.lartschy.beyond90.data.repository

import android.util.Log
import com.lartschy.beyond90.data.model.League
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.MatchResponse
import com.lartschy.beyond90.data.model.Team
import com.lartschy.beyond90.data.remote.FootballApiService

class FootballRepository(private val apiService: FootballApiService) {
    suspend fun getAllLeagues(): List<League> {
        return apiService.getAllLeagues().leagues
            .filter { it.strSport == "Soccer" && it.strLeague != "_No League" }
    }

    suspend fun getTeamsByLeagueName(leagueName: String): List<Team> {
        val response = apiService.getTeamsByLeagueName(leagueName)
        return response.teams
    }


    suspend fun searchMatch(query: String): MatchResponse? {
        return apiService.searchMatch(query)
    }



}
