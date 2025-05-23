#include "MyMalloc.hpp"
#include <unistd.h>
#include <iostream>

MyMalloc::MyMalloc() : allocations(1024) {}

MyMalloc::~MyMalloc() {}

void* MyMalloc::allocate(size_t bytes) {
    size_t pageSize = getpagesize();
    size_t roundedSize = (bytes + pageSize - 1) & ~(pageSize - 1);

    void* ptr = mmap(nullptr, roundedSize, PROT_READ | PROT_WRITE,
                     MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    if (ptr == MAP_FAILED) {
        return nullptr;
    }

    allocations.insert(ptr, roundedSize);
    return ptr;
}

void MyMalloc::deallocate(void* ptr) {
    size_t size = allocations.find(ptr);
    if (size > 0) {
        munmap(ptr, size);
        allocations.remove(ptr);
    }
}
