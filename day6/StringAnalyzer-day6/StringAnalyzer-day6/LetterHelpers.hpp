//
//  LetterHelpers.hpp
//  StringAnalyzer-day6
//
//  Created by Jia Gao on 8/26/24.
//

#ifndef LetterHelpers_hpp
#define LetterHelpers_hpp
#include <string>

bool terminator (char c);
bool isPunctuation(char c);
bool isVowel (char c);
bool isConsonants(char c);

double NumWords(std::string s);
double NumSentences(std::string s);
double numVowels(std::string s);
double NumConsonants(std::string s);
double AverageWordLength(std::string s);
double AverageVowelsPerWord(std::string s);



#include <stdio.h>

#endif /* LetterHelpers_hpp */
