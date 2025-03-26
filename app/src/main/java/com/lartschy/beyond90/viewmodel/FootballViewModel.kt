package com.lartschy.beyond90.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lartschy.beyond90.data.model.Event
import com.lartschy.beyond90.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FootballViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    private val _matches = MutableStateFlow<List<Event>>(emptyList())
    val matches = _matches.asStateFlow()

    fun fetchMatches(leagueId: String) {
        viewModelScope.launch {
            try {
                _matches.value = repository.getUpcomingMatches(leagueId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
