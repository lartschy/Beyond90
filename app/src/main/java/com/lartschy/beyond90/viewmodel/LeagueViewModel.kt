package com.lartschy.beyond90.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lartschy.beyond90.data.model.League
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
                _leagues.value = result
            } catch (e: Exception) {
                // handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
