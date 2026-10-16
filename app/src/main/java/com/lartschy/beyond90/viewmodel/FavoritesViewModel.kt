package com.lartschy.beyond90.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lartschy.beyond90.data.model.Team
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    private val _favoriteTeams = MutableStateFlow<List<Team>>(emptyList())
    val favoriteTeams: StateFlow<List<Team>> = _favoriteTeams

    private val _favoriteTeamIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteTeamIds: StateFlow<Set<String>> = _favoriteTeamIds

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadFavorites() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Log.e("FavoritesVM", "Cannot load favorites: no logged-in user")
            return
        }

        Log.d("FavoritesVM", "Loading favorites for user: $uid")

        _isLoading.value = true

        firestore.collection("users")
            .document(uid)
            .collection("favorites")
            .get()
            .addOnSuccessListener { snapshot ->

                Log.d(
                    "FavoritesVM",
                    "Found ${snapshot.documents.size} favorite teams"
                )

                val teams = snapshot.documents.mapNotNull { document ->

                    try {
                        val team = Team(
                            idTeam = document.getString("idTeam") ?: document.id,
                            strTeam = document.getString("strTeam") ?: "",
                            strTeamBadge = document.getString("strTeamBadge"),
                            strLeague = document.getString("strLeague"),
                            strLeague2 = document.getString("strLeague2"),
                            strLeague3 = document.getString("strLeague3"),
                            strLeague4 = document.getString("strLeague4"),
                            strLeague5 = document.getString("strLeague5"),
                            strLeague6 = document.getString("strLeague6"),
                            strLeague7 = document.getString("strLeague7"),
                            strStadium = document.getString("strStadium"),
                            intStadiumCapacity =
                                document.getString("intStadiumCapacity")
                        )

                        Log.d(
                            "FavoritesVM",
                            """
            FIRESTORE FAVORITE:
            documentId = ${document.id}
            idTeam = ${team.idTeam}
            strTeam = ${team.strTeam}
            strTeamBadge = ${team.strTeamBadge}
            strLeague = ${team.strLeague}
            strLeague2 = ${team.strLeague2}
            strLeague3 = ${team.strLeague3}
            strLeague4 = ${team.strLeague4}
            strLeague5 = ${team.strLeague5}
            strLeague6 = ${team.strLeague6}
            strLeague7 = ${team.strLeague7}
            strStadium = ${team.strStadium}
            intStadiumCapacity = ${team.intStadiumCapacity}
            allLeagues = ${team.allLeagues}
            """.trimIndent()
                        )

                        team

                    } catch (e: Exception) {
                        Log.e(
                            "FavoritesVM",
                            "Could not convert favorite ${document.id}",
                            e
                        )
                        null
                    }
                }

                _favoriteTeams.value = teams

                _favoriteTeamIds.value = snapshot.documents
                    .map { it.id }
                    .toSet()

                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                Log.e(
                    "FavoritesVM",
                    "Failed to load favorites: ${e.message}",
                    e
                )
                _isLoading.value = false
            }
    }

    fun toggleFavorite(team: Team) {

        val uid = auth.currentUser?.uid
        val teamId = team.idTeam

        Log.d("FavoritesVM", "toggleFavorite() called")
        Log.d("FavoritesVM", "User UID: $uid")
        Log.d("FavoritesVM", "Team: ${team.strTeam}")
        Log.d("FavoritesVM", "Team ID: $teamId")

        if (uid == null) {
            Log.e("FavoritesVM", "No logged-in Firebase user!")
            return
        }

        if (teamId == null) {
            Log.e("FavoritesVM", "Team has no ID!")
            return
        }

        val favoriteRef = firestore
            .collection("users")
            .document(uid)
            .collection("favorites")
            .document(teamId)

        // Currently a favourite → remove it
        if (_favoriteTeamIds.value.contains(teamId)) {

            Log.d("FavoritesVM", "Removing $teamId from favorites")

            // Update UI immediately
            _favoriteTeamIds.value =
                _favoriteTeamIds.value - teamId

            _favoriteTeams.value =
                _favoriteTeams.value.filter {
                    it.idTeam != teamId
                }

            // Delete from Firestore
            favoriteRef.delete()
                .addOnSuccessListener {
                    Log.d(
                        "FavoritesVM",
                        "Successfully removed $teamId"
                    )
                }
                .addOnFailureListener { e ->

                    Log.e(
                        "FavoritesVM",
                        "Failed to remove favorite: ${e.message}",
                        e
                    )

                    // Undo UI change if Firestore failed
                    _favoriteTeamIds.value =
                        _favoriteTeamIds.value + teamId

                    _favoriteTeams.value =
                        _favoriteTeams.value + team
                }

        } else {

            Log.d("FavoritesVM", "Adding $teamId to favorites")

            val favoriteData = hashMapOf(
                "idTeam" to team.idTeam,
                "strTeam" to team.strTeam,
                "strTeamBadge" to team.strTeamBadge,
                "strLeague" to team.strLeague,
                "strLeague2" to team.strLeague2,
                "strLeague3" to team.strLeague3,
                "strLeague4" to team.strLeague4,
                "strLeague5" to team.strLeague5,
                "strLeague6" to team.strLeague6,
                "strLeague7" to team.strLeague7,
                "strStadium" to team.strStadium,
                "intStadiumCapacity" to team.intStadiumCapacity
            )

            // Update UI immediately
            _favoriteTeamIds.value =
                _favoriteTeamIds.value + teamId

            _favoriteTeams.value =
                _favoriteTeams.value + team

            // Save to Firestore
            favoriteRef.set(favoriteData)
                .addOnSuccessListener {
                    Log.d(
                        "FavoritesVM",
                        "Successfully added $teamId"
                    )
                }
                .addOnFailureListener { e ->

                    Log.e(
                        "FavoritesVM",
                        "Failed to add favorite: ${e.message}",
                        e
                    )

                    // Undo UI change if Firestore failed
                    _favoriteTeamIds.value =
                        _favoriteTeamIds.value - teamId

                    _favoriteTeams.value =
                        _favoriteTeams.value.filter {
                            it.idTeam != teamId
                        }
                }
        }
    }
}

