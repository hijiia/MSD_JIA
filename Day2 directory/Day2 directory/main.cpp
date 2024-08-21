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
    std::cout << "change: " << change;
    quarters = change / 25;
    dimes = change % 25 / 10;
    nickels = change % 25 % 10 / 5;
    pennies = change - quarters * 25 - dimes * 10 - nickels * 5;
    std::cout << "quarters: " << quarters;
    std::cout << "dimes: " << dimes;
    std::cout << "nickels: " << nickels;
    std::cout << "pennies: " << pennies << std::endl;
    return 0;
}
