
# Source files
SRCS = cmdline.cpp expr_test.cpp expr.cpp main.cpp parse.cpp val.cpp env.cpp

# Header files
HEADERS = cmdline.hpp expr.h val.h parse.hpp env.h

# Compiler settings
CXX = g++
CXXFLAGS = -std=c++14 -Wall -Wextra -g

# Default target builds msdscript and test executables
all: msdscript test_msdscript test_expr

# Build the main interpreter
msdscript: $(SRCS) $(HEADERS)
	$(CXX) $(CXXFLAGS) $(SRCS) -o msdscript

# Build the test runner
test_msdscript: test_msdscript.cpp exec.cpp exec.h
	$(CXX) $(CXXFLAGS) test_msdscript.cpp exec.cpp -o test_msdscript

# Build expression tests
test_expr: expr_test.cpp expr.cpp val.cpp parse.cpp env.cpp
	$(CXX) $(CXXFLAGS) expr_test.cpp expr.cpp val.cpp parse.cpp env.cpp -o test_expr

.PHONY: test test_expr_run clean doc

# Run all tests
test: test_expr_run test_msdscript_run

# Run expression tests
test_expr_run: test_expr
	./test_expr

# Run script tests
test_msdscript_run: msdscript test_msdscript
	@echo "Running single script tests with msdscript..."
	./test_msdscript ./msdscript
	@echo "Running comparison tests between msdscript and msdscript2..."
	./test_msdscript ./msdscript ./msdscript2

# Clean build artifacts
clean:
	rm -f msdscript msdscript2 test_msdscript test_expr *.o
	rm -rf html latex

# Generate documentation
doc:
	doxygen Doxyfile
