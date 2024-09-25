//
//  main.cpp
//  Cards
//
//  Created by Jia Gao on 8/27/24.
//
#include <iostream>
#include <vector>
#include <string>


struct card{ int rank;// ACE TO KING
    std::string name ;// "Hearts", "Diamonds", "Clubs", "Spades"
    std::string color;};// RED BLACK


std::vector<card> initializeDeck() {
std::vector<card> deck;

        // Hearts
        for (int rank = 1; rank <= 13; rank++) {
                deck.push_back(card{rank, "Hearts", "Red"});
            }

        // Diamonds
        for (int rank = 1; rank <= 13; rank++) {
                deck.push_back(card{rank, "Diamonds", "Red"});
            }

        
        // Clubs
        for (int rank = 1; rank <= 13; rank++) {
                deck.push_back(card{rank, "Clubs", "Black"});
            }

        
        // Spades
        for (int rank = 1; rank <= 13; rank++) {
                deck.push_back(card{rank, "Spades", "Black"});
            }
    return deck;
    }

void PrintDeck(std::vector<card> deck){
    for (int i =0; i<deck.size(); i++){
        std::cout<<deck[i].name<<":"<<deck[i].rank<< std::endl;
    }
}

int main(int argc, const char * argv[]) {
    // insert code here...
    
    
    std::vector<card> newdeck;
    newdeck=initializeDeck();
    
    PrintDeck(newdeck);
}
