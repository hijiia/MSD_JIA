CXX = g++
CXXFLAGS = -std=c++17 -Wall -g

# Object files for main application
APP_OBJS = main.o exp.o

# Object files for tests
TEST_OBJS = test.o exp.o

# Build the main application
msdscript: $(APP_OBJS)
	$(CXX) $(CXXFLAGS) -o msdscript $(APP_OBJS)

# Build the tests
test: $(TEST_OBJS)
	$(CXX) $(CXXFLAGS) -o test $(TEST_OBJS)

# Compile main.cpp
main.o: main.cpp exp.h
	$(CXX) $(CXXFLAGS) -c main.cpp

# Compile test.cpp
test.o: test.cpp exp.h
	$(CXX) $(CXXFLAGS) -c test.cpp

# Compile exp.cpp
exp.o: exp.cpp exp.h
	$(CXX) $(CXXFLAGS) -c exp.cpp

# Clean build files
clean:
	rm -f *.o msdscript test
