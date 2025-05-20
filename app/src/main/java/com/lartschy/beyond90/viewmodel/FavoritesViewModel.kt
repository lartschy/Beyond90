package com.lartschy.beyond90.viewmodel

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
class FavoritesViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    private val _favoriteTeams = MutableStateFlow<List<Team>>(emptyList())
    val favoriteTeams: StateFlow<List<Team>> = _favoriteTeams

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchFavoriteTeams() {
        val uid = auth.currentUser?.uid ?: return
        _isLoading.value = true
        firestore.collection("users")
            .document(uid)
            .collection("favorites")
            .get()
            .addOnSuccessListener { snapshot ->
                val ids = snapshot.documents.map { it.getString("teamId") }
                viewModelScope.launch {
                    val allTeams = repository.getAllTeams()
                    _favoriteTeams.value = allTeams.filter { it.idTeam in ids }
                    _isLoading.value = false
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    private fun loadTeamsByIds(ids: List<String>) {
        viewModelScope.launch {
            try {
                val allTeams = repository.getAllTeams()
                _favoriteTeams.value = allTeams.filter { ids.contains(it.idTeam) }
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}
