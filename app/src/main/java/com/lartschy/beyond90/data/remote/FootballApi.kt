package com.lartschy.beyond90.data.remote

import com.lartschy.beyond90.data.model.EventsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApi {
    @GET("eventsnextleague.php")
    suspend fun getUpcomingMatches(
        @Query("id") leagueId: String
    ): EventsResponse
}
