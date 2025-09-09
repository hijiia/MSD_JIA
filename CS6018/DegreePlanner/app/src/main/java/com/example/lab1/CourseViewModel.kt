package com.example.lab1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.lab1.data.requiredCourse
import com.example.lab1.data.toRequiredCourses
import com.example.lab1.network.ApiService
import kotlinx.coroutines.launch

 /*ViewModel: Holds and manages data for courses, majors, and requirements*/
class CourseViewModel : ViewModel() {

    private val apiService = ApiService()

     // List of courses the user has added (Course name, Lecturer)
    var courseList by mutableStateOf(listOf<Pair<String, String>>())
        private set

    var selectedMajors by mutableStateOf(setOf<String>())
        private set

    var requiredCourses by mutableStateOf(listOf<requiredCourse>())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val filteredRequiredCourses: List<requiredCourse>
        get() = if (selectedMajors.isEmpty()) {
            emptyList()
        } else {
            requiredCourses.filter { it.major in selectedMajors }
        }

    // calculate the progress
    val completionStatus: Pair<Int, Int>
        get() {
            var completed = 0

            val requiredCourses = filteredRequiredCourses.filter { it.type == "requiredCourse" }
            val oneOfGroups = filteredRequiredCourses.filter { it.type == "oneOf" }
                .groupBy { it.oneOfCourses }

            requiredCourses.forEach { reqCourse ->
                if (courseList.any { (courseName, _) -> courseName == reqCourse.name }) {
                    completed++
                }
            }

            oneOfGroups.forEach { (oneOfOptions, _) ->
                if (oneOfOptions.any { optionCourse ->
                        courseList.any { (courseName, _) -> courseName == optionCourse }
                    }) {
                    completed++
                }
            }

            val total = requiredCourses.size + oneOfGroups.size
            return Pair(completed, total)
        }

    val isAllRequirementsMet: Boolean
        get() {
            val (completed, total) = completionStatus
            return total > 0 && completed == total
        }
     // Load requirements for one degree plan from the network
    // parse json
    fun loadRequirementsFromNetwork(degreeName: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val plansResult = apiService.fetchDegreePlans()
                plansResult.fold(
                    onSuccess = { plansResponse ->
                        val plan = plansResponse.plans.find {
                            it.name.equals(degreeName, ignoreCase = true)
                        }

                        if (plan != null) {
                            val requirementsResult = apiService.fetchDegreeRequirements(plan.path)
                            requirementsResult.fold(
                                onSuccess = { requirementsResponse ->
                                    requiredCourses = requirementsResponse.requirements.toRequiredCourses(requirementsResponse.name)
                                    isLoading = false
                                },
                                onFailure = { exception ->
                                    errorMessage = "Failed to load requirements: ${exception.message}"
                                    isLoading = false
                                }
                            )
                        } else {
                            errorMessage = "Degree plan '$degreeName' not found"
                            isLoading = false
                        }
                    },
                    onFailure = { exception ->
                        errorMessage = "Failed to load degree plans: ${exception.message}"
                        isLoading = false
                    }
                )
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
                isLoading = false
            }
        }
    }

    fun loadAllDegreePlans() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val plansResult = apiService.fetchDegreePlans()
                plansResult.fold(
                    onSuccess = { plansResponse ->
                        val allRequiredCourses = mutableListOf<requiredCourse>()

                        plansResponse.plans.forEach { plan ->
                            val requirementsResult = apiService.fetchDegreeRequirements(plan.path)
                            requirementsResult.fold(
                                onSuccess = { requirementsResponse ->
                                    allRequiredCourses.addAll(
                                        requirementsResponse.requirements.toRequiredCourses(plan.name)
                                    )
                                },
                                onFailure = { exception ->
                                    errorMessage = "Failed to load requirements for ${plan.name}: ${exception.message}"
                                }
                            )
                        }

                        requiredCourses = allRequiredCourses
                        isLoading = false
                    },
                    onFailure = { exception ->
                        errorMessage = "Failed to load degree plans: ${exception.message}"
                        isLoading = false
                    }
                )
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
                isLoading = false
            }
        }
    }

    fun addCourse(name: String, lecturer: String) {
        if (name.isNotBlank() && lecturer.isNotBlank()) {
            courseList = courseList + Pair(name.trim(), lecturer.trim())
        }
    }

    fun updateCourse(index: Int, name: String, lecturer: String) {
        if (name.isNotBlank() && lecturer.isNotBlank() && index >= 0 && index < courseList.size) {
            courseList = courseList.toMutableList().apply {
                set(index, Pair(name.trim(), lecturer.trim()))
            }
        }
    }

    fun deleteCourse(index: Int) {
        if (index >= 0 && index < courseList.size) {
            courseList = courseList.toMutableList().apply {
                removeAt(index)
            }
        }
    }

    fun toggleMajorSelection(major: String) {
        selectedMajors = if (major in selectedMajors) {
            selectedMajors - major
        } else {
            selectedMajors + major
        }
    }

    fun initializeRequiredCourses(courses: List<requiredCourse>) {
        requiredCourses = courses
    }

    fun getAllMajors(): List<String> {
        return requiredCourses.map { it.major }.distinct()
    }
     // Clear error message
    fun clearError() {
        errorMessage = null
    }

    override fun onCleared() {
        super.onCleared()
        apiService.close()
    }
}