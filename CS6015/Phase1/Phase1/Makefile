
CXX = g++
all: tests

#build the executable
tests: exp.o tests.o
	$(CXX) -o tests exp.o tests.o

#compile exp.cpp
exp.o: exp.cpp exp.h
	$(CXX) -c exp.cpp

#compile tests.cpp
tests.o: tests.cpp exp.h
	$(CXX) -c tests.cpp

# Clean
	rm -f *.o tests

.PHONY: all clean
