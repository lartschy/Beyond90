package com.lartschy.beyond90.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<String?>(null)
    val authState: StateFlow<String?> = _authState

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _authState.value = if (task.isSuccessful) {
                    "Registration successful"
                } else {
                    task.exception?.message ?: "Registration failed"
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
                        is FirebaseAuthInvalidUserException -> "User not found"
                        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
                        else -> exception?.localizedMessage ?: "Login failed. Please try again"
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

