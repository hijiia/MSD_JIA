#define CATCH_CONFIG_RUNNER  
#include "catch.h"
#include "cmdline.hpp"
#include <iostream>
#include <string>
#include <cstdlib>

run_mode_t use_arguments(int argc, const char* argv[]) {
    bool test_mode = false;
    bool help_mode = false;
    run_mode_t mode = do_nothing;

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
        } else if (arg == "--interp") {
            if (mode != do_nothing) {
                std::cerr << "Error: Multiple modes specified.\n";
                exit(1);
            }
            mode = do_interp;
        } else if (arg == "--print") {
            if (mode != do_nothing) {
                std::cerr << "Error: Multiple modes specified.\n";
                exit(1);
            }
            mode = do_print;
        } else if (arg == "--pretty-print") {
            if (mode != do_nothing) {
                std::cerr << "Error: Multiple modes specified.\n";
                exit(1);
            }
            mode = do_pretty_print;
        } else {
            std::cerr << "Error: Unknown argument '" << arg << "'\n";
            std::cerr << "Use --help for usage information.\n";
            exit(1);
        }
    }

    if (help_mode) {
        std::cout << "Usage: program_name [options]\n";
        std::cout << "Options:\n";
        std::cout << "  --help          : Display this help message\n";
        std::cout << "  --test          : Run tests\n";
        std::cout << "  --version       : Display program version\n";
        std::cout << "  --interp        : Interpret an expression\n";
        std::cout << "  --print         : Print an expression\n";
        std::cout << "  --pretty-print  : Pretty-print an expression\n";
        exit(0);
    }

    if (test_mode) {
        std::cout << "Running tests...\n";
        
        //Use an explicit Catch session
        Catch::Session session;
        int test_result = session.run();
        
        if (test_result != 0) {
            std::cerr << "Tests failed.\n";
            exit(1);
        }
        std::cout << "All tests passed.\n";
        exit(0);
    }

    return mode;
}
