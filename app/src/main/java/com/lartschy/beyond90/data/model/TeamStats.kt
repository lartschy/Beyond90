package com.lartschy.beyond90.data.model

import retrofit2.http.Body
import retrofit2.http.POST

data class StatsRequest(
    val team1: String,
    val team2: String
)

data class StatsResponse(
    val team1: String,
    val team2: String,
    val team1_wins: Int,
    val team2_wins: Int,
    val draws: Int,
    val total_matches: Int
)