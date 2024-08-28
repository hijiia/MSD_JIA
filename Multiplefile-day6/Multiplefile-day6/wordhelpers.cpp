//
//  wordhelpers.cpp
//  Multiplefile-day6
//
//  Created by Jia Gao on 8/27/24.
//

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

int NumWords(std::string s){
    int words = 0;
    
    for (int i = 0; i < s.size(); i++){
        if(s[i] == ' '){
            words++;
            
        }
        
        
    }
    return words = words+1;
}
    //f6
int NumSentences(std::string s){

    int countsentences = 0;
    for (int i = 0 ; i < s.size(); i++){
        if (IsTerminator(s[i])){
            countsentences ++ ;
        }
       
    }
    return countsentences;
}

double AverageWordLength(std::string s){
    double a = NumVowels(s);
    double b = NumConsonants(s);
    return (a + b)/NumWords(s);
        }
