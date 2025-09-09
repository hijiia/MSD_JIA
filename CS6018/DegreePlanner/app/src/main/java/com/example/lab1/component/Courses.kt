package com.example.lab1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab1.data.requiredCourse
import com.example.lab1.ui.theme.lightbg
import com.example.lab1.ui.theme.oneofcolor

@Composable
fun displayCourse(course: String, lecturer: String) {
    Column {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = Color(0xFF90CAF9)
        )

        Text(
            text = "Course: $course",
            color = Color(0xFF0D47A1)
        )
        Text(
            text = "Lecturer: $lecturer",
            color = Color(0xFF1976D2)
        )
    }
}

@Composable
fun CourseCard(course: requiredCourse, isGreen: Boolean = false, isOneOf: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (isGreen) Color(0xFFE3F2FD)
                else if (isOneOf) Color(0xFFBBDEFB)
                else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Course: ${course.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF0D47A1)
                )
                Text(
                    text = "Major: ${course.major}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1976D2)
                )
            }
            if (isGreen) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1565C0)
                )
            }
        }
    }
}

@Composable
fun PaddingTitle(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Your $name:",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0D47A1),
        modifier = modifier
    )
}