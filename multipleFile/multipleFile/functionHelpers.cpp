//
//  functionHelpers.cpp
//  multipleFile
//
//  Created by Jia Gao on 8/28/24.
//

#include "functionHelpers.hpp"
#include <iostream>
#include <string>

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


double AverageVowelsPerWord(std::string s){
    double a = NumVowels(s);
    double b = NumWords(s);
    return a/b;
}

