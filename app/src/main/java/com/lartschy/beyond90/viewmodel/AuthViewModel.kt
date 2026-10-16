package com.lartschy.beyond90.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _authState = MutableStateFlow<String?>(null)
    val authState: StateFlow<String?> = _authState

    fun register(
        username: String,
        email: String,
        password: String
    ) {
        val cleanUsername = username
            .trim()
            .removePrefix("@")
            .lowercase(Locale.ROOT)

        if (cleanUsername.isBlank()) {
            _authState.value = "Please enter a username"
            return
        }

        if (!cleanUsername.matches(Regex("^[a-z0-9_]+$"))) {
            _authState.value =
                "Username can only contain letters, numbers and _"
            return
        }

        if (cleanUsername.length < 3) {
            _authState.value =
                "Username must be at least 3 characters"
            return
        }

        firestore
            .collection("usernames")
            .document(cleanUsername)
            .get()
            .addOnCompleteListener { usernameTask ->

                if (!usernameTask.isSuccessful) {
                    _authState.value =
                        usernameTask.exception?.localizedMessage
                            ?: "Could not check username"
                    return@addOnCompleteListener
                }

                if (usernameTask.result.exists()) {
                    _authState.value = "Username is already taken"
                    return@addOnCompleteListener
                }

                // Username is available → create Firebase account
                auth.createUserWithEmailAndPassword(
                    email,
                    password
                ).addOnCompleteListener { task ->

                    if (!task.isSuccessful) {
                        _authState.value =
                            task.exception?.localizedMessage
                                ?: "Registration failed"
                        return@addOnCompleteListener
                    }

                    val user = auth.currentUser

                    if (user == null) {
                        _authState.value = "Registration failed"
                        return@addOnCompleteListener
                    }

                    val usernameData = hashMapOf(
                        "uid" to user.uid
                    )

                    val userSearchData = hashMapOf(
                        "uid" to user.uid,
                        "username" to cleanUsername,
                        "email" to email.trim().lowercase()
                    )

                    firestore
                        .collection("usernames")
                        .document(cleanUsername)
                        .set(usernameData)
                        .addOnSuccessListener {

                            firestore
                                .collection("userSearch")
                                .document(user.uid)
                                .set(userSearchData)
                                .addOnSuccessListener {

                                    _authState.value =
                                        "Registration successful"
                                }
                                .addOnFailureListener { exception ->

                                    _authState.value =
                                        exception.localizedMessage
                                            ?: "Failed to create user profile"
                                }
                        }
                        .addOnFailureListener { exception ->

                            _authState.value =
                                exception.localizedMessage
                                    ?: "Failed to reserve username"
                        }
                }
            }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = "Login successful"
                } else {
                    val exception = task.exception

                    _authState.value = when (exception) {
                        is FirebaseAuthInvalidUserException ->
                            "User not found"

                        is FirebaseAuthInvalidCredentialsException ->
                            "Invalid email or password"

                        else ->
                            exception?.localizedMessage
                                ?: "Login failed. Please try again"
                    }
                }
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = "Logged out"
    }

    fun clearState() {
        _authState.value = null
    }
}