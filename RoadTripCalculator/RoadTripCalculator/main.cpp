//
//  main.cpp
//  RoadTripCalculator
//
//  Created by Jia Gao on 8/20/24.
//

#include <iostream>

int main() {
    int drivingDistance, milesPerGallon, costOfGasPerGallon;
    float result;
    std::cout << "enter drivingDistance:";
    std::cin >> drivingDistance;
    std::cout << "milesPerGallon:";
    std::cin >> milesPerGallon;
    std::cout << "costOfGasPerGallon:";
    std::cin >> costOfGasPerGallon;
    result = (drivingDistance/ milesPerGallon) * costOfGasPerGallon;
    std::cout << "Result:" << result << std::endl;
    return 0;
}
