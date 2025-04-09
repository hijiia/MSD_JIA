
#ifndef CMDLINE_HPP
#define CMDLINE_HPP

typedef enum {
    do_nothing,
    do_test,
    do_interp,
    do_print,
    do_pretty_print
} run_mode_t;

run_mode_t use_arguments(int argc, const char* argv[]);

#endif /* cmdline_hpp */
