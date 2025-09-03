package com.example.degreeplanner.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.degreeplanner.data.Course
import com.example.degreeplanner.data.Requirement
import com.example.degreeplanner.data.CourseRequirement
import com.example.degreeplanner.ui.components.RequirementItem

@Composable
fun RequirementList(requirements: List<Requirement>, userCourses: List<Course>) {
    Column {
        Text("Requirement List")
        for (requirement in requirements) {
            RequirementItem(requirement = requirement, isSatisfied = requirement.isSatisfied(userCourses))
        }
    }

}
@Preview
@Composable
fun RequirementListPreview() {
    //courses
    val cs101 = Course("CS", 101)
    val cs102 = Course("CS", 102)
    val phil101 = Course("PHIL", 101)
    val music101 = Course("MUC", 101)
    val math101 = Course("MATH", 101)

    //requirements
    val sampleRequirements = listOf(
        CourseRequirement.Mandatory(cs101),
        CourseRequirement.Mandatory(cs102),
        CourseRequirement.Mandatory(math101),
        CourseRequirement.Elective(listOf(phil101, music101))
    )

    //user courses (part completed)
    val sampleUserCourses = listOf(
        cs101, //User has completed CS101
        math101 //User has completed MATH101
    )

    RequirementList(
        requirements = sampleRequirements,
        userCourses = sampleUserCourses
    )
}