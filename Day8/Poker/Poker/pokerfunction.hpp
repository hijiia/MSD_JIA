//
//  pokerfunction.hpp
//  Poker
//
//  Created by Jia Gao on 9/2/24.
//

#ifndef pokerfunction_hpp
#define pokerfunction_hpp
#include <iostream>
#include <vector>
#include <string>

struct card {
    int rank;
    std::string suit;
};
//Function:printout
std::vector <card> Deck();
//Function:printout
void printOut (std::vector <card> &printallcards);
void shuffling (std::vector <card> &cards);
bool isFlush (std::vector <card> hand);

#endif /* pokerfunction_hpp */
