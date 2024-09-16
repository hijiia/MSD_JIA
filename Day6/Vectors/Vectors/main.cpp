//
//  main.cpp
//  Vectors
//
//  Created by Jia Gao on 8/30/24.
//
#include <iostream>
#include <vector>
#include "vectorFunction.hpp"
#include <string>
int main(int argc, const char * argv[]) {
    /////
    //    std::vector <int> ints;
    //    int num;
    //    for (int i =0; i< 10; i++){
    //        std::cout << "Please enter a integer number: " << std::endl;
    //        std::cin >> num;
    //        ints.push_back(num);
    //    }
    //        std::cout << "result = " << sumup(ints); 
    
    ///
    std::vector <std::string> strings;
    std::string s;
    for (int i = 0; i< 1; i++){
        std::cout << "Please enter a string: " << std::endl;
        std::cin >> s;
        strings.push_back(s);
    }
    std::vector <char> chars = stringToVec(strings);
    for (int i = 0; i< chars.size(); i++){
        std::cout << chars[i] << std::endl;
    }
    ///
    //    std::vector <int> numbers;
    //    int aNumber;
    //    for (int i = 0; i< 10; i++){
    //        std::cout << "Please enter a number: "<< std::endl;
    //        std::cin >> aNumber;
    //        numbers.push_back(aNumber);
    //    }
    //    std::vector <int> reverseNum = reverseInts(numbers);
    //    for (int i = 0; i< reverseNum.size(); i++){
    //        std::cout << reverseNum[i] << std::endl;
    //    }
    //    
    //    return 0;
    //    }
    
    
    
}
    
