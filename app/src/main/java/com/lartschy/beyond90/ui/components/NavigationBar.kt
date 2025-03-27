package com.lartschy.beyond90.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("fixtures", Icons.Default.DateRange, "Fixtures"),
        BottomNavItem("leagues", Icons.Default.Star, "Leagues"),
        BottomNavItem("teams", Icons.Default.SportsSoccer, "Teams"),
        BottomNavItem("profile", Icons.Default.Person, "Profile")
    )

    NavigationBar(
        modifier = Modifier.height(56.dp),
        containerColor = Color(0xFFFFFFFF)
    ) {
        val currentBackStackEntry = navController.currentBackStackEntryAsState()

        items.forEach { item ->
            val isSelected = item.route == currentBackStackEntry.value?.destination?.route

            // Customizing icon and label color based on selected state
            val iconColor = if (isSelected) Color.Gray else Color.Black
            val labelColor = if (isSelected) Color.Gray else Color.Black

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconColor // Set icon color here
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = labelColor // Set label color here
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)
