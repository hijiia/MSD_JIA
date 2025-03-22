
#define CATCH_CONFIG_RUNNER
#include <iostream>
#include "cmdline.hpp"
#include "expr.h"
#include "parse.hpp"
#include "catch.h"

int main(int argc, const char *argv[]) {
    run_mode_t mode = use_arguments(argc, argv);

    try {
        if (mode == do_interp) {
            Expr *e = parse_expr(std::cin);
            std::cout << e->interp() << std::endl;
        } else if (mode == do_print) {
            Expr *e = parse_expr(std::cin);
            std::cout << "(" << e->pretty_print() << ")" <<  std::endl;
        } else if (mode == do_pretty_print) {
            Expr *e = parse_expr(std::cin);
            std::cout << e->pretty_print() << std::endl;
        } else if (mode == do_test) {
            int result = Catch::Session().run();
            return result == 0 ? 0 : 1;
        }
        return 0;
    } catch (std::runtime_error exn) {
        std::cerr << exn.what() << "\n";
        return 1;
    }
}
