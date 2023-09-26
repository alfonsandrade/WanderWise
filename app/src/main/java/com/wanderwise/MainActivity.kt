package com.wanderwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wanderwise.ui.theme.WanderWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WanderWiseTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "loginScreen") {
                    composable("loginScreen") {
                        val loginScreen = LoginScreenActivity(navController)
                        loginScreen.Display()
                    }
                    composable("tripScreen") {
                        val tripScreen = TripScreenActivity(navController)
                        tripScreen.Display()
                    }
                    composable("cityListScreen") {
                        val cityListScreen = CityListScreenActivity(navController)
                        cityListScreen.Display()
                    }
                    composable("cityActivitiesScreen") {
                        val cityActivitiesScreen = CityActivitiesScreenActivity(navController)
                        cityActivitiesScreen.Display()
                    }
                }
            }
        }
    }
}