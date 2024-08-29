//
//  main.cpp
//   Day7
//
//  Created by Jia Gao on 8/27/24.
//

#include <iostream>
#include <string>
#include <vector>

struct Politician{
    std::string name;
    std::string affiliation;
    std::string sf;
};

// create a vector p to save struct

// function1:read from p  check for affiliation == Javacans, save in vector pJavacans

// function2:read from p  check for affiliation == Cplusers && s == federal, save in vector pFederal


int main(int argc, const char * argv[]) {
    std::vector <Politician> p;// create a vector to save struct
    
   
    return 0;
}







//vector is a data type，std::vector <Politician> p;
//创建一个 Politician 类型的 vector-- in main




// if bool affiliation isJavacans return true and save in vector2 (for loop add elements by push_back)
// bool federal isFederal return true and save in vector2
// output !isJavacans || !isFederal
