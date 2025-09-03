package com.example.degreeplanner.ui.components
import androidx.compose.foundation.layout.Row
import com.example.degreeplanner.data.Requirement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.degreeplanner.data.Course
import com.example.degreeplanner.data.CourseRequirement.Mandatory
import com.example.degreeplanner.data.CourseRequirement.Elective
import com.example.degreeplanner.data.CourseRequirement


@Composable
fun RequirementItem(requirement: Requirement, isSatisfied: Boolean) {
    Row {
        Text(requirement.getDescription())
        if (isSatisfied) {
            Text("Satisfied", color = Color.Green)
        } else {
            Text("Not Satisfied", color = Color.Red)
        }
    }
}
@Preview
@Composable
fun RequirementItemPreview() {
    RequirementItem(requirement = CourseRequirement.Mandatory(Course("CS", 101)), isSatisfied = true)
}