#include "MyMalloc.hpp"
#include <iostream>
#include <chrono>
#include <cstdlib>

void benchmark() {
    MyMalloc myAlloc;
    size_t numAllocations = 100000;
    void* pointers[numAllocations];

    auto start = std::chrono::high_resolution_clock::now();
    for (size_t i = 0; i < numAllocations; i++) {
        pointers[i] = myAlloc.allocate(128);
    }
    auto end = std::chrono::high_resolution_clock::now();
    std::cout << "MyMalloc time: "
              << std::chrono::duration<double>(end - start).count() << "s\n";

    start = std::chrono::high_resolution_clock::now();
    for (size_t i = 0; i < numAllocations; i++) {
        free(malloc(128));
    }
    end = std::chrono::high_resolution_clock::now();
    std::cout << "System malloc time: "
              << std::chrono::duration<double>(end - start).count() << "s\n";
}

int main() {
    benchmark();
    return 0;
}
