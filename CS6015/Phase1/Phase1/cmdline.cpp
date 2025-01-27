//
//  cmdline.cpp
//  Phase1
//
#define CATCH_CONFIG_RUNNER
#include "catch.h"
#include "cmdline.h"
#include <iostream>
#include <string.h>
#include <stdio.h>


void use_arguments(int argc, const char* argv[]){
    bool test = false;
    
    for (int i = 1; i < argc; i++){
        std::string argv_text = argv[i];
        if (argv_text == "--help"){
            std::cout << " --test: to test the program\n";
            std::cout << " --help: print out help message\n";
            exit(0);
        }else if (argv_text == "--test"){
            if (!test){
                std::cout << "...running the tests";
                int result = Catch::Session().run();
                if (result != 0) exit(1);
                exit(0);
            }else {
                std::cerr << "have tested\n";
                exit(1);
            }
        }else {
            std::cerr << "Not applied argument!\n";
            exit(1);
        }
    }

    if (!test){
        exit(0);
    }
    
}
