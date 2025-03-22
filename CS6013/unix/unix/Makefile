CXX = g++
CXXFLAGS = -std=c++11 -Wall

# Target executable
TARGET = myshell

# Source files
SRCS = main.cpp shelpers.cpp

# Object files
OBJS = $(SRCS:.cpp=.o)

all: $(TARGET)

# build the executable
$(TARGET): $(OBJS)
	$(CXX) $(OBJS) -o $(TARGET)

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

# Clean
clean:
	rm -f $(OBJS) $(TARGET)

# Optional rule for testing
test: $(TARGET)
	./$(TARGET)
