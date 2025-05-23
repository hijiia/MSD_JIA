#ifndef MYMALLOC_HPP
#define MYMALLOC_HPP

#include "HashTable.hpp"
#include <cstdlib>
#include <sys/mman.h>

class MyMalloc {
private:
    HashTable allocations;
public:
    MyMalloc();
    ~MyMalloc();

    void* allocate(size_t bytes);
    void deallocate(void* ptr);
};

#endif
