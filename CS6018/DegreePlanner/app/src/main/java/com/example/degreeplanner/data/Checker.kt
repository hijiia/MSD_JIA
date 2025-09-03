package com.example.degreeplanner.data

class Checker {
    fun requirementSatisfied(courses: List<Course>, requirements: List<Requirement>): Boolean{
        //
        return requirements.all { requirement -> requirement.isSatisfied(courses) }
    }

}