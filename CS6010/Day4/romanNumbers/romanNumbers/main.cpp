//
//  main.cpp
//  romanNumbers
//
//  Created by Jia Gao on 8/23/24.
//

#include <iostream>
using namespace std ;

int main()
{
    int number ;
    cout << "Enter decimal number: "<< endl ;
    cin >> number ;
    if(number < 0)
        cout << "Invalid input" << endl  ;
    else
    {
        cout << "Roman numeral version:" << endl ;
        while(number > 0)
        {
            if(number >= 1000)
            {
                cout << 'M' ;
                number -= 1000 ;
            }
            else if(number >= 900)
            {
                cout <<'C'<<'M' ;
                number -= 900 ;
            }
            else if(number >= 500)
            {
                cout << 'D' ;
                number -= 500 ;
            }
            else if(number >= 400)
            {
                cout <<'C'<<'D' ;
                number -= 400 ;
            }
            else if(number >= 100)
            {
                cout << 'C' ;
                number -= 100 ;
            }
            else if(number >= 90)
            {
                cout <<'X'<<'C' ;
                number -= 1000 ;
            }
            else if(number >= 50)
            {
                cout << 'L' ;
                number -= 50 ;
            }
            else if(number >= 40)
            {
                cout <<'X'<<'L' ;
                number -= 40 ;
            }
            else if(number >= 10)
            {
                cout << 'X' ;
                number -= 10 ;
            }
            else if(number >= 9)
            {
                cout <<'I'<<'X' ;
                number -= 9 ;
            }
            else if(number >= 5)
            {
                cout << 'V' ;
                number -= 5 ;
            }
            else if(number >= 4)
            {
                cout <<'I'<<'V' ;
                number -= 4 ;
            }
            else
            {
                cout << 'I' ;
                number -= 1 ;
            }
        }
    }
    cout << endl ;
    return 0;
}

