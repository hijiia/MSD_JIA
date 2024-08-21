//
//  main.cpp
//  IfStatementPART 2
//
//  Created by Jia Gao on 8/21/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    char weekdayResponse, holidayResponse, childrenResponse;
    std::cout << "Is it a weekday:\n";
    std::cin >> weekdayResponse;
    std::cout << "Is it a holiday:\n";
    std::cin >> holidayResponse;
    std::cout << "Do you have young children:\n";
    std::cin >> childrenResponse;
    
    // create boolean variable
    bool isWeekday;
    bool isHoliday;
    bool haveChildren;
    
    if(weekdayResponse == 'Y'){
        isWeekday = true;
    } else{
        isWeekday = false;
    }
    
    if(holidayResponse == 'Y'){
        isHoliday = true;
    } else{
        isHoliday = false;
    }
    if(childrenResponse =='Y'){
        haveChildren=true;
    }else{
        haveChildren = false;
    }
    
    if (isWeekday && !isHoliday && haveChildren){
        std::cout <<"Busy day.\n";
    }else{
        std::cout << "You can get sleep.\n";
    }
        
    return 0;
}
