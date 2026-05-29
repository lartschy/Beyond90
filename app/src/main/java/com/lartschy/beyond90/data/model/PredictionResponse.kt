package com.lartschy.beyond90.data.model

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
    @SerializedName("home")
    val team1: String?,

    @SerializedName("away")
    val team2: String?,

    @SerializedName("result")
    val prediction: String?,

    @SerializedName("probabilities")
    val probabilities: Map<String, Double>?,

    @SerializedName("stats")
    val stats: Map<String, Any>?,

    @SerializedName("matches_used")
    val matches_used: Map<String, Int>?
)