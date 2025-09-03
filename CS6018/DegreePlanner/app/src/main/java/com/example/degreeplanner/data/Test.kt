package com.example.degreeplanner.data
//listOf() 是 Kotlin 标准库中的内置函数 作用：创建一个不可变的列表（List）
    fun main(){
        val cs101 = Course("cs", 101)
        val cs102 = Course("cs", 102)
        val phil101 = Course("phil", 101)
        val music101 = Course("mus", 101)
        val math101 = Course("math", 101)

        val user01courseList = listOf<Course>(cs101, cs102, phil101)

        val requirement: List<Requirement> = listOf(
            CourseRequirement.Mandatory(cs101),
            CourseRequirement.Mandatory(cs102),
            CourseRequirement.Mandatory(math101),
            CourseRequirement.Elective(listOf(phil101,music101))
            )
        //()
        val result = Checker().requirementSatisfied(user01courseList, requirement)
        print(result)

    }