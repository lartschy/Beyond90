package com.lartschy.beyond90.data.model

import com.google.gson.annotations.SerializedName

data class LeagueResponse(
    val leagues: List<League>
)

data class League(
    @SerializedName("idLeague") val idLeague: String,
    @SerializedName("strLeague") val strLeague: String,
    @SerializedName("strSport") val strSport: String,
    @SerializedName("strLeagueAlternate") val strLeagueAlternate: String?
)
