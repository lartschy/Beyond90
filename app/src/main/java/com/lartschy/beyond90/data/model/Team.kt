package com.lartschy.beyond90.data.model
import com.google.gson.annotations.SerializedName

data class TeamResponse(
    val teams: List<Team>
)

data class Team(
    @SerializedName("idTeam") val idTeam: String,
    @SerializedName("strTeam") val strTeam: String,
    @SerializedName("strTeamBadge") val strTeamBadge: String?,
    @SerializedName("strLeague") val strLeague: String?,
    @SerializedName("strLeague2") val strLeague2: String?,
    @SerializedName("strLeague3") val strLeague3: String?,
    @SerializedName("strLeague4") val strLeague4: String?,
    @SerializedName("strLeague5") val strLeague5: String?,
    @SerializedName("strLeague6") val strLeague6: String?,
    @SerializedName("strLeague7") val strLeague7: String?,
    @SerializedName("strStadium") val strStadium: String?,
    @SerializedName("intStadiumCapacity") val intStadiumCapacity: String?
) {
    val allLeagues: List<String>
        get() = listOfNotNull(
            strLeague,
            strLeague2,
            strLeague3,
            strLeague4,
            strLeague5,
            strLeague6,
            strLeague7
        ).filter { it.isNotBlank() }
}
