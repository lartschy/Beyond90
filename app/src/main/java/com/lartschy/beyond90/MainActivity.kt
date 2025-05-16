package com.lartschy.beyond90

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.navigation.compose.rememberNavController
import com.lartschy.beyond90.ui.components.FootballAppUI
import com.lartschy.beyond90.ui.screens.HomeScreen
import com.lartschy.beyond90.ui.screens.LeagueScreen
import com.lartschy.beyond90.ui.screens.RegistrationScreen
import com.lartschy.beyond90.ui.theme.Beyond90Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Beyond90Theme(
                darkTheme = false,
                dynamicColor = false
            ) {
                FootballAppUI()
            }
        }
    }
}

