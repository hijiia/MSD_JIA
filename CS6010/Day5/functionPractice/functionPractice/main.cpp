//
//  main.cpp
//  functionPractice
//
//  Created by Jia Gao on 8/23/24.
//
#include <iostream>
#include <cmath>
#include <ctime>

//hypotenuse function
float hypotenuse(float side1, float side2){
return sqrt((side1*side1) + (side2*side2));
}
//velocity function
void velocity (double speed, double angle ){
    double x=speed*cos(angle * M_PI/180);
    double y=speed*sin(angle * M_PI/180);
    std::cout<<"x= "<<x<<std::endl;
    std::cout<<"y= "<<std::endl;
}
//check capital letter function
bool isCapitalized (std::string letter){
    if (int(letter[0])>=65 && int(letter[0]) <= 90){
        return true;
    }
    else{
        return false;
    }
    }
// boolToString function
std::string boolToString (bool capitalized){
    
    if (capitalized){
        return "true";
    }
    else{
        return "false";
    }
}
// prime number
bool primenumber (int number){
    if(number > 2 && number % 2 == 0){
        return true;
    }
    else if (number > 2 && number % 3 == 0){
        return true;
    }
    else if  (number > 2 && number % 5 == 0){
        return true;
    }
    else if (number > 2 && number % 7 == 0){
        return true;
    }
    else if (number > 2 && number % 11 == 0){
        return true;
    }
    else {
        return false;
    }
    
    
    int main(int argc, const char * argv[]) {
        // PART 1
        //PARTA print the length of the hypotenuse
        float side1;
        float side2;
        float hypotenuseLength;
        std::cout<< "Please enter side1:\n";
        std::cin>> side1;
        std::cout<< "Please enter side2:\n";
        std::cin>> side2;
        if ( side1<0 || side2<0 ){
            
            std::cout<< " Please enter a valid number.\n" <<"\n";
        }
        else {
            std::cout<< "The length of the hypotenuse is: " << hypotenuse (side1,side2);
        }
        
        std::cout<< "Now I'm printing Task 2 \n";
        
        //  PARTB print out their x and y velocity
        double speed;
        double angle;
        std::cout<< "Please enter your speed: ";
        std::cin >> speed;
        std::cout<< "Please enter your angle: ";
        std::cin >> angle;
        velocity (speed, angle);
        
        
        //  PARTC
        
        std::time_t result = std::time(nullptr);
        std::cout << std::asctime(std::localtime(&result))
        << result << " seconds since the Epoch\n";
        
        
        
        // PART2 write Your Own Functions
        
        
        //whether or not the string starts with a capital letter
        std::string letter;
        std::cout << "Please enter the letter:\n";
        std::cin >> letter;
        bool capital = isCapitalized (letter);
        if (capital){
            std::cout<<"the string starts with a capital letter\n";
        }
        else{
            std::cout<<"the string starts without a capital letter\n";
        }
        
        //boolToString
        
        bool capitalized = isCapitalized (letter);
        std::string stringcap = boolToString (capitalized);
        std::cout<<stringcap<<std::endl;
        
        //prime number
        std::cout << "Please enter the number: \n";
        std::cin << number;
        bool prime = primenumber(number);
        if (number){
            std::cout<<"the number is not a prime number.\n"
        }
        else{
            std::cout<<"the number is a prime number.\n"
        }
    }
}
