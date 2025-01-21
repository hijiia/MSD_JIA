//
//  main.cpp
//  Phase1
//

#include <iostream>
#define CATCH_CONFIG_RUNNER
#include "catch.h"
int main(int argc, char **argv) {
    int result = Catch::Session().run(argc, argv);
    return (result == 0) ? 0 : 1;
}
