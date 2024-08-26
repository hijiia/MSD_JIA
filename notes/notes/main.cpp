//
//  main.cpp
//  notes
//
//  Created by Jia Gao on 8/22/24.
//

#include <iostream>
#include <string>

int main(int argc, const char * argv[]) {
    //structure
    std::string cppStr = "   ";
    std::cout << cppStr << "/n";
    //example1
    std::string userInput;
    std::cout << "please do a text entry:/n";
    std::cin >> userInput;
    std::cout << userInput;
    //concatenation:+
    std::string greeting ="Hello";
    
    //comparison:
    
    if (greeting == "hello"){
        
    }
    //length
    std::string str1="hello, world";
    std::cout << str1.length()<< std::endl;
    
    //change
    std::string str2 ="hello, world";
    str1[12]='$';
    std::cout << str2 << std::endl;
    
    //substring
    std::string str3="Hello, world/n";
    std::string substr=str3.substr(7,5);
    std::cout << "Hello, World!\n";
    return 0;
    
    //password
bool isP
    
    
    
    
    
    
    
    
    
    std::string password;
    bool isValid =false;
    while(!isValid){
        std::cout<<"please enter your password\n";
        std::cin>>password;
        
        if(password.length()<8){
            std::cout<<"please enter more characters/n";
        }else if (password.find("$")
                  == std::string::npos){
            std::cout<<"please add dollar sign to your password/n";
        }else if (!(password[0] >= 'A' && password[0]<='Z')){
            std::cout<<"please check first letter is capital/n";
        }else {
            isValid=true;
            std::cout << "strong passord"<< std::endl;
        }
        
        
        
        
    }
}

    
calculatePowewr ( int base, int exponent){
    int result= base;
    for (int i=0; i< exponent; i++)
        result = result * base;
}
return result


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
