//
//  main.cpp
//   Day7
//
//  Created by Jia Gao on 8/27/24.
//

#include <iostream>
#include <string>
#include <vector>

struct Politician{
std::string name;
std::string affiliation;
std::string sf;
};
// function1:read from p  check for affiliation == Javacans, save in vector pJavacans
std::vector <Politician> findjavacans(std::vector <Politician> politicians){
std::vector <Politician> javacaners;
for (int i = 0; i< politicians.size(); i++){
  if(politicians[i].affiliation == "Javacan"){
      javacaners.push_back(politicians[i]);
        }
    }
    return javacaners;
}
// function2:read from p  check for affiliation == Cplusers && s == federal, save in vector pFederal
std::vector <Politician> findFederalCplusers(std::vector<Politician> politicians){
std::vector <Politician> FederalCplusers;
for (int i = 0; i< politicians.size(); i++){
    if(politicians[i].affiliation == "Cplusers" && politicians[i].sf =="Federal"){
        FederalCplusers.push_back(politicians[i]);
        }
    }
    return FederalCplusers;
}



int main(int argc, const char * argv[]) {
    
    std::vector <Politician> politicians;
    Politician allpolitician;
    
    for (int i =0; i< 3; i++){
        
        std::cout<< "please enter a politican name \n";
        std::cin >>  allpolitician.name ;
            
        std::cout<< "please enter your affiliation \n";
        std::cin >> allpolitician.affiliation;
        
        std::cout<< "is it federal or state\n";
        std::cin >> allpolitician.sf;
        
        politicians. push_back(allpolitician);
        
    }
    
    std::vector <Politician> javapolitician = findjavacans (politicians);
    for (int i = 0; i < javapolitician.size(); i++){
        
        std::cout << javapolitician[i].name;
        std::cout << javapolitician[i].affiliation;
        std::cout << javapolitician[i].sf;
    }
        
    
        
    std::vector <Politician> allfederalCplusers = findFederalCplusers (politicians);
    for (int i = 0; i< allfederalCplusers.size(); i++){
        std::cout<< allfederalCplusers[i].name;
        std::cout<< allfederalCplusers[i].affiliation;
        std::cout<< allfederalCplusers[i].sf;
            
        }

    }
