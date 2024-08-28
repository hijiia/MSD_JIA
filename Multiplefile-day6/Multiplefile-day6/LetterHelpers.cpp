//
//  LetterHelpers.cpp
//  Multiplefile-day6
//
//  Created by Jia Gao on 8/27/24.
//
#include <iostream>
#include <string>
#include "LetterHelpers.hpp"
#include "wordhelpers.hpp"

bool IsTerminator (char c) {
    if( c == '.' || c == '?' || c == '!'){
        return true;
    } else {
        
        return false;
    }
}


bool IsPunctuation (char c) {
    if( c == '.' || c == '?' || c==  '!' || c== ','){
        return true;
    }else{
        return false;}
}


bool IsVowel (char c) {
    c = std::tolower (c);
    if (c == 'a' || c == 'e' || c == 'i'|| c =='o'|| c =='u'|| c =='y'){
        return true;
    }else {
        return false;
    }
}

bool IsConsonant (char c) {
    c = std::tolower (c);
    if (!IsTerminator(c) && !IsPunctuation(c) && !IsVowel(c)){
        return true;
    }else{
        return false;
    }
}


int NumVowels (std::string s){
    
    int countvowels = 0;
    for (int i = 0 ; i< s.size(); i++){
        if(IsVowel(s[i])){
            countvowels ++;
        }
    }
    return countvowels;
}


int NumConsonants(std::string s){
    int countconsonants =0;
    for(int i = 0 ; i < s.size(); i++){
        if (IsConsonant(s[i])){
            
            countconsonants ++ ;
        }
        
    }
    return countconsonants;
}


double AverageVowelsPerWord(std::string s){
    double a = NumVowels(s);
    double b = NumWords(s);
    return a/b;
}

