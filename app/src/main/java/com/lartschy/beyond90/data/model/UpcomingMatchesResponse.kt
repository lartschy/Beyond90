package com.lartschy.beyond90.data.model

import com.google.gson.annotations.SerializedName

data class UpcomingMatchesResponse(
    @SerializedName("events")
    val events: List<Match>?
)