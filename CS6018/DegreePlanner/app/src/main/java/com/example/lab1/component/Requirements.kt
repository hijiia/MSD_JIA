// Requirement.kt
package com.example.lab1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab1.data.requiredCourse
import com.example.lab1.ui.theme.Pink80

@Composable
fun RequiredCourseList(
    viewModel: CourseViewModel,
    modifier: Modifier = Modifier
) {
    val allMajors = viewModel.getAllMajors()

    Column(modifier = modifier) {
        // Major selection section
        MajorSelectionSection(
            allMajors = allMajors,
            selectedMajors = viewModel.selectedMajors,
            onMajorToggle = viewModel::toggleMajorSelection
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress section
        if (viewModel.isAllRequirementsMet) {
            CompletionMessage(isAllRequirementsMet = true)
        } else {
            ProgressSection(
                filteredCourses = viewModel.filteredRequiredCourses,
                completionStatus = viewModel.completionStatus
            )
        }

        // Required courses list
        RequiredCoursesList(
            filteredCourses = viewModel.filteredRequiredCourses,
            courseList = viewModel.courseList
        )
    }
}

@Composable
fun MajorSelectionSection(
    allMajors: List<String>,
    selectedMajors: Set<String>,
    onMajorToggle: (String) -> Unit
) {
    Text(
        text = "Please select your major",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )

    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        allMajors.map { major ->
            item {
                MajorCheckboxItem(
                    major = major,
                    isSelected = major in selectedMajors,
                    onToggle = { onMajorToggle(major) }
                )
            }
        }
    }
}

@Composable
fun MajorCheckboxItem(
    major: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )
        Text(
            text = major,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun ProgressSection(
    filteredCourses: List<requiredCourse>,
    completionStatus: Pair<Int, Int>
) {
    if (filteredCourses.isNotEmpty()) {
        val (completed, total) = completionStatus
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Pink80
            )
        ) {
            Text(
                text = "Progress: $completed/$total completed",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun RequiredCoursesList(
    filteredCourses: List<requiredCourse>,
    courseList: List<Pair<String, String>>
) {
    Column {
        filteredCourses.forEach { course ->
            // check if the course is completed
            val isCompleted = when (course.type) {
                "requiredCourse" -> {
                    courseList.any { (courseName, _) -> courseName == course.name }
                }
                "oneOf" -> {
                    course.oneOfCourses.any { optionCourse ->
                        courseList.any { (courseName, _) -> courseName == optionCourse }
                    }
                }
                else -> false
            }

            CourseCard(
                course = course,
                isGreen = isCompleted,
                isOneOf = course.type == "oneOf"
            )
        }
    }
}

@Composable
fun CompletionMessage(isAllRequirementsMet: Boolean) {
    if (isAllRequirementsMet) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Text(
                text = "Congratulations! You are all set!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}