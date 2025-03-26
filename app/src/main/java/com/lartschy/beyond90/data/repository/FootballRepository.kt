package com.lartschy.beyond90.data.repository


import com.lartschy.beyond90.data.remote.FootballApi
import com.lartschy.beyond90.data.model.Event
import javax.inject.Inject

class FootballRepository @Inject constructor(private val api: FootballApi) {
    suspend fun getUpcomingMatches(leagueId: String): List<Event> {
        return api.getUpcomingMatches(leagueId).events
    }
}
