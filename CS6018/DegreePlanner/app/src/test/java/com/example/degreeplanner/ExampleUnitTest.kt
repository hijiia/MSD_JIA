package com.example.degreeplanner

import com.example.degreeplanner.data.Course
import org.junit.Test
import org.junit.Assert.*
import com.example.degreeplanner.data.CourseRequirement

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */



class CourseRequirementTest {

    //Sample courses for testing
    val cs101 = Course("cs", 101)
    val cs102 = Course("cs", 102)
    val phil101 = Course("phil", 101)
    val muc101 = Course("muc", 101)
    val math101 = Course("math", 101)

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

    @Test
    fun elective_requirement_satisfied_when_one_option_completed() {
        val requirement = CourseRequirement.Elective(listOf(phil101, muc101))
        val userCourses = listOf(phil101) // Completed one of the options

        assertTrue(
            "Elective requirement should be satisfied when one option is completed",
            requirement.isSatisfied(userCourses)
        )
    }

    @Test
    fun elective_requirement_not_satisfied_when_no_options_completed() {
        val requirement = CourseRequirement.Elective(listOf(phil101, muc101))
        val userCourses = listOf(cs101) // Completed different course

        assertFalse(
            "Elective requirement should not be satisfied when no options are completed",
            requirement.isSatisfied(userCourses)
        )
    }
}