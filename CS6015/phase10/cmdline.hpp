#ifndef cmdline_hpp
#define cmdline_hpp
#include "catch.h"

typedef enum {
    do_nothing,
    do_interp,
    do_print,
    do_pretty_print,
    do_test
} run_mode_t;

run_mode_t use_arguments(int argc, const char **argv);

#endif
