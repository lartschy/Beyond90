package com.lartschy.beyond90.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.lartschy.beyond90.data.model.Match
import com.lartschy.beyond90.data.model.MatchPrediction
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictionRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun predictionsCollection() =
        firestore.collection("users")
            .document(
                auth.currentUser?.uid
                    ?: throw IllegalStateException(
                        "You must be logged in to save predictions."
                    )
            )
            .collection("predictions")

    suspend fun savePrediction(
        match: Match,
        homeScore: Int,
        awayScore: Int,
        weekStart: String
    ) {
        val eventId = match.idEvent
            ?: throw IllegalArgumentException("Match ID is missing.")

        val data = mapOf(
            "eventId" to eventId,
            "homeTeam" to (match.strHomeTeam ?: ""),
            "awayTeam" to (match.strAwayTeam ?: ""),
            "leagueId" to (match.idLeague ?: ""),
            "leagueName" to (match.strLeague ?: ""),
            "matchDate" to (match.actualDate ?: ""),
            "homeScore" to homeScore,
            "awayScore" to awayScore,
            "weekStart" to weekStart,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        predictionsCollection()
            .document(eventId)
            .set(data)
            .await()
    }

    suspend fun getPredictions(
        matches: List<Match>
    ): Map<String, MatchPrediction> {
        val collection = predictionsCollection()
        val result = mutableMapOf<String, MatchPrediction>()
        Log.d("PredictionRepo", "Current user: ${auth.currentUser?.uid}")
        Log.d("PredictionRepo", "Loading predictions for ${matches.size} matches")
        for (match in matches) {
            val eventId = match.idEvent ?: continue

            val document = collection
                .document(eventId)
                .get()
                .await()

            if (!document.exists()) continue

            val homeScore = document.getLong("homeScore")?.toInt()
                ?: continue
            val awayScore = document.getLong("awayScore")?.toInt()
                ?: continue

            result[eventId] = MatchPrediction(
                eventId = eventId,
                homeScore = homeScore,
                awayScore = awayScore
            )

            Log.d(
                "PredictionRepo",
                "Event ID: $eventId, document exists: ${document.exists()}"
            )
        }

        return result
    }
}