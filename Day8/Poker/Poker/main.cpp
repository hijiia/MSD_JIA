//
//  main.cpp
//  Poker
//
//  Created by Jia Gao on 8/28/24.
//

#include <iostream>
#include <vector>
#include <string>
#include "pokerfunction.hpp"


int main(int argc, const char * argv[]) {

    std::vector <card> totalcards = Deck();
    printOut(totalcards);
    
    shuffling(totalcards);
    printOut(totalcards);
    
    //bool isFlush (totalcards)
    if (isFlush (totalcards) == true){
        std::cout <<"This is a flush.";
    }else{
        std::cout <<"This is not a flush.";
    }
    
}


// 
