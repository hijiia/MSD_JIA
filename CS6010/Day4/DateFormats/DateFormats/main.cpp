//
//  main.cpp
//  DateFormats
//
//  Created by Jia Gao on 8/22/24.
#include <iostream>
#include <string>
int main(int argc, const char * argv[]) {
//input variable
  std::string usDate;
  int month;
  int day;
  int year;
  std::string monthName;
  std::string dayString;
  std::string yearString;
  //taking user input
  std::cout<<"Enter a date in the format mm/dd/yyyy :";
  std::cin>>usDate;
  //making all checks on the date format
  if(usDate.size()>10){
    std::cout<<"recheck the date!\n";
    return 1;
  }
  if (usDate.find("/")==std::string::npos){
    std::cout<<"wrong format\n";
    return 1;
  }
  if(usDate.find("/")!=2){
    std::cout<<"Please enter day and month in the right format\n";
    return 1;
  }
  if((usDate.find('/',usDate.find('/')+1))!=5){
    std::cout<<"Please enter year in the right format\n";
    return 1;
  }
  //convert the date to integer
  month=std::stoi(usDate.substr(0,2));
  day=std::stoi(usDate.substr(3,2));
  year=std::stoi(usDate.substr(6,4));
  //convert to separate strings to print
  dayString=std::to_string(day);
  yearString=std::to_string(year);
  //doing all checks on the validity of the date
  if (month>12||month<1){
    std::cout<<"Invalid month\n";
    return 1;
  }
  if (day<1||day>31){
    std::cout<<"Invalid day\n";
    return 1;
  }
  if(year<1000||year>9999){
    std::cout<<"Invalid year\n";
    return 1;
  }
  //printing out the date
  std::cout<<"Date in English format:\n";
  //equating all corresponding months
  if (month==1){
    monthName="January";
  }else if(month==2){
    monthName="February";
  }else if(month==3){
    monthName="March";
  }else if(month==4){
    monthName="April";
  }else if(month==5){
    monthName="May";
  }else if(month==6){
    monthName="June";
  }else if(month==7){
    monthName="July";
  }else if(month==8){
    monthName="August";
  }else if(month==9){
    monthName="September";
  }else if(month==10){
    monthName="October";
  }else if(month==11){
    monthName="November";
  }else if(month==12){
    monthName="December";
  }
  //printing the right format
  std::cout<<monthName+' '+dayString+','+yearString+"\n";
  return 0;



    return 0;
}
