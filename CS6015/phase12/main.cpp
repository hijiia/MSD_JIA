#include <iostream>
#include <sstream>
#include "cmdline.hpp"
#include "expr.h"
#include "val.h"
#include "pointer.h"
#include "env.h"

int main(int argc, const char *argv[]) {
    try {
        run_mode_t mode = use_arguments(argc, argv);

        if (mode == do_nothing) {
            return 0;
        }
        
        std::string input;
        std::getline(std::cin, input);
        PTR(Expr) expr = parse_str(input);

        switch (mode) {
            case do_interp: {
                PTR(Val) result = expr->interp(Env::empty);  // Pass empty
                std::cout << result->to_string() << "\n";
                break;
            }
            case do_print:
                expr->printExp(std::cout);
                std::cout << "\n";
                break;
            case do_pretty_print:
                std::cout << expr->to_pretty_string() << "\n";
                break;
            default:
                break;
        }

        return 0; 

    } catch (const std::runtime_error &exn) {
        std::cerr << "Error: " << exn.what() << "\n";
        return 1; // Exit with code 1 on runtime errors
    } catch (const std::exception &exn) {
        std::cerr << "Unexpected Error: " << exn.what() << "\n";
        return 1; // Exit with code 1 on other exceptions
    }
}
