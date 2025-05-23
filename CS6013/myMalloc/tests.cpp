#include "MyMalloc.hpp"
#include <iostream>

int main() {
    MyMalloc myAlloc;
    void* p1 = myAlloc.allocate(100);
    void* p2 = myAlloc.allocate(5000);
    
    std::cout << "Allocated addresses: " << p1 << " " << p2 << std::endl;

    myAlloc.deallocate(p1);
    myAlloc.deallocate(p2);

    return 0;
}
