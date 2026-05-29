package com.lartschy.beyond90.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lartschy.beyond90.data.model.StatsResponse
import com.lartschy.beyond90.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    var stats by mutableStateOf<StatsResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun fetchStats(team1: String, team2: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                val response = repository.getMatchStats(team1, team2)
                stats = response
            } catch (e: Exception) {
                Log.e("StatsViewModel", "Error fetching stats", e)
            } finally {
                isLoading = false
            }
        }
    }
}
*/