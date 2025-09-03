package com.example.degreeplanner.ui.screen
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.degreeplanner.data.Course
import com.example.degreeplanner.data.CourseRequirement
import com.example.degreeplanner.data.Requirement
import com.example.degreeplanner.ui.components.CourseInput
import com.example.degreeplanner.ui.components.CourseList
import com.example.degreeplanner.ui.components.RequirementList


@Composable
fun PlannerScreen() {
    var userCourses by remember {
        mutableStateOf(
            listOf(
                Course("CS", 101),
                Course("MATH", 101)
            )
        )
    }

    val requirements = listOf(
        CourseRequirement.Mandatory(Course("PHIL", 101)),
        CourseRequirement.Mandatory(Course("CS", 102)),
        CourseRequirement.Mandatory(Course("MATH", 101))
    )

    Column {
        Text("Degree Planner")

        CourseInput(
            onAddCourse = { newCourse ->
                if (!userCourses.contains(newCourse)) {
                    userCourses = userCourses + newCourse
                }
            }
        )

        CourseList(
            courses = userCourses,
            onRemoveCourse = { courseToRemove ->
                userCourses = userCourses.filter { it != courseToRemove }
            }
        )

        RequirementList(
            requirements = requirements,
            userCourses = userCourses
        )
    }
}
@Preview
@Composable
fun PlannerScreenPreview() {
    PlannerScreen()
}

