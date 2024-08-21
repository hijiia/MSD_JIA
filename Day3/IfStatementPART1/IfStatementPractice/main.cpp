//
//  main.cpp
//  IfStatementPractice
//
//  Created by Jia Gao on 8/21/24.
//

#include <iostream>
//PART 1
int main(int argc, const char * argv[]) {
    int age;
    std::cout << " Please enter your age: ";
    std::cin >> age;
    // check vote
    if (age >= 18){
        std::cout <<"you can vote\n";
    }else{
        std::cout <<"you cannnot vote\n";
    }
    //check senate
    if (age >= 30){
        std::cout <<"you can senate\n";
    } else{
        std::cout<<"you cannnot senate\n";
    }
    
    // check genaration
    if (age > 80){
        std::cout <<"greatest generation";
    }
    else if(age > 60){
        std::cout <<"baby boomers";
    }
    else if(age > 40){
        std::cout<<"generation X";
    }
    else if(age >20){
        std::cout<<"millennial";
    }
    else{
        std::cout<<"iKid";
    }
}



