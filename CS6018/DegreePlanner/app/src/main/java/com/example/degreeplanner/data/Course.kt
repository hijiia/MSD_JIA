package com.example.degreeplanner.data

data class Course(
    val department : String,
    val number : Int
) {
    fun getName(): String {
        return "$department $number"
    }
}