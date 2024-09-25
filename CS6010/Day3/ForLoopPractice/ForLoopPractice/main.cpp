//
//  main.cpp
//  ForLoopPractice
//
//  Created by Jia Gao on 8/21/24.
//

#include <iostream>
//Print the numbers from 1 to 10 for loop
int main(int argc, const char * argv[]) {
        int number;
    for (int i=1; i<11; i++){
        std::cout<< i<<"\n";
    }
    
    //Print the numbers from 1 to 10 while loop
    int i=1;
    while (i<11){
        std::cout<< i <<"\n";
        i++;
    }
    
    //Enter the two numbers
    int number1;
    int number2;
    std::cout << "please enter number1: \n";
    std::cin >> number1;
    std::cout << "please enter number2: \n";
    std::cin >> number2;
    if (number1 < number2){
        for (int i= number1; i <= number2; i++){
            std::cout<< i <<"\n";
        }
    }
    //Enter the two numbers reverse
    if (number1 > number2){
        for (int i= number1; number1 <= number2; --i){
            std::cout<< i <<"\n";
        }
    }
    std::cout<<std::endl;
    
    //Print all the odd numbers between 1 and 20
    
    //    int oddnumber;
    std::cout << "Now im printing out odd numbers from 1-20"<<std::endl;
    for (int i = 1; i <= 20; i=i+2) {
        std::cout<< i <<"\n";
    }
    
    
    // Add up
    std::cout << "Now im printing out added numbers"<<std::endl;
    int number;
    int sum = 0;
    std::cout << "Enter positive numbers to add up untill a negative number input): ";
    std::cin >> number;
    while (number > 0){
        sum = sum + number;
        std::cin >> number;
    }
    
    std::cout << "sum of the numbers u entered: " << sum <<  std::endl;
    
    // Multiplication Table
    std::cout << "Now im printing out added numbers"<<std::endl;
//    int k=1;
    for (int k = 1; k <= 5; ++k) {
        std::cout << k << "x*: ";
        for (int j = 1; j <= 5; ++j) {
            std::cout << k * j << " ";
    
            
        }
        std::cout<<std::endl;
    }
    std::cout<<std::endl;


    

    
    
    
    
}
