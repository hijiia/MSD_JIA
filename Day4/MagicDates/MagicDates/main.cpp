//
//  main.cpp
//  MagicDates
//
//  Created by Jia Gao on 8/22/24.
//

#include <iostream>
int main(int argc, const char * argv[]) {
  // insert code here...
  std::string usdate;
  bool isVaild = false;
  std::cout<<"please enter a date (mm/dd/yyyy)."<<std::endl;
  std::cin>>usdate;
  if (usdate[2] != '/'){
    std::cout<<"Invalid date"<<std::endl;
  }
  else if (usdate[5] != '/'){
    std::cout<<"Invalid date"<<std::endl;
  }
  else {
    isVaild = true ;
    // std::cout<<"right date"<<std::endl;
  }
  std::string month= usdate.substr(0,2);
  int M= stoi(month);{
    //std::cout<<M<<std::endl;
  }
  std::string day= usdate.substr(3,2);
  int D= stoi(day);{
    //std::cout<<D<<std::endl;
  }
  std::string year= usdate.substr(6,4);
  int Y= stoi(year);{
    //std::cout<<Y<<std::endl;
  }
  if (M >12||M <1){
    std::cout<<"Invalid date"<<std::endl;
    return 1;
  }
  else if (D<1||D>31){
    std::cout<<"Invalid date"<<std::endl;
    return 1;
  }
  else if(Y<1000||Y>9999){
    std::cout<<"Invalid date"<<std::endl;
    return 1;
  }
  int Num = M * D;
  std::string Num_str = std::to_string(Num);
  std::string Year= usdate.substr(8,2);
  if (Num_str == Year ){
    std::cout<< usdate + " IS a magic date"<<std::endl;
  }
  else {
    std::cout<< usdate + " IS not a magic date"<<std::endl;
  }
  return 0;
}


