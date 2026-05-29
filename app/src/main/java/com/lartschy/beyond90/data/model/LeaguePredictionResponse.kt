package com.lartschy.beyond90.data.model

data class LeaguePredictionResponse(
    val league_id: String,
    val predicted_table: List<PredictedTeam>,
    val teams: Int
)

data class PredictedTeam(
    val team: String,
    val predicted_points: Double,
    val predicted_rank: Int
)