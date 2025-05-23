#include <iostream>
#include <cstdlib>
#include <vector>
#include <string>
#include <ctime>
#include <unistd.h>
#include "exec.h"

// Configuration structure
struct TestConfig {
    int num_tests = 100;
    int max_depth = 5; // Maximum depth of randomly generated expressions.
    bool verbose = false;
    std::vector<std::string> test_modes = {"--interp", "--print", "--pretty-print"};
};

// Function declarations
std::string random_expr_string(int depth = 0); //Generates random test expressions
void run_single_test(const std::string &script_path, const TestConfig &config); // Tests a single msdscript for correctness.
void run_comparison_test(const std::string &script1, const std::string &script2, const TestConfig &config); // Compares two msdscript implementations to find discrepancies
bool check_executable(const std::string &path); //Checks if a given executable file is valid

int main(int argc, char *argv[]) {
    try {
        TestConfig config;
        srand(time(nullptr));  // seed the random number generator for different test cases each run.

        if (argc == 2) {
            if (!check_executable(argv[1])) {
                std::cerr << "Error: Cannot execute " << argv[1] << "\n";
                return 1;
            }
            run_single_test(argv[1], config);
        } else if (argc == 3) {
            if (!check_executable(argv[1]) || !check_executable(argv[2])) {
                std::cerr << "Error: Cannot execute one or both scripts\n";
                return 1;
            }
            run_comparison_test(argv[1], argv[2], config);
        } else {
            std::cerr << "Usage: test_msdscript <msdscript_path> or test_msdscript <msdscript1> <msdscript2>\n";
            return 1;
        }
        return 0;
    } catch (const std::exception& e) {
        std::cerr << "Fatal error: " << e.what() << "\n";
        return 1;
    }
}

std::string random_expr_string(int depth) {
    // When max depth is reached, only return numbers
    if (depth >= TestConfig().max_depth) {
        return std::to_string(rand() % 100);
    }
    
    // Increase probability of generating numbers as depth increases
    int choice = rand() % (4 + depth); // Generates a random number between 0 and 3
    if (choice < 2) {
        return std::to_string(rand() % 100);
    }
    
    // Generate variable names
    static const std::vector<std::string> vars = {"x", "y", "z", "w"};
    
    switch (rand() % 4) { // Generates a random number between 0 and 3
        case 0: // Inserts " + " between them to form an addition expression, e.g.:"35 + (x + 10)"
            return random_expr_string(depth + 1) + " + " + random_expr_string(depth + 1);
        case 1: // Similar to case 0, but inserts " * " to form a multiplication expression, e.g.:"(y * (4 + 8))"
            return random_expr_string(depth + 1) + " * " + random_expr_string(depth + 1);
        case 2: {
            // Add parentheses sometimes
            if (rand() % 2 == 0) {
                return "(" + random_expr_string(depth + 1) + ")"; // randomly decides whether to add parentheses around a sub-expression
            } else {
                return random_expr_string(depth + 1);
            }
        }
        default: {
            // Use different variable names
            std::string var = vars[rand() % vars.size()];
            return "(_let " + var + " = " + random_expr_string(depth + 1) +
                   " _in " + random_expr_string(depth + 1) + ")"; // "(_let x = 5 _in (x + 3))"
        }
    }
}

void run_single_test(const std::string &script_path, const TestConfig &config) {
    int tests_passed = 0;
    
    for (int i = 0; i < config.num_tests; i++) {
        try {
            std::string test_expr = random_expr_string(0); //Generates a random test expression
            
            if (config.verbose) {
                std::cout << "Test " << (i + 1) << "/" << config.num_tests
                         << ": " << test_expr << "\n";
            }
            
            // Runs msdscript in interpreter mode (--interp).
            const char *argv[] = { script_path.c_str(), "--interp", NULL };
            ExecResult result = exec_program(2, argv, test_expr);
            
            if (result.exit_code != 0) {
                std::cerr << "Error on test " << (i + 1) << ":\n"
                         << "Input: " << test_expr << "\n"
                         << "stderr: " << result.err << "\n";
            } else {
                tests_passed++;
                if (config.verbose) {
                    std::cout << "Output: " << result.out << "\n";
                }
            }
        } catch (const std::exception& e) {
            std::cerr << "Test " << (i + 1) << " failed with error: " << e.what() << "\n";
        }
    }
    
    std::cout << "\nTest Summary:\n"
              << "Total tests: " << config.num_tests << "\n"
              << "Tests passed: " << tests_passed << "\n"
              << "Success rate: " << (tests_passed * 100.0 / config.num_tests) << "%\n";
}

void run_comparison_test(const std::string &script1, const std::string &script2,
                        const TestConfig &config) {
    int tests_run = 0;
    int tests_passed = 0;
    
    for (int i = 0; i < config.num_tests; i++) {
        try {
            std::string test_expr = random_expr_string(0);
            tests_run++;
            
            if (config.verbose) {
                std::cout << "Test " << (i + 1) << "/" << config.num_tests
                         << ": " << test_expr << "\n";
            }
            
            bool test_passed = true;
            for (const auto &mode : config.test_modes) {
                const char *argv1[] = { script1.c_str(), mode.c_str(), NULL };
                const char *argv2[] = { script2.c_str(), mode.c_str(), NULL };
                
                ExecResult result1 = exec_program(2, argv1, test_expr);
                ExecResult result2 = exec_program(2, argv2, test_expr);
                
                if (result1.out != result2.out ||
                    result1.exit_code != result2.exit_code) {
                    std::cerr << "\nDiscrepancy found in test " << (i + 1) << ":\n"
                             << "Mode: " << mode << "\n"
                             << "Input: " << test_expr << "\n"
                             << script1 << " output: " << result1.out << "\n"
                             << script2 << " output: " << result2.out << "\n"
                             << script1 << " exit code: " << result1.exit_code << "\n"
                             << script2 << " exit code: " << result2.exit_code << "\n";
                    test_passed = false;
                    break;
                }
            }
            
            if (test_passed) {
                tests_passed++;
                if (config.verbose) {
                    std::cout << "Test passed\n";
                }
            }
        } catch (const std::exception& e) {
            std::cerr << "Test " << (i + 1) << " failed with error: " << e.what() << "\n";
        }
    }
    
    std::cout << "\nComparison Test Summary:\n"
              << "Total tests: " << tests_run << "\n"
              << "Tests passed: " << tests_passed << "\n"
              << "Success rate: " << (tests_passed * 100.0 / tests_run) << "%\n";
}

bool check_executable(const std::string &path) {
    return access(path.c_str(), X_OK) == 0;
}

//Generate a random expression using random_expr_string.
//Execute msdscript in --interp mode to interpret the expression.
//If exit_code != 0, report an error.
//Record the number of passed tests if the execution is successful.
//Finally, output the test results summarizing the success rate.
