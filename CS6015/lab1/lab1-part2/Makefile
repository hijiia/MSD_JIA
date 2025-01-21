# Compiler and options
CXX = c++
CFLAGS = -c -Wall -std=c++11
LDFLAGS = -o
# Define source and header files
CXXSOURCE = $(wildcard *.cpp)
HEADERS = $(wildcard *.h)
# genaerate .o list
OBJECTS = Caps.o Pants.o Clothing.o main.o
#object files
Caps.o: Caps.cpp Caps.h
	$(CXX) $(CFLAGS) Caps.cpp -o Caps.o
Pants.o: Pants.cpp Pants.h
	$(CXX) $(CFLAGS) Pants.cpp -o Pants.o
Clothing.o: Clothing.cpp Clothing.h
	$(CXX) $(CFLAGS) Clothing.cpp -o Clothing.o
main.o: main.cpp
	$(CXX) $(CFLAGS) main.cpp -o main.o


output: $(OBJECTS)
	$(CXX) $(OBJECTS) $(LDFLAGS) output
	
build: output

run: build
	./output	
	
clean:
	rm -f *.o *.out *.output
	
printM:
	echo "Sourcefiles: $(CXXSOURCE)"
	echo "Headerfiles: $(HEADERS)"
	echo "Compiler: $(CXX)"
	echo "Compiler Flags: $(CXXFLAGS)"
	echo "Linker Flags: $(LDFLAGS)"
	echo "Object Files: $(OBJECTS)"
