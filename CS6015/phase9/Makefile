
SRCS = cmdline.cpp expr_test.cpp expr.cpp main.cpp parse.cpp val.cpp
# HEADERS = cmdline.hpp expr.h catch.h val.h
HEADERS = cmdline.hpp expr.h val.h
CXX = g++
CXXFLAGS = -std=c++11

# Default target builds msdscript and test_msdscript
all: msdscript test_msdscript

# Build the original msdscript
msdscript: $(SRCS) $(HEADERS)
	$(CXX) $(CXXFLAGS) $(SRCS) -o msdscript

# Build test_msdscript
test_msdscript: test_msdscript.cpp exec.cpp exec.h
	$(CXX) $(CXXFLAGS) test_msdscript.cpp exec.cpp -o test_msdscript

.PHONY: test clean doc

# Run tests
test: msdscript test_msdscript
	@echo "Running single script tests with msdscript..."
	./test_msdscript ./msdscript
	@echo "Running comparison tests between msdscript and msdscript2..."
	./test_msdscript ./msdscript ./msdscript2

clean:
	rm -f msdscript msdscript2 test_msdscript *.o
	rm -rf html latex

doc:
	doxygen msdscript
