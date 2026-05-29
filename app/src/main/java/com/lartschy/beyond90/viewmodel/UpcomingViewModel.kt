package com.lartschy.beyond90.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.PredictionResponse
import com.lartschy.beyond90.data.model.StatsResponse
import com.lartschy.beyond90.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    private val _prediction = MutableStateFlow<PredictionResponse?>(null)
    val prediction: StateFlow<PredictionResponse?> = _prediction
    private val _isTraining = MutableStateFlow(false)
    val isTraining: StateFlow<Boolean> = _isTraining

    private val _match = MutableStateFlow<Match?>(null)
    val match = _match.asStateFlow()

    fun load(homeTeam: String, awayTeam: String) {

        viewModelScope.launch {

            _isTraining.value = true

            // TRAIN MODEL
            repository.trainModel()

            // OPTIONAL MATCH FETCH
            val matchResult = repository.searchMatch(homeTeam, awayTeam)
            _match.value = matchResult.firstOrNull()

            // PREDICTION
            val predictionResult = repository.predictMatch(
                homeTeam,
                awayTeam
            )

            _prediction.value = predictionResult

            _isTraining.value = false
        }
    }


}
