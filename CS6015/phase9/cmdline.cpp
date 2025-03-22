#include "cmdline.hpp"
#include <iostream>
#include <string.h>
#include <stdio.h>


run_mode_t use_arguments(int argc, const char **argv) {
    if (argc != 2) {
        std::cerr << "Usage: msdscript --test | --interp | --print | --pretty-print \n";
        exit(1);
    }

    std::string arg = argv[1];

    if (arg == "--test") {
        return do_test;
    } else if (arg == "--interp") {
        return do_interp;
    } else if (arg == "--print") {
        return do_print;
    } else if (arg == "--pretty-print") {
        return do_pretty_print;
    } else {
        std::cerr << "Invalid argument.\n";
        exit(1);
    }
}

