package com.example.lab1

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lab1.ui.theme.Lab1Theme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests using Compose Test Rule
 * These test the UI components and user interactions
 */
@RunWith(AndroidJUnit4::class)
class CourseAppUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_initial_majors_are_displayed() {
        // Given: app is launched
        val viewModel = CourseViewModel()

        composeTestRule.setContent {
            Lab1Theme {
                CourseApp(viewModel = viewModel)
            }
        }
        Thread.sleep(3000)
        composeTestRule.waitForIdle()

        // Then: main UI elements should be visible
        composeTestRule.onNodeWithText("Computer Science").assertIsDisplayed()
        composeTestRule.onNodeWithText("English").assertIsDisplayed()
    }

    @Test
    fun test_add_new_course_dialog_opens_and_closes() {
        val viewModel = CourseViewModel()

        composeTestRule.setContent {
            Lab1Theme {
                CourseApp(viewModel = viewModel)
            }
        }

        // When: clicking add new class button (FAB)
        composeTestRule.onNodeWithText("Add new class").performClick()

        // Then: dialog should open
        composeTestRule.onNodeWithText("Add New Course").assertIsDisplayed()
        composeTestRule.onNodeWithText("Course Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lecturer").assertIsDisplayed()

        // When: clicking cancel
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Then: dialog should close
        composeTestRule.onNodeWithText("Add New Course").assertDoesNotExist()
    }

    @Test
    fun test_add_new_course_functionality() {
        val viewModel = CourseViewModel()

        composeTestRule.setContent {
            Lab1Theme {
                CourseApp(viewModel = viewModel)
            }
        }

        // When: adding a new course
        composeTestRule.onNodeWithText("Add new class").performClick()

        // Fill in course details - target the text fields directly
        composeTestRule.onAllNodesWithText("e.g. CS6018").onFirst().performTextInput("CS101")
        composeTestRule.onAllNodesWithText("e.g. Ben").onFirst().performTextInput("Dr. Smith")

        // Submit
        composeTestRule.onNodeWithText("Confirm").performClick()

        // Then: course should be added and displayed
        composeTestRule.onNodeWithText("Course: CS101").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lecturer: Dr. Smith").assertIsDisplayed()
    }

    @Test
    fun test_edit_course_functionality() {
        val viewModel = CourseViewModel()
        // Pre-add a course for editing
        viewModel.addCourse("CS101", "Dr. Smith")

        composeTestRule.setContent {
            Lab1Theme {
                CourseApp(viewModel = viewModel)
            }
        }

        // Verify course is displayed first
        composeTestRule.onNodeWithText("Course: CS101").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lecturer: Dr. Smith").assertIsDisplayed()

        // When: clicking edit button
        composeTestRule.onNodeWithText("Edit").performClick()

        // Then: edit dialog should open
        composeTestRule.onNodeWithText("Edit Course").assertIsDisplayed()

        // The text fields should have the current values pre-filled
        // Clear and enter new values
        composeTestRule.onAllNodesWithText("CS101").onFirst().performTextClearance()
        composeTestRule.onAllNodesWithText("CS101").onFirst().performTextInput("CS102")

        composeTestRule.onNodeWithText("Confirm").performClick()

        // Then: course should be updated
        composeTestRule.onNodeWithText("Course: CS102").assertIsDisplayed()
    }

    @Test
    fun test_dialog_validation_prevents_empty_submission() {
        val viewModel = CourseViewModel()

        composeTestRule.setContent {
            Lab1Theme {
                CourseApp(viewModel = viewModel)
            }
        }

        // When: opening dialog without filling fields
        composeTestRule.onNodeWithText("Add new class").performClick()

        // Then: confirm button should be disabled
        composeTestRule.onNodeWithText("Confirm").assertIsNotEnabled()

        // When: filling only one field
        composeTestRule.onAllNodesWithText("e.g. CS6018").onFirst().performTextInput("CS101")

        // Then: confirm button should still be disabled
        composeTestRule.onNodeWithText("Confirm").assertIsNotEnabled()

        // When: filling both fields
        composeTestRule.onAllNodesWithText("e.g. Ben").onFirst().performTextInput("Dr. Smith")

        // Then: confirm button should be enabled
        composeTestRule.onNodeWithText("Confirm").assertIsEnabled()
    }

    @Test
    fun test_basic_course_operations() {
        val viewModel = CourseViewModel()

        composeTestRule.setContent {
            Lab1Theme {
                CourseApp(viewModel = viewModel)
            }
        }

        // Test basic course addition
        composeTestRule.onNodeWithText("Add new class").performClick()

        // Use more specific node selection
        composeTestRule.onNode(
            hasText("e.g. CS6018") and hasSetTextAction()
        ).performTextInput("TestCourse")

        composeTestRule.onNode(
            hasText("e.g. Ben") and hasSetTextAction()
        ).performTextInput("TestProf")

        composeTestRule.onNodeWithText("Confirm").performClick()

        // Verify course was added
        composeTestRule.onNodeWithText("Course: TestCourse").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lecturer: TestProf").assertIsDisplayed()
    }
}