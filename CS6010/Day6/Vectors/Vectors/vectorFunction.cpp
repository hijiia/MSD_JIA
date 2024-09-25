//
//  vectorFunction.cpp
//  Vectors
//
//  Created by Jia Gao on 9/9/24.
//
#include "vectorFunction.hpp"
#include <vector>
// Function stringToVec
std::vector <char> stringToVec(std::vector <std::string> strings){
    std::vector <char> chars;
    for(int i = 0; i< strings.size(); i++){
        
        for (int j = 0; j< strings[i].size(); j++){
            chars.push_back(strings[i][j]);
        }
    }
    return chars;
    }

//a function sum that takes a vector of ints as a parameter and returns the sum of all the values in the vector
int sumup (std::vector <int> ints){
    int sum = 0;
    for(int i = 0; i < ints.size(); i++){
        sum += ints[i];
    }
    return sum;
}

//a function reverse that takes a vector of ints and returns a vector with the same elements in reverse order.
std::vector <int> reverseInts(std::vector <int> ints){
    std::vector <int> reverseOrder;
    for(int i = ints.size() - 1; i >= 0; i--){
        reverseOrder.push_back(ints[i]);
    }
    return reverseOrder;
}









