#build: A TARGET WITHOUT COMMANDS
#output: use c++ compiler, compile object files to executable file called output
#Caps.o, Clothing.o,Pants.o: compile cpp files to object files
#run: run the executable file
#clean: delete all the drived files such as .o .out .output
build: output
	
output: main.cpp Caps.o Clothing.o Pants.o
	c++ main.cpp Caps.o Clothing.o Pants.o -o output
Caps.o: Caps.cpp Caps.h
	c++ -c Caps.cpp
Clothing.o: Clothing.cpp Clothing.h
	c++ -c Clothing.cpp
Pants.o: Pants.cpp Pants.h
	c++ -c Pants.cpp

run: output
	./output

clean:
	rm -f *.o *.out *output
