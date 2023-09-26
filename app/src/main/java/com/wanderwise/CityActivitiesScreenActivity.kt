package com.wanderwise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

class CityActivitiesScreenActivity (private val navController: NavController?){
    @Composable
    fun Display() {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            // Header with buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Add trip button
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add city",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Blue)
                        .clickable { /* Handle add trip action */ }
                        .padding(8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Blue)
                        .clickable { /* Handle settings action */ }
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Body with a list of trips
            Column(
                modifier = Modifier.verticalScroll(state = rememberScrollState())
            ) {
                // Dummy data for trips
                val trips = listOf("Activity 1" , "Activity 2", "Activity 3")
                trips.forEach { tripName ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Blue)
                            .padding(8.dp)
                            .clickable { navController?.navigate("cityActivitiesScreen") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = tripName, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CityActivitiesScreenPreview() {
    val cityListScreen = CityActivitiesScreenActivity(null)
    cityListScreen.Display()
}