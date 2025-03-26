package com.lartschy.beyond90.data.model

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("idEvent") val id: String,
    @SerializedName("strEvent") val matchName: String,
    @SerializedName("dateEvent") val date: String,
    @SerializedName("strTime") val time: String,
    @SerializedName("strThumb") val thumbnail: String? // Optional image
)

data class EventsResponse(
    @SerializedName("events") val events: List<Event>
)
