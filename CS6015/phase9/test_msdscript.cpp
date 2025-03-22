#include <iostream>
#include <cstdlib>
#include <string>
#include <stdexcept>
#include "exec.h"

std::string random_expr_string() {
    if ((rand() % 10) < 6)
        return std::to_string(rand() % 100);
    else
        return "(" + random_expr_string() + (rand() % 2 ? " + " : " * ") + random_expr_string() + ")";
}

int main(int argc, char **argv) {
    srand((unsigned int)time(nullptr));  // Seed random number generator

    if (argc == 2) {
        // Single msdscript: test print and interp consistency
        const char * const interp_argv[] = { "msdscript", "--interp" };
        const char * const print_argv[] = { "msdscript", "--print" };

        for (int i = 0; i < 100; i++) {
            std::string in = random_expr_string();
            std::cout << "Trying: " << in << "\n";

            ExecResult interp_result = exec_program(2, interp_argv, in+ "\n1\n");
            ExecResult print_result = exec_program(2, print_argv, in+ "\n1\n");
            ExecResult interp_again_result = exec_program(2, interp_argv, print_result.out+ "\n1\n");

            if (interp_again_result.out != interp_result.out) {
                std::cerr << "Different result for printed expression!\n";
                std::cerr << "Original interp: " << interp_result.out;
                std::cerr << "Re-interpreted after print: " << interp_again_result.out;
                throw std::runtime_error("Test failed.");
            }
        }
        std::cout << "All tests passed\n";

    } else if (argc == 3) {
        // Compare two msdscript executables with interp
        const char * const interp1_argv[] = { argv[1], "--interp" };
        const char * const interp2_argv[] = { argv[2], "--interp" };

        for (int i = 0; i < 100; i++) {
            std::string in = random_expr_string();
            std::cout << "Trying: " << in << "\n";

            ExecResult interp1_result = exec_program(2, interp1_argv, in);
            ExecResult interp2_result = exec_program(2, interp2_argv, in);

            if (interp1_result.out != interp2_result.out) {
                std::cerr << "Different results found!\n";
                std::cerr << "Input: " << in << "\n";
                std::cerr << "Program 1 output: " << interp1_result.out;
                std::cerr << "Program 2 output: " << interp2_result.out;
                throw std::runtime_error("Mismatch between scripts.");
            }
        }
        std::cout << "No differences found between the two programs.\n";

    } else {
        std::cerr << "Usage:\n";
        std::cerr << "  " << argv[0] << " <msdscript_path>\n";
        std::cerr << "  " << argv[0] << " <msdscript_path1> <msdscript_path2>\n";
        return 1;
    }

    return 0;
}
