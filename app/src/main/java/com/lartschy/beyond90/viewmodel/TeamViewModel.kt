package com.lartschy.beyond90.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lartschy.beyond90.data.model.Team
import com.lartschy.beyond90.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _favoriteTeams = MutableStateFlow<Set<String>>(emptySet())
    val favoriteTeams: StateFlow<Set<String>> = _favoriteTeams

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    fun fetchTeams(leagueName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getTeamsByLeagueName(leagueName)
                _teams.value = result
                loadFavoriteTeams() // also load favorites after fetch
            } catch (e: Exception) {
                // handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(team: Team) {
        val uid = auth.currentUser?.uid ?: return
        val teamId = team.idTeam

        val userFavoritesRef = firestore.collection("users")
            .document(uid)
            .collection("favorites")
            .document(teamId)

        viewModelScope.launch {
            if (_favoriteTeams.value.contains(teamId)) {
                userFavoritesRef.delete()
                _favoriteTeams.value = _favoriteTeams.value - teamId
            } else {
                userFavoritesRef.set(mapOf("teamId" to teamId))
                _favoriteTeams.value = _favoriteTeams.value + teamId
            }
        }
    }


    private fun loadFavoriteTeams() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("favorites")
            .get()
            .addOnSuccessListener { snapshot ->
                _favoriteTeams.value = snapshot.documents.mapNotNull { it.id }.toSet()
            }
    }
}



