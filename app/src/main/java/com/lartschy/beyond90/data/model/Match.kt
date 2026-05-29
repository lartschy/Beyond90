package com.lartschy.beyond90.data.model

import com.google.gson.annotations.SerializedName

data class MatchResponse(
    @SerializedName("event")
    val events: List<Match>?
)

data class Match(
    @SerializedName("idEvent") val idEvent: String?,
    @SerializedName("idAPIfootball") val idAPIfootball: String?,
    @SerializedName("strEvent") val strEvent: String?,
    @SerializedName("strEventAlternate") val strEventAlternate: String?,
    @SerializedName("strFilename") val strFilename: String?,
    @SerializedName("strSport") val strSport: String?,
    @SerializedName("idLeague") val idLeague: String?,
    @SerializedName("strLeague") val strLeague: String?,
    @SerializedName("strLeagueBadge") val strLeagueBadge: String?,
    @SerializedName("strSeason") val strSeason: String?,
    @SerializedName("strDescriptionEN") val strDescriptionEN: String?,
    @SerializedName("strHomeTeam") val strHomeTeam: String?,
    @SerializedName("strAwayTeam") val strAwayTeam: String?,
    @SerializedName("intHomeScore") val intHomeScore: String?,
    @SerializedName("intRound") val intRound: String?,
    @SerializedName("intAwayScore") val intAwayScore: String?,
    @SerializedName("intSpectators") val intSpectators: String?,
    @SerializedName("strOfficial") val strOfficial: String?,
    @SerializedName("strTimestamp") val strTimestamp: String?,
    @SerializedName("dateEvent") val dateEvent: String?,
    @SerializedName("dateEventLocal") val dateEventLocal: String?,
    @SerializedName("strTime") val strTime: String?,
    @SerializedName("strTimeLocal") val strTimeLocal: String?,
    @SerializedName("strGroup") val strGroup: String?,
    @SerializedName("idHomeTeam") val idHomeTeam: String?,
    @SerializedName("strHomeTeamBadge") val strHomeTeamBadge: String?,
    @SerializedName("idAwayTeam") val idAwayTeam: String?,
    @SerializedName("strAwayTeamBadge") val strAwayTeamBadge: String?,
    @SerializedName("intScore") val intScore: String?,
    @SerializedName("intScoreVotes") val intScoreVotes: String?,
    @SerializedName("strResult") val strResult: String?,
    @SerializedName("idVenue") val idVenue: String?,
    @SerializedName("strVenue") val strVenue: String?,
    @SerializedName("strCountry") val strCountry: String?,
    @SerializedName("strCity") val strCity: String?,
    @SerializedName("strPoster") val strPoster: String?,
    @SerializedName("strSquare") val strSquare: String?,
    @SerializedName("strFanart") val strFanart: String?,
    @SerializedName("strThumb") val strThumb: String?,
    @SerializedName("strBanner") val strBanner: String?,
    @SerializedName("strMap") val strMap: String?,
    @SerializedName("strStatus") val strStatus: String?,
    @SerializedName("strPostponed") val strPostponed: String?,
    @SerializedName("strLocked") val strLocked: String?,
    @SerializedName("strDate") val strDate: String?

) {
    val actualDate: String?
        get() = dateEvent
            ?: dateEventLocal
            ?: strDate
            ?: strTimestamp?.substring(0, 10)
}

