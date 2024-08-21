//
//  main.cpp
//  practice
//
//  Created by Jia Gao on 8/21/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    int grade;
    std::cout << " please enter your grade: \n";
    std::cin >> grade;
    if (grade >=60){
        std::cout <<"pass\n";
    }
    
    
    
    int age;
    std::cout << "please enter your age: \n ";
    std::cin >> age;
    if  (age >18 || age <80) {
        std::cout <<"you can vote \n ";
        
    }
    return 0;
}

bool isDone = true;
while (isDone){
    
    std::cout <<"I am inside\n";
}
