package com.example.degreeplanner

import org.junit.Test

import org.junit.Assert.*
import com.example.degreeplanner.data.Course
import com.example.degreeplanner.data.CourseRequirement
import com.example.degreeplanner.data.Requirement
import org.junit.Assert.*
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */



class CourseRequirementTest {

    // Sample courses for testing
    val cs101 = Course("CS", 101)
    val cs102 = Course("CS", 102)
    val phil101 = Course("PHIL", 101)
    val music101 = Course("MUC", 101)
    val math101 = Course("MATH", 101)

    @Test
    fun mandatory_requirement_satisfied_when_course_completed() {
        val requirement = CourseRequirement.Mandatory(cs101)
        val userCourses = listOf(cs101)

        assertTrue(
            "Mandatory requirement should be satisfied when course is completed",
            requirement.isSatisfied(userCourses)
        )
    }

    @Test
    fun mandatory_requirement_not_satisfied_when_course_not_completed() {
        val requirement = CourseRequirement.Mandatory(cs101)
        val userCourses = listOf(cs102) // Different course

        assertFalse(
            "Mandatory requirement should not be satisfied when course is not completed",
            requirement.isSatisfied(userCourses)
        )
    }
}