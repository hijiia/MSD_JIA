//
//  main.cpp
//  Multiplefile-day6
//
//  Created by Jia Gao on 8/27/24.
//

#include <iostream>
#include "wordhelpers.hpp"
#include "LetterHelpers.hpp"
int main(int argc, const char * argv[]) {
    std::string s;
    while( s != "done"){
        std::cout << "Enter a string : " << std::endl ;
        getline(std::cin ,s) ;
        if(s != "done"){
            std::cout << " Number of words : " << NumWords(s) << std::endl  ;
            std::cout << " Number of sentences : " << NumSentences(s) << std::endl  ;
            std::cout << " Number of vowels : " << NumVowels(s) << std::endl  ;
            std::cout << " Number of Consonants: " << NumConsonants(s) << std::endl  ;
            std::cout << " Reading level (average word length): " << AverageWordLength(s) << std::endl ;
            std::cout << " Average vowels per word: " << AverageVowelsPerWord(s) << std::endl ;
        }
    }
    std::cout << "Goodbye" << std::endl ;
    return 0;
}// insert code here...
