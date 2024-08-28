//
//  LetterHelpers-words.cpp
//  StringAnalyzer-day6
//
//  Created by Jia Gao on 8/26/24.
#include <string>
double NumWords(std::string s){
    double cword = 0 ;
    for(int i=0 ; s[i]!='\0' ; i++)
    {
        if(s[i]==' ' && s[i-1]!=' ')
            cword ++ ;
    }
    return cword+1 ;
}
double NumSentences(std::string s){
    double CountSentences = 0 ;
    for(int i=0 ; s[i]!='\0' ; i++)
    {
        if(terminator(s[i]))
            CountSentences++ ;
    }
    return CountSentences ;
    
}
double numVowels(std::string s){
    double CountVowels = 0 ;
    for(int i=0 ;s[i]!='\0' ; i++)
    {
        if(isVowel(s[i]))
            CountVowels++ ;
    }
    return CountVowels ;
}
double NumConsonants(std::string s){
    double CountConsonants = 0 ;
    for(int i=0 ;s[i]!='\0' ; i++)
    {
        if(NumConsonants(s[i]))
            CountConsonants++ ;
    }
    return CountConsonants ;
}
double AverageWordLength(std::string s){
        double CountLength =  ( NumConsonants(s) + numVowels(s) ) / NumWords(s) ;
        return CountLength ;
    }
double AverageVowelsPerWord(std::string s) {
    double CountVowelsPerWord = numVowels(s) / NumWords(s) ;
        return CountVowelsPerWord ;
    }
    
#include "LetterHelpers-words.hpp"
