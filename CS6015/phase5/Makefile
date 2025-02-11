CXX = clang++
CXXFLAGS = -std=c++11 -Wall -Wextra -arch arm64
LDFLAGS =

SRCS = exp.cpp tests.cpp cmdline.cpp main.cpp
OBJS = $(SRCS:.cpp=.o)
TARGET = tests

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(OBJS) $(LDFLAGS)
	chmod +x $(TARGET)  # has execute permissions

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS) $(TARGET)

.PHONY: all clean
