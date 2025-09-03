package com.example.degreeplanner.data

////sealed+abstract： sealed class 中的方法必须是 abstract 或有实现
//sealed class Requirement {
//    abstract fun isSatisfiedBy(course: List<Course>) : Boolean
//    abstract fun getDescription() : String
//}
//Interface 实现：class A : InterfaceB
//Class 继承： class A : ClassB()
interface Requirement {
    //satisfy graduation/term requirement
    fun isSatisfied(courses: List<Course>) : Boolean
    //one must take certain course
    fun getDescription():String
}

sealed class CourseRequirement: Requirement {
    data class Mandatory (val requiredCourse: Course) : CourseRequirement() {
        override fun isSatisfied(courses: List<Course>): Boolean {
            return courses.contains(requiredCourse)
        }
        override fun getDescription(): String = "${requiredCourse.getName()} is mandatory"
    }
    //choose one of from set
    data class Elective (val options: List<Course>) : CourseRequirement() {
        override fun isSatisfied(courses: List<Course>): Boolean {
            return options.any {course -> courses.contains(course)}
        }

        override fun getDescription(): String {
            val courseList = options.joinToString(",")
            return "you have to choose at least one from ${courseList}"
        }
    }

}

