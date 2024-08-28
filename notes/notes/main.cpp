//
//  main.cpp
//  notes
//
//  Created by Jia Gao on 8/22/24.
//

#include <iostream>
#include <string>
#include <vector>
//create data type
struct Student{
    std::string name;
    int idNumber;
    std::vector <double> grades;
    double gpa;
};
// function average grade for a student
double calculateAverage(Student student){
    std::vector<double> grades = student.grades;
    int sum = 0;
    for  (int i =0 ; i<grades.size(); i++)
        sum+= grades [i];
    return sum/grades
}


int main(int argc, const char * argv[]) {
//    //structure
//    std::string cppStr = "   ";
//    std::cout << cppStr << "/n";
//    //example1
//    std::string userInput;
//    std::cout << "please do a text entry:/n";
//    std::cin >> userInput;
//    std::cout << userInput;
//    //concatenation:+
//    std::string greeting ="Hello";
//    
//    //comparison:
//    
//    if (greeting == "hello"){
//        
//    }
//    //length
//    std::string str1="hello, world";
//    std::cout << str1.length()<< std::endl;
//    
//    //change
//    std::string str2 ="hello, world";
//    str1[12]='$';
//    std::cout << str2 << std::endl;
//    
//    //substring
//    std::string str3="Hello, world/n";
//    std::string substr=str3.substr(7,5);
//    std::cout << "Hello, World!\n";
//    return 0;
//    
// name string
// idnum int
// grades std::vector double
// gpa double

    

    Student  student1;
    student1.name ="Jia";
    student1.gpa = 70;
    student1.idNumber = 12345;
    student1.grades.push_back (50);
    
    
    std::vector <Student>students;
    students.push_back(student1);
    
    std::cout << "The student average = "<< calculateAverage (student1)<< std::endl;
    
    
    
    
    
}
