//
//  main.cpp
//  Day2 directory
//
//  Created by Jia Gao on 8/20/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    int itemPrice, moneyPaid, change;
    int quarters, dimes, nickels, pennies;
    std::cout << "itemPrice:";
    std::cin >> itemPrice;
    std::cout << "moneyPaid:";
    std::cin >> moneyPaid;
    change = moneyPaid - itemPrice;
    if(moneyPaid<0 || itemPrice<0){
        std::cout << "number of cents can not be negative.";
    }
    if(moneyPaid>0 && itemPrice>0 && moneyPaid<itemPrice){
        std::cout << "Insufficient funds!.";
    }
    if(moneyPaid>0 && itemPrice>0 && moneyPaid>itemPrice){
        std::cout << "change: " << change << std::endl;
        
        
        std::cout <<"now I'm printing out coins available to return" <<std::endl;
        int COINS_AVAILABLE = 2;
        int availableQuarters = COINS_AVAILABLE;
        int availableDimes = COINS_AVAILABLE;
        int availableNickels = COINS_AVAILABLE;
        int availablePennies = COINS_AVAILABLE;
        quarters = 0;
        dimes = 0;
        nickels = 0;
        pennies = 0;
        while (change >= 25 && availableQuarters > 0) {
            quarters++;
            change -= 25;
            availableQuarters--;
        }
        while (change >= 10 && availableDimes > 0) {
            dimes++;
            change -= 10;
            availableDimes--;
        }
        while (change >= 5 && availableNickels > 0) {
            nickels++;
            change -= 5;
            availableNickels--;
        }
        while (change >= 1 && availablePennies > 0) {
            pennies++;
            change -= 1;
            availablePennies--;
        }
        if (change > 0) {
            std::cout << "Unable to return change, out of coins!" << std::endl;
        } else {
            std::cout << "Coins returned:" << std::endl;
            std::cout << "Quarters: " << quarters << std::endl;
            std::cout << "Dimes: " << dimes << std::endl;
            std::cout << "Nickels: " << nickels << std::endl;
            std::cout << "Pennies: " << pennies << std::endl;
        }
    }
}
