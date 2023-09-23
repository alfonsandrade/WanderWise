package com.wanderwise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

class TripScreenActivity {
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
                    contentDescription = "Add Trip",
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
                val trips = listOf("Trip 1", "Trip 2", "Trip 3")
                trips.forEach { tripName ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Blue)
                            .padding(8.dp)
                            .clickable { /* Handle trip click action */ },
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
fun TripScreenPreview() {
    val tripScreen = TripScreenActivity()
    tripScreen.Display()
}