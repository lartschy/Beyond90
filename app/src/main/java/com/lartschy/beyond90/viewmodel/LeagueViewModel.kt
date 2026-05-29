package com.lartschy.beyond90.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lartschy.beyond90.data.model.League
import com.lartschy.beyond90.data.model.LeaguePredictionResponse
import com.lartschy.beyond90.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeagueViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {
    private val _leagues = MutableStateFlow<List<League>>(emptyList())
    val leagues: StateFlow<List<League>> = _leagues

    private val _prediction = MutableStateFlow<LeaguePredictionResponse?>(null)
    val prediction: StateFlow<LeaguePredictionResponse?> = _prediction
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchLeagues()
    }

    private fun fetchLeagues() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAllLeagues()
                Log.d("LeagueVM", "Fetched leagues: ${result.size}")
                _leagues.value = result
            } catch (e: Exception) {
                Log.e("LeagueVM", "Error fetching leagues", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchLeaguePrediction(leagueId: String) {
        viewModelScope.launch {
            try {
                val result = repository.getLeaguePrediction(leagueId)
                _prediction.value = result
            } catch (e: Exception) {
                Log.e("LeagueVM", "Prediction error", e)
            }
        }
    }

}
