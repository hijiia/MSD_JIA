//
//  main.cpp
//  Palindromes
//
//  Created by Jia Gao on 8/22/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    std::string inputString;
    std::string reversed;
    //take an input from the user
    std::cout<<"Enter a word:";
    std::cin>>inputString;
    //std::cout<<inputString.size();
    for(int i=1;i<=inputString.size();i++){
        reversed=reversed+inputString[inputString.size()-i];
        //std::cout<<reversed;
    }
    std::cout<<"input:\n"<<inputString<<"\n";
    std::cout<<"reversed:\n"<<reversed<<"\n";
    if ( reversed == inputString){
        std::cout<<"The word is a palindrome\n";
    }
    else{
        std::cout<<"It is not a palindrome\n";
    }
    
        
    }
