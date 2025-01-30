//
//  cmdline.cpp
//  Phase1
//
#define CATCH_CONFIG_RUNNER
#include "catch.h"
#include "cmdline.h"
#include <iostream>
#include <string>
#include <cstdlib>

void use_arguments(int argc, const char* argv[]) {
    bool test_mode = false;
    bool help_mode = false;

    for (int i = 1; i < argc; i++) {
        std::string arg = argv[i];

        if (arg == "--help") {
            help_mode = true;
        } else if (arg == "--test") {
            if (test_mode) {
                std::cerr << "Error: Tests have already been run.\n";
                exit(1);
            }
            test_mode = true;
        } else if (arg == "--version") {
            std::cout << "Program Version 1.0\n";
            exit(0);
        } else {
            std::cerr << "Error: Unknown argument '" << arg << "'\n";
            std::cerr << "Use --help for usage information.\n";
            exit(1);
        }
    }

    if (help_mode) {
        std::cout << "Usage: program_name [options]\n";
        std::cout << "Options:\n";
        std::cout << "  --help     : Display this help message\n";
        std::cout << "  --test     : Run tests\n";
        std::cout << "  --version  : Display program version\n";
        exit(0);
    }

    if (test_mode) {
        std::cout << "Running tests...\n";
        int test_result = Catch::Session().run();
        if (test_result != 0) {
            std::cerr << "Tests failed.\n";
            exit(1);
        }
        std::cout << "All tests passed.\n";
        exit(0);
    }

    // no specific mode, proceed normal execution
    std::cout << "Running program in normal mode...\n";
}
