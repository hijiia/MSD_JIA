//
//  LetterHelpers-letters.cpp
//  StringAnalyzer-day6
//
//  Created by Jia Gao on 8/26/24.

bool terminator (char c){
    if( c == '.'||c == '?'||c == '!')
        return true;
    else{
        return false;
        }
    
    }
bool isPunctuation(char c){
    if(c == '.' || c == '?' || c == '!' || c == ','){
        return true ;
    }
    else{
        return false ;
    }
};
bool isVowel (char c){
    if(c== 'a' || c=='e' || c == 'i' || c == 'o' || c == 'u' || c== 'y'|| c =='A' || c =='E' || c == 'I' || c == 'O' || c == 'U' || c == 'Y')
    {
        return true ;
    }else{
        return false ;
    }
            
};

bool isConsonants(char c){
    if(!isVowel(c) && !isPunctuation(c) && c != ' ')
        return true ;
    return false ;
}

#include "LetterHelpers-letters.hpp"
