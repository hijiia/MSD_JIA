//
//

#include <iostream>
#include <sstream>
#include "cmdline.h"
#include "exp.h"

int main(int argc, const char *argv[]) {
    run_mode_t mode = use_arguments(argc, argv); // mode from command-line

    if (mode == do_nothing) {
        return 0; // Exit if no valid
    }

    std::string input;
    std::getline(std::cin, input); // input

    try {
        Expr* expr = parse_str(input); // Parse the expression

        switch (mode) {
            case do_interp:
                std::cout << expr->interp() << "\n"; // Interpret and print
                break;
            case do_print:
                expr->printExp(std::cout);
                std::cout << "\n"; // Print the expression normally
                break;
            case do_pretty_print:
                std::cout << expr->to_pretty_string() << "\n"; // Pretty-print
                break;
            default:
                break;
        }

        delete expr; // Free memory
    } catch (std::exception &e) {
        std::cerr << "Error: " << e.what() << "\n";
        return 1;
    }

    return 0;
}
