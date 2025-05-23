
# Compiler settings
CXX = clang++
CXXFLAGS = -std=c++11 -Wall -Wextra -arch arm64 -g
LDFLAGS =

# Source files
SRCS = expr.cpp tests.cpp cmdline.cpp main.cpp val.cpp
OBJS = $(SRCS:.cpp=.o)
TEST_OBJS = test_msdscript.cpp exec.cpp
RUN_OBJS = run_msdscript.cpp exec.cpp
RUN2_OBJS = run_msdscript2.cpp exec.cpp
TARGET = msdscript

# Versioning
VERSION = 1.0.0
BUILD_DATE = $(shell date +%Y%m%d)

# Default target: build msdscript
all: $(TARGET) test_msdscript run_msdscript run_msdscript2

# Build msdscript
$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(OBJS) $(LDFLAGS)
	chmod +x $(TARGET)

# Compile individual object files
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

# Build test_msdscript
test_msdscript: $(TEST_OBJS)
	$(CXX) $(CXXFLAGS) $(TEST_OBJS) -o $@
	chmod +x $@

# Build run_msdscript
run_msdscript: $(RUN_OBJS)
	$(CXX) $(CXXFLAGS) $(RUN_OBJS) -o $@
	chmod +x $@

# Build run_msdscript2
run_msdscript2: $(RUN2_OBJS)
	$(CXX) $(CXXFLAGS) $(RUN2_OBJS) -o $@
	chmod +x $@

# Run tests
.PHONY: test
test: test_msdscript $(TARGET)
	./test_msdscript ./$(TARGET)
	./test_msdscript ./$(TARGET) ./$(TARGET)

# Install target
.PHONY: install
install: $(TARGET)
	install -m 755 $(TARGET) /usr/local/bin/

# Version information
.PHONY: version
version:
	@echo "Version: $(VERSION)"
	@echo "Build date: $(BUILD_DATE)"

# Clean up all generated files
.PHONY: clean
clean:
	rm -f $(OBJS) $(TARGET) test_msdscript run_msdscript run_msdscript2 *.o

# Help target
.PHONY: help
help:
	@echo "Available targets:"
	@echo "  all            - Build all targets"
	@echo "  test          - Run tests"
	@echo "  install       - Install to /usr/local/bin"
	@echo "  clean         - Remove generated files"
	@echo "  version       - Show version information"
	@echo "  help          - Show this help message"
