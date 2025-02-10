CXX = clang++
CXXFLAGS = -std=c++11 -Wall -Wextra

all: tests

# Build the executable
tests: exp.o test.o
	$(CXX) $(CXXFLAGS) -o tests exp.o test.o

# Compile exp.cpp
exp.o: exp.cpp exp.h
	$(CXX) $(CXXFLAGS) -c exp.cpp

# Compile tests.cpp
test.o: test.cpp exp.h
	$(CXX) $(CXXFLAGS) -c test.cpp

# Clean
clean:
	rm -f *.o tests

.PHONY: all clean
