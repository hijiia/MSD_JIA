package com.example.lab1

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import com.example.lab1.CourseViewModel
import com.example.lab1.data.requiredCourse

class CourseViewModelTest {

    private lateinit var viewModel: CourseViewModel
    private lateinit var sampleRequiredCourses: List<requiredCourse>

    @Before
    fun setup() {
        viewModel = CourseViewModel()
        sampleRequiredCourses = listOf(
            requiredCourse("CS101", "Computer Science", "requiredCourse"),
            requiredCourse("CS102", "Computer Science", "requiredCourse"),
            requiredCourse("CS2000", "Computer Science", "oneOf", listOf("CS2000", "CS2001")),
            requiredCourse("CS2001", "Computer Science", "oneOf", listOf("CS2000", "CS2001")),
            requiredCourse("EN101", "English", "requiredCourse"),
            requiredCourse("EN2001", "English", "oneOf", listOf("EN2001", "EN2002")),
            requiredCourse("EN2002", "English", "oneOf", listOf("EN2001", "EN2002"))
        )
        viewModel.initializeRequiredCourses(sampleRequiredCourses)
    }

    @Test
    fun `test initial state is empty`() {
        // Given: fresh ViewModel
        val freshViewModel = CourseViewModel()

        // Then: initial state should be empty
        assertTrue("Course list should be empty initially", freshViewModel.courseList.isEmpty())
        assertTrue("Selected majors should be empty initially", freshViewModel.selectedMajors.isEmpty())
        assertTrue("Required courses should be empty initially", freshViewModel.requiredCourses.isEmpty())
    }

    @Test
    fun `test add course with valid input`() {
        // Given: valid course input
        val courseName = "CS101"
        val lecturer = "Dr. Smith"

        // When: adding course
        viewModel.addCourse(courseName, lecturer)

        // Then: course should be added
        assertEquals("Course list should have 1 item", 1, viewModel.courseList.size)
        assertEquals("Course name should match", courseName, viewModel.courseList[0].first)
        assertEquals("Lecturer should match", lecturer, viewModel.courseList[0].second)
    }

    @Test
    fun `test add course with blank input should be ignored`() {
        // When: adding course with blank name
        viewModel.addCourse("", "Dr. Smith")

        // Then: course should not be added
        assertTrue("Course list should remain empty", viewModel.courseList.isEmpty())

        // When: adding course with blank lecturer
        viewModel.addCourse("CS101", "")

        // Then: course should not be added
        assertTrue("Course list should remain empty", viewModel.courseList.isEmpty())
    }

    @Test
    fun `test add course trims whitespace`() {
        // Given: input with whitespace
        val courseName = "  CS101  "
        val lecturer = "  Dr. Smith  "

        // When: adding course
        viewModel.addCourse(courseName, lecturer)

        // Then: whitespace should be trimmed
        assertEquals("Course name should be trimmed", "CS101", viewModel.courseList[0].first)
        assertEquals("Lecturer should be trimmed", "Dr. Smith", viewModel.courseList[0].second)
    }

    @Test
    fun `test update course with valid index`() {
        // Given: existing course
        viewModel.addCourse("CS101", "Dr. Smith")

        // When: updating course
        viewModel.updateCourse(0, "CS102", "Dr. Johnson")

        // Then: course should be updated
        assertEquals("Course name should be updated", "CS102", viewModel.courseList[0].first)
        assertEquals("Lecturer should be updated", "Dr. Johnson", viewModel.courseList[0].second)
    }

    @Test
    fun `test update course with invalid index should be ignored`() {
        // Given: existing course
        viewModel.addCourse("CS101", "Dr. Smith")
        val originalCourse = viewModel.courseList[0]

        // When: updating with invalid index
        viewModel.updateCourse(-1, "CS102", "Dr. Johnson")
        viewModel.updateCourse(1, "CS102", "Dr. Johnson")

        // Then: course should remain unchanged
        assertEquals("Course should remain unchanged", originalCourse, viewModel.courseList[0])
    }

    @Test
    fun `test delete course with valid index`() {
        // Given: existing courses
        viewModel.addCourse("CS101", "Dr. Smith")
        viewModel.addCourse("CS102", "Dr. Johnson")

        // When: deleting first course
        viewModel.deleteCourse(0)

        // Then: first course should be removed
        assertEquals("Should have 1 course remaining", 1, viewModel.courseList.size)
        assertEquals("Remaining course should be CS102", "CS102", viewModel.courseList[0].first)
    }

    @Test
    fun `test delete course with invalid index should be ignored`() {
        // Given: existing course
        viewModel.addCourse("CS101", "Dr. Smith")

        // When: deleting with invalid index
        viewModel.deleteCourse(-1)
        viewModel.deleteCourse(1)

        // Then: course should remain
        assertEquals("Course list should still have 1 item", 1, viewModel.courseList.size)
    }

    @Test
    fun `test toggle major selection`() {
        // When: selecting a major
        viewModel.toggleMajorSelection("Computer Science")

        // Then: major should be selected
        assertTrue("Computer Science should be selected", viewModel.selectedMajors.contains("Computer Science"))

        // When: toggling the same major again
        viewModel.toggleMajorSelection("Computer Science")

        // Then: major should be deselected
        assertFalse("Computer Science should be deselected", viewModel.selectedMajors.contains("Computer Science"))
    }

    @Test
    fun `test filtered required courses with no selection`() {
        // When: no majors selected
        // Then: filtered list should be empty
        assertTrue("Filtered courses should be empty when no majors selected",
            viewModel.filteredRequiredCourses.isEmpty())
    }

    @Test
    fun `test filtered required courses with selection`() {
        // When: selecting Computer Science major
        viewModel.toggleMajorSelection("Computer Science")

        // Then: should show only Computer Science courses
        assertEquals("Should have 4 Computer Science courses", 4, viewModel.filteredRequiredCourses.size)
        assertTrue("All filtered courses should be Computer Science",
            viewModel.filteredRequiredCourses.all { it.major == "Computer Science" })
    }

    @Test
    fun `test completion status calculation with required courses`() {
        // Given: Computer Science major selected and required courses added
        viewModel.toggleMajorSelection("Computer Science")
        viewModel.addCourse("CS101", "Dr. Smith")

        // When: getting completion status
        val (completed, total) = viewModel.completionStatus

        // Then: should show 1 completed out of 3 total (2 required + 1 oneOf group)
        assertEquals("Should have 1 completed course", 1, completed)
        assertEquals("Should have 3 total requirements", 3, total)
    }

    @Test
    fun `test completion status calculation with oneOf courses`() {
        // Given: Computer Science major selected and oneOf course added
        viewModel.toggleMajorSelection("Computer Science")
        viewModel.addCourse("CS2000", "Dr. Smith") // This should satisfy the oneOf group

        // When: getting completion status
        val (completed, total) = viewModel.completionStatus

        // Then: should show 1 completed (oneOf group satisfied)
        assertEquals("Should have 1 completed group", 1, completed)
        assertEquals("Should have 3 total requirements", 3, total)
    }

    @Test
    fun `test is all requirements met when incomplete`() {
        // Given: Computer Science major selected but not all courses added
        viewModel.toggleMajorSelection("Computer Science")
        viewModel.addCourse("CS101", "Dr. Smith")

        // Then: requirements should not be met
        assertFalse("Requirements should not be met", viewModel.isAllRequirementsMet)
    }

    @Test
    fun `test is all requirements met when complete`() {
        // Given: Computer Science major selected and all requirements satisfied
        viewModel.toggleMajorSelection("Computer Science")
        viewModel.addCourse("CS101", "Dr. Smith")      // Required course
        viewModel.addCourse("CS102", "Dr. Johnson")    // Required course
        viewModel.addCourse("CS2000", "Dr. Wilson")    // OneOf course (satisfies the group)

        // Then: requirements should be met
        assertTrue("Requirements should be met", viewModel.isAllRequirementsMet)
    }

    @Test
    fun `test get all majors`() {
        // When: getting all majors
        val allMajors = viewModel.getAllMajors()

        // Then: should return distinct majors
        assertEquals("Should have 2 majors", 2, allMajors.size)
        assertTrue("Should contain Computer Science", allMajors.contains("Computer Science"))
        assertTrue("Should contain English", allMajors.contains("English"))
    }

    @Test
    fun `test multiple major selection`() {
        // When: selecting multiple majors
        viewModel.toggleMajorSelection("Computer Science")
        viewModel.toggleMajorSelection("English")

        // Then: both should be selected
        assertTrue("Computer Science should be selected", viewModel.selectedMajors.contains("Computer Science"))
        assertTrue("English should be selected", viewModel.selectedMajors.contains("English"))

        // And: filtered courses should include both majors
        assertEquals("Should have 7 filtered courses", 7, viewModel.filteredRequiredCourses.size)
    }

    @Test
    fun `test oneOf course completion logic`() {
        // Given: Computer Science major selected
        viewModel.toggleMajorSelection("Computer Science")

        // When: adding one course from oneOf group
        viewModel.addCourse("CS2000", "Dr. Smith")

        // Then: the oneOf group should be considered complete
        val oneOfCourses = viewModel.filteredRequiredCourses.filter { it.type == "oneOf" }
        val isOneOfComplete = oneOfCourses.any { course ->
            course.oneOfCourses.any { optionCourse ->
                viewModel.courseList.any { (courseName, _) -> courseName == optionCourse }
            }
        }
        assertTrue("OneOf group should be complete", isOneOfComplete)
    }

    @Test
    fun `test completion status with mixed requirements`() {
        // Given: English major selected with mixed requirement types
        viewModel.toggleMajorSelection("English")
        viewModel.addCourse("EN101", "Dr. Smith")      // Required course
        viewModel.addCourse("EN2001", "Dr. Johnson")   // OneOf course

        // When: getting completion status
        val (completed, total) = viewModel.completionStatus

        // Then: both requirements should be complete
        assertEquals("Should have 2 completed requirements", 2, completed)
        assertEquals("Should have 2 total requirements", 2, total)
        assertTrue("All requirements should be met", viewModel.isAllRequirementsMet)
    }
}