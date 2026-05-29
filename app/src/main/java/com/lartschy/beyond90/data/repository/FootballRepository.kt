package com.lartschy.beyond90.data.repository

import io.ktor.serialization.kotlinx.json.json
import android.util.Log
import com.lartschy.beyond90.data.model.League
import com.lartschy.beyond90.data.model.LeaguePredictionResponse
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.MatchStats
import com.lartschy.beyond90.data.model.PredictionResponse
import com.lartschy.beyond90.data.model.StatsResponse
import com.lartschy.beyond90.data.model.Team
import com.lartschy.beyond90.data.remote.FootballApiService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class FootballRepository(
    private val apiService: FootballApiService
) {
    companion object {
        private const val BACKEND_BASE_URL = "http://10.0.2.2:8000"
    }

    // --- Leagues ---
    suspend fun getAllLeagues(): List<League> {
        val leagues = apiService.getAllLeagues().leagues
        Log.d("FootballRepo", "API returned ${leagues.size} leagues")
        return leagues.filter { it.strSport == "Soccer" && it.strLeague != "_No League" }
    }

    // --- Teams ---
    suspend fun getTeamsByLeague(leagueName: String): List<Team> {
        Log.d("FootballRepo", "Fetching teams for league=$leagueName")
        val response = apiService.getTeams(leagueName)
        return response.teams ?: emptyList()
    }

    // --- Matches ---
    suspend fun searchMatch(team1: String, team2: String): List<Match> {
        return try {
            apiService.searchMatch(team1, team2).events ?: emptyList()
        } catch (e: Exception) {
            Log.e("FootballRepository", "Error fetching match", e)
            emptyList()
        }
    }

    private val client = HttpClient() {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    suspend fun predictMatch(
        team1: String,
        team2: String
    ): PredictionResponse? {
        return try {
            apiService.predictMatch(team1, team2)
        } catch (e: Exception) {
            Log.e("FootballRepository", "Prediction failed", e)
            null
        }
    }

    suspend fun trainModel(): Boolean {
        return try {

            val response = apiService.trainModel()

            Log.d("TRAIN", response.toString())

            true

        } catch (e: Exception) {

            Log.e("TRAIN", "Training failed", e)

            false
        }
    }

    suspend fun getLeaguePrediction(leagueId: String): LeaguePredictionResponse? {
        return try {
            apiService.predictLeague(leagueId)
        } catch (e: Exception) {
            Log.e("FootballRepository", "League prediction error", e)
            null
        }
    }
}


