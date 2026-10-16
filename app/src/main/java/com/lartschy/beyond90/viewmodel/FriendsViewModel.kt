package com.lartschy.beyond90.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lartschy.beyond90.data.model.Friend
import com.lartschy.beyond90.data.model.FriendRequest
import com.lartschy.beyond90.data.model.UserSearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FriendsViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _searchResult = MutableStateFlow<UserSearchResult?>(null)
    val searchResult: StateFlow<UserSearchResult?> = _searchResult

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _incomingRequests =
        MutableStateFlow<List<FriendRequest>>(emptyList())
    val incomingRequests: StateFlow<List<FriendRequest>> =
        _incomingRequests

    private val _friends =
        MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends

    private val _isLoadingRequests =
        MutableStateFlow(false)
    val isLoadingRequests: StateFlow<Boolean> =
        _isLoadingRequests

    private val _isLoadingFriends =
        MutableStateFlow(false)
    val isLoadingFriends: StateFlow<Boolean> =
        _isLoadingFriends

    fun searchUserByUsername(username: String) {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            _message.value = "You must be logged in"
            return
        }

        val cleanUsername = username
            .trim()
            .removePrefix("@")
            .lowercase()

        if (cleanUsername.isBlank()) {
            _searchResult.value = null
            _message.value = "Please enter a username"
            return
        }

        _isSearching.value = true
        _searchResult.value = null
        _message.value = null

        firestore
            .collection("usernames")
            .document(cleanUsername)
            .get()
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    _isSearching.value = false
                    _message.value =
                        task.exception?.localizedMessage
                            ?: "Search failed"
                    return@addOnCompleteListener
                }

                val document = task.result

                if (!document.exists()) {
                    _isSearching.value = false
                    _message.value =
                        "No user found with that username"
                    return@addOnCompleteListener
                }

                val uid = document.getString("uid")

                if (uid == null) {
                    _isSearching.value = false
                    _message.value = "Invalid user"
                    return@addOnCompleteListener
                }

                if (uid == currentUser.uid) {
                    _isSearching.value = false
                    _message.value = "You cannot add yourself"
                    return@addOnCompleteListener
                }

                // Get public user information
                firestore
                    .collection("userSearch")
                    .document(uid)
                    .get()
                    .addOnCompleteListener { userTask ->

                        _isSearching.value = false

                        if (userTask.isSuccessful) {

                            val userDocument = userTask.result

                            if (userDocument.exists()) {

                                _searchResult.value =
                                    UserSearchResult(
                                        uid = uid,
                                        username =
                                            userDocument.getString(
                                                "username"
                                            ) ?: cleanUsername,
                                        email =
                                            userDocument.getString(
                                                "email"
                                            ) ?: ""
                                    )

                            } else {
                                _message.value = "User profile not found"
                            }

                        } else {
                            _message.value =
                                userTask.exception?.localizedMessage
                                    ?: "Search failed"
                        }
                    }
            }
    }

    fun sendFriendRequest(toUser: UserSearchResult) {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            _message.value = "You must be logged in"
            return
        }

        firestore
            .collection("userSearch")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                val username =
                    document.getString("username")

                if (username == null) {
                    _message.value =
                        "Your username could not be found"
                    return@addOnSuccessListener
                }

                val requestData = hashMapOf(
                    "fromUid" to currentUser.uid,
                    "fromUsername" to username,
                    "fromEmail" to (currentUser.email ?: ""),
                    "toUid" to toUser.uid,
                    "status" to "pending",
                    "createdAt" to System.currentTimeMillis()
                )

                firestore
                    .collection("friendRequests")
                    .add(requestData)
                    .addOnSuccessListener {

                        _message.value = "Friend request sent"
                        _searchResult.value = null
                    }
                    .addOnFailureListener { exception ->

                        _message.value =
                            exception.localizedMessage
                                ?: "Failed to send request"
                    }
            }
    }

    fun loadIncomingRequests() {

        val currentUser = auth.currentUser ?: return

        _isLoadingRequests.value = true

        firestore
            .collection("friendRequests")
            .whereEqualTo(
                "toUid",
                currentUser.uid
            )
            .whereEqualTo(
                "status",
                "pending"
            )
            .get()
            .addOnCompleteListener { task ->

                _isLoadingRequests.value = false

                if (task.isSuccessful) {

                    val requests =
                        task.result?.documents?.mapNotNull { document ->

                            val fromUid =
                                document.getString("fromUid")

                            val fromUsername =
                                document.getString("fromUsername")

                            val fromEmail =
                                document.getString("fromEmail")

                            if (
                                fromUid != null &&
                                fromUsername != null &&
                                fromEmail != null
                            ) {

                                FriendRequest(
                                    id = document.id,
                                    fromUid = fromUid,
                                    fromUsername = fromUsername,
                                    fromEmail = fromEmail
                                )

                            } else {
                                null
                            }

                        } ?: emptyList()

                    _incomingRequests.value = requests

                } else {

                    _message.value =
                        task.exception?.localizedMessage
                            ?: "Failed to load friend requests"
                }
            }
    }

    fun acceptFriendRequest(request: FriendRequest) {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            _message.value = "You must be logged in"
            return
        }

        val requestRef = firestore
            .collection("friendRequests")
            .document(request.id)

        // Jane's friend entry for John
        val myFriendData = hashMapOf(
            "uid" to request.fromUid,
            "username" to request.fromUsername,
            "email" to request.fromEmail,
            "requestId" to request.id
        )

        // John's friend entry for Jane
        val otherFriendData = hashMapOf(
            "uid" to currentUser.uid,
            "name" to (currentUser.displayName ?: "Unknown"),
            "email" to (currentUser.email ?: ""),
            "requestId" to request.id
        )

        val myFriendRef = firestore
            .collection("users")
            .document(currentUser.uid)
            .collection("friends")
            .document(request.fromUid)

        val otherFriendRef = firestore
            .collection("users")
            .document(request.fromUid)
            .collection("friends")
            .document(currentUser.uid)

        // First mark the request as accepted
        requestRef
            .update("status", "accepted")
            .addOnSuccessListener {

                // Add John to Jane's friends
                myFriendRef
                    .set(myFriendData)
                    .addOnSuccessListener {

                        // Add Jane to John's friends
                        otherFriendRef
                            .set(otherFriendData)
                            .addOnSuccessListener {

                                _incomingRequests.value =
                                    _incomingRequests.value.filter {
                                        it.id != request.id
                                    }

                                loadFriends()

                                _message.value =
                                    "Friend request accepted"
                            }
                            .addOnFailureListener { exception ->
                                _message.value =
                                    exception.localizedMessage
                                        ?: "Failed to add friend to the other account"
                            }
                    }
                    .addOnFailureListener { exception ->
                        _message.value =
                            exception.localizedMessage
                                ?: "Failed to add friend"
                    }
            }
            .addOnFailureListener { exception ->
                _message.value =
                    exception.localizedMessage
                        ?: "Failed to accept request"
            }
    }

    fun declineFriendRequest(request: FriendRequest) {

        firestore
            .collection("friendRequests")
            .document(request.id)
            .update("status", "declined")
            .addOnSuccessListener {

                _incomingRequests.value =
                    _incomingRequests.value.filter {
                        it.id != request.id
                    }

                _message.value = "Friend request declined"
            }
            .addOnFailureListener { exception ->

                _message.value =
                    exception.localizedMessage
                        ?: "Failed to decline request"
            }
    }

    fun loadFriends() {

        val currentUser = auth.currentUser ?: return

        _isLoadingFriends.value = true

        firestore
            .collection("users")
            .document(currentUser.uid)
            .collection("friends")
            .get()
            .addOnCompleteListener { task ->

                _isLoadingFriends.value = false

                if (task.isSuccessful) {

                    val friends =
                        task.result?.documents?.mapNotNull { document ->

                            val uid =
                                document.getString("uid")

                            val username =
                                document.getString("username")

                            val email =
                                document.getString("email")

                            if (
                                uid != null &&
                                username != null &&
                                email != null
                            ) {
                                Friend(
                                    uid = uid,
                                    username = username,
                                    email = email
                                )
                            } else {
                                null
                            }

                        } ?: emptyList()

                    _friends.value = friends

                } else {

                    _message.value =
                        task.exception?.localizedMessage
                            ?: "Failed to load friends"
                }
            }
    }

    fun removeFriend(friend: Friend) {

        val currentUser = auth.currentUser ?: return

        firestore
            .collection("users")
            .document(currentUser.uid)
            .collection("friends")
            .document(friend.uid)
            .delete()
            .addOnSuccessListener {

                _friends.value =
                    _friends.value.filter {
                        it.uid != friend.uid
                    }

                _message.value = "Friend removed"
            }
            .addOnFailureListener { exception ->

                _message.value =
                    exception.localizedMessage
                        ?: "Failed to remove friend"
            }
    }

    fun clearMessage() {
        _message.value = null
    }
}