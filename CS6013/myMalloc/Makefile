all: tests benchmark

tests: tests.cpp MyMalloc.cpp HashTable.cpp
		g++ -o tests tests.cpp MyMalloc.cpp HashTable.cpp -std=c++17

benchmark: benchmark.cpp MyMalloc.cpp HashTable.cpp
		g++ -o benchmark benchmark.cpp MyMalloc.cpp HashTable.cpp -std=c++17

clean:
		rm -f tests benchmark
