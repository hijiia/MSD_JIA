//
//  main.cpp
//  StringAnalyzerDay5
//
//  Created by Jia Gao on 8/26/24.
//

#include <iostream>
#include <string>

// f1
bool IsTerminator (char c) {
    if( c == '.' || c == '?' || c == '!'){
        return true;
    } else {
        
        return false;
    }
}

//f2
bool IsPunctuation (char c) {
    if( c == '.' || c == '?' || c==  '!' || c== ','){
        return true;
    }else{
        return false;}
}
//f3
bool IsVowel (char c) {
    c = std::tolower (c);
    if (c == 'a' || c == 'e' || c == 'i'|| c =='o'|| c =='u'|| c =='y'){
        return true;
    }else {
        return false;
    }
    
}
//f4
bool IsConsonant (char c) {
    c = std::tolower (c);
    if (!IsTerminator(c) && !IsPunctuation(c) && !IsVowel(c)){
        return true;
    }else{
        return false;
    }
}

//f5
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
//f7
int NumVowels (std::string s){
    
    int countvowels = 0;
    for (int i = 0 ; i< s.size(); i++){
        if(IsVowel(s[i])){
            countvowels ++;
        }
    }
    return countvowels;
}
//f8
int NumConsonants(std::string s){
    int countconsonants =0;
    for(int i = 0 ; i < s.size(); i++){
        if (IsConsonant(s[i])){
            
            countconsonants ++ ;
        }
        
    }
    return countconsonants;
}

//f9

double AverageWordLength(std::string s){
    double a = NumVowels(s);
    double b = NumConsonants(s);
    return (a + b)/NumWords(s);
        }
            
// f10

double AverageVowelsPerWord(std::string s){
    double a = NumVowels(s);
    double b = NumWords(s);
    return a/b;
}


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
}






































