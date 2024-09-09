//
//  pokerfunction.cpp
//  Poker
//
//  Created by Jia Gao on 9/2/24.
//

#include "pokerfunction.hpp"
#include <iostream>
#include <stdio.h>
#include <vector>


//function: deck of cards
std::vector <card> Deck(){
std::vector <card> allcards;
//variable
    card pokerCard;

    for(int i = 1; i <= 14; i++){
        pokerCard.suit = "spades";
        pokerCard.rank = i;
        allcards.push_back(pokerCard);
    }
    for(int i = 1; i <= 14; i++){
        pokerCard.suit = "hearts";
        pokerCard.rank = i;
        allcards.push_back(pokerCard);
    }
    for(int i = 1; i <= 14; i++){
        pokerCard.suit = "diamonds";
        pokerCard.rank = i;
        allcards.push_back(pokerCard);
    }
    for(int i = 1; i <= 14; i++){
        pokerCard.suit = "clubs";
        pokerCard.rank = i;
        allcards.push_back(pokerCard);
    }

return allcards;
    }

//Function:printout
void printOut (std::vector <card> &printallcards){
    card printcards;
    std::string name;
    for (int i = 0; i < printallcards.size(); i++){
        if( printallcards[i].rank != 1 &&  printallcards[i].rank != 14 &&  printallcards[i].rank != 11 &&  printallcards[i].rank != 12 &&  printallcards[i].rank != 13){
            std::cout << printallcards[i].rank;
            std::cout << printallcards[i].suit << std::endl;
    }
        else if (printallcards[i].rank == 1||printallcards[i].rank == 14){
            std::cout <<  "Ace";
            std::cout << printallcards[i].suit << std::endl;
            
            
        }
        else if (printallcards[i].rank == 11){
            std::cout << "Jack";
            std::cout << printallcards[i].suit << std::endl;
            
        }
        else if (printallcards[i].rank == 12){
            std::cout << "Queen";
            std::cout << printallcards[i].suit << std::endl;
            
        }
        else if (printallcards[i].rank == 13){
            std::cout <<  "King";
            std::cout << printallcards[i].suit << std::endl;
            
        }
        
    }
    
}

//Shuffling
void shuffling (std::vector <card> &cards){
    int index = 0;
    card temp;
    for (int i = 1; i< cards.size(); i++){
        std::srand((unsigned int)time(0));
        index =  std::rand() % i;
        if (index <52){
            temp = cards[i-1];
            cards[i-1] = cards[index];
            cards[index] = temp;
        }
    }
}

//A hand
std::vector <card> ahand (std::vector <card> shuffledCard){
    std::vector<card>hand;
    for (int i = 0; i< 5; i++){
        hand.push_back(shuffledCard[i]);
        
    }
    return hand;
}
//isFlush

bool isFlush (std::vector <card> hand){
    for (int i = 0; i< hand.size(); i++){
        if(hand[i].suit != hand[i+1].suit){
            return false;
        }
    } return true; 
   
}
//isStraight

bool isStraightFlush (std::vector <card> hand){
    for (int i = 0; i< hand.size(); i++){
        if ()
    }
}



        



//}




