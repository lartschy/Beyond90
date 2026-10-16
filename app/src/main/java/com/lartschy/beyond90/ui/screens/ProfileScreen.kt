package com.lartschy.beyond90.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lartschy.beyond90.ui.components.TeamCard
import com.lartschy.beyond90.viewmodel.AuthViewModel
import com.lartschy.beyond90.viewmodel.FavoritesViewModel
import com.lartschy.beyond90.viewmodel.FriendsViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    friendsViewModel: FriendsViewModel = viewModel()
) {
    val context = LocalContext.current

    val authState by authViewModel.authState.collectAsState()

    val favoriteTeams by favoritesViewModel.favoriteTeams.collectAsState()
    val favoriteTeamIds by favoritesViewModel.favoriteTeamIds.collectAsState()
    val isLoading by favoritesViewModel.isLoading.collectAsState()

    val searchResult by friendsViewModel.searchResult.collectAsState()
    val isSearching by friendsViewModel.isSearching.collectAsState()
    val friendMessage by friendsViewModel.message.collectAsState()
    val incomingRequests by friendsViewModel.incomingRequests.collectAsState()
    val friends by friendsViewModel.friends.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser

    val userEmail = currentUser?.email ?: "Unknown"

    var currentUsername by remember {
        mutableStateOf<String?>(null)
    }

    // Load the current user's username
    LaunchedEffect(currentUser?.uid) {

        if (currentUser != null) {

            FirebaseFirestore
                .getInstance()
                .collection("userSearch")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->

                    currentUsername =
                        document.getString("username")
                }
        }
    }

    // Logout flow
    LaunchedEffect(authState) {

        if (authState == "Logged out") {

            Toast.makeText(
                context,
                "Logged out",
                Toast.LENGTH_SHORT
            ).show()

            navController.navigate("home") {
                popUpTo(0) {
                    inclusive = true
                }
            }

            authViewModel.clearState()
        }
    }

    // Load favourites and friend data
    LaunchedEffect(Unit) {

        favoritesViewModel.loadFavorites()
        friendsViewModel.loadIncomingRequests()
        friendsViewModel.loadFriends()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // ---------------------------------------------------------
        // Profile information
        // ---------------------------------------------------------

        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F0E9)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
            ) {

                Text(
                    text = if (currentUsername != null) {
                        "@$currentUsername"
                    } else {
                        "Loading username..."
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = userEmail,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        // ---------------------------------------------------------
        // Incoming friend requests
        // ---------------------------------------------------------

        if (incomingRequests.isNotEmpty()) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Friend Requests",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            incomingRequests.forEach { request ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F0E9)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {

                        Text(
                            text = "@${request.fromUsername}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "wants to be your friend",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Button(
                                onClick = {
                                    friendsViewModel.acceptFriendRequest(request)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Accept")
                            }

                            Button(
                                onClick = {
                                    friendsViewModel.declineFriendRequest(request)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Decline")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ---------------------------------------------------------
        // Friends
        // ---------------------------------------------------------

        if (friends.isNotEmpty()) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Friends",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            friends.forEach { friend ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F0E9)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "@${friend.username}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = friend.email,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }

                        Button(
                            onClick = {
                                friendsViewModel.removeFriend(friend)
                            }
                        ) {
                            Text("Remove")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ---------------------------------------------------------
        // Add Friend
        // ---------------------------------------------------------

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Add Friend",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        var friendUsername by remember {
            mutableStateOf("")
        }

        OutlinedTextField(
            value = friendUsername,
            onValueChange = {
                friendUsername = it
            },
            label = {
                Text("Friend's username")
            },
            placeholder = {
                Text("@username")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                friendsViewModel.searchUserByUsername(friendUsername)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSearching
        ) {

            if (isSearching) {

                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp)
                )

            } else {

                Text("Search")
            }
        }

        // ---------------------------------------------------------
        // Search result
        // ---------------------------------------------------------

        searchResult?.let { user ->

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F0E9)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(14.dp)
                ) {

                    Text(
                        text = "@${user.username}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = user.email,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            friendsViewModel.sendFriendRequest(user)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Friend Request")
                    }
                }
            }
        }

        // ---------------------------------------------------------
        // Friend-related message
        // ---------------------------------------------------------

        friendMessage?.let { message ->

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------------------------------------------------
        // Favourite teams
        // ---------------------------------------------------------

        Text(
            text = "Favourite Teams",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(favoriteTeams) { team ->

                    TeamCard(
                        team = team,
                        isFavorite = favoriteTeamIds.contains(team.idTeam),
                        onToggleFavorite = {
                            favoritesViewModel.toggleFavorite(it)
                        }
                    )
                }
            }
        }

        // ---------------------------------------------------------
        // Logout
        // ---------------------------------------------------------

        Button(
            onClick = {
                authViewModel.logout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Logout")
        }
    }
}
