package com.lartschy.beyond90.data.remote

import com.lartschy.beyond90.data.model.LeagueResponse
import com.lartschy.beyond90.data.model.MatchResponse
import com.lartschy.beyond90.data.model.TeamResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApiService {
    @GET("all_leagues.php")
    suspend fun getAllLeagues(): LeagueResponse

    @GET("search_all_teams.php")
    suspend fun getTeamsByLeagueName(
        @Query("l") leagueName: String
    ): TeamResponse

    @GET("eventsnextleague.php")
    suspend fun getNextEventsForLeague(
        @Query("id") leagueId: String
    ): MatchResponse

    @GET("searchevents.php")
    suspend fun searchMatch(@Query("e") query: String): MatchResponse



}
