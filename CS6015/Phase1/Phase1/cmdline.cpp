//
//  cmdline.cpp
//  Phase1
//
#include <iostream>
#include <string>
#include <vector>
#include <cstdlib>

void use_arguments(int argc, char* argv[]) {
    bool testAppeared = false; // test been seen

    for (int i = 1; i < argc; ++i) {
        std::string arg = argv[i];

        if (arg == "--help") {
            std::cout << "test commands"
                      << "--help: Show help message.\n"
                      << "--test: Run tests.\n";
            exit(0); // exit after showing help
        }
        else if (arg == "--test") {
            if (testAppeared) {
                std::cerr << "Error: '--test' argument run multiple times.\n";
                exit(1);
            }
            std::cout << "Tests passed\n";
            testAppeared = true; // test seen
        }
        else {
            std::cerr << "Error: Unknown argument '" << arg << "'.\n";
            exit(1);
        }
    }
}
