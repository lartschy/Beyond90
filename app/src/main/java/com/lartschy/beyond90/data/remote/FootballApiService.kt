package com.lartschy.beyond90.data.remote

import com.lartschy.beyond90.data.model.LeaguePredictionResponse
import com.lartschy.beyond90.data.model.LeagueResponse
import com.lartschy.beyond90.data.model.MatchResponse
import com.lartschy.beyond90.data.model.PredictionResponse
import com.lartschy.beyond90.data.model.StatsRequest
import com.lartschy.beyond90.data.model.StatsResponse
import com.lartschy.beyond90.data.model.TeamResponse
import com.lartschy.beyond90.data.model.TrainResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
interface FootballApiService {
    @GET("api/leagues")
        suspend fun getAllLeagues(): LeagueResponse

    @GET("api/teams/{leagueId}")
    suspend fun getTeams(@Path("leagueId") leagueId: String): TeamResponse

    @GET("api/search_match")
    suspend fun searchMatch(
        @Query("team1") team1: String,
        @Query("team2") team2: String
    ): MatchResponse

    @GET("api/predict_match")
    suspend fun predictMatch(
        @Query("team1") team1: String,
        @Query("team2") team2: String
    ): PredictionResponse

    @GET("api/train")
    suspend fun trainModel(): TrainResponse

    @GET("api/predict_league")
    suspend fun predictLeague(
        @Query("league_id") leagueId: String
    ): LeaguePredictionResponse
}
