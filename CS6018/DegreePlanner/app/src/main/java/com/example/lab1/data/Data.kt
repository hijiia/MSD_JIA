// Data.kt
package com.example.lab1.data

data class requiredCourse(
    val name: String,
    val major: String,
    val type: String, // "requiredCourse" or "oneOf"
    val oneOfCourses: List<String> = emptyList()
)

data class DegreePlansResponse(
    val plans: List<DegreePlan>
)

data class DegreePlan(
    val name: String,
    val path: String
)

data class DegreeRequirementsResponse(
    val name: String,
    val requirements: List<Requirement>
)

data class Requirement(
    val type: String,
    val course: Course? = null,
    val courses: List<Course>? = null
)

data class Course(
    val department: String,
    val number: String
) {
    fun toDisplayName(): String = "${department}${number}"
}

fun List<Requirement>.toRequiredCourses(degreeName: String): List<requiredCourse> {
    val result = mutableListOf<requiredCourse>()

    for (requirement in this) {
        when (requirement.type) {
            "requiredCourse" -> {
                requirement.course?.let { course ->
                    result.add(
                        requiredCourse(
                            name = course.toDisplayName(),
                            major = degreeName,
                            type = "requiredCourse"
                        )
                    )
                }
            }
            "oneOf" -> {
                requirement.courses?.let { courses ->
                    val courseNames = courses.map { it.toDisplayName() }
                    courses.forEach { course ->
                        result.add(
                            requiredCourse(
                                name = course.toDisplayName(),
                                major = degreeName,
                                type = "oneOf",
                                oneOfCourses = courseNames
                            )
                        )
                    }
                }
            }
        }
    }

    return result
}