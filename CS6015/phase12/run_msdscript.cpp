#include <iostream>
#include <stdlib.h>
#include "exec.h"

static std::string random_expr_string(int depth = 0) {
    // Set maximum recursion depth
    const int MAX_DEPTH = 5;
    
    // When max depth is reached, only return numbers
    if (depth >= MAX_DEPTH) {
        return std::to_string(rand() % 100);
    }
    
    // Increase probability of generating numbers as depth increases
    int choice = rand() % (3 + depth);
    if (choice < 1) {
        return std::to_string(rand() % 100);
    }
    
    switch (rand() % 2) {
        case 0:
            return random_expr_string(depth + 1) + " + " + random_expr_string(depth + 1);
        default:
            return random_expr_string(depth + 1) + " * " + random_expr_string(depth + 1);
    }
}

int main(int /*argc*/, char ** /*argv*/) {
    // Initialize random seed
    srand(time(nullptr));
    
    const char * const interp_argv[] = { "msdscript", "--interp" };
    const char * const print_argv[] = { "msdscript", "--print" };
    
    for (int i = 0; i < 100; i++) {
        try {
            std::string in = random_expr_string();
            std::cout << "Test " << (i + 1) << "/100: " << in << "\n";
            
            ExecResult interp_result = exec_program(2, interp_argv, in);
            if (interp_result.exit_code != 0) {
                std::cerr << "Interpreter error: " << interp_result.err << "\n";
                continue;
            }
            
            ExecResult print_result = exec_program(2, print_argv, in);
            if (print_result.exit_code != 0) {
                std::cerr << "Print error: " << print_result.err << "\n";
                continue;
            }
            
            ExecResult interp_again_result = exec_program(2, interp_argv, print_result.out);
            if (interp_again_result.exit_code != 0) {
                std::cerr << "Second interpreter error: " << interp_again_result.err << "\n";
                continue;
            }
            
            if (interp_again_result.out != interp_result.out) {
                std::cerr << "Results don't match!\n"
                         << "Original interpretation: " << interp_result.out << "\n"
                         << "Printed form: " << print_result.out << "\n"
                         << "Re-interpretation: " << interp_again_result.out << "\n";
                throw std::runtime_error("different result for printed expression");
            }
            
            std::cout << "Result: " << interp_result.out << "\n";
        } catch (const std::exception& e) {
            std::cerr << "Error: " << e.what() << "\n";
            return 1;
        }
    }
    
    std::cout << "\nAll tests passed successfully!\n";
    return 0;
}
