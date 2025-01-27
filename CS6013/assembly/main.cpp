#include <iostream>
#include <sys/time.h>
extern "C" {
    timeval myGTOD();
}

int main() {
    timeval tv = myGTOD();
    std::cout << "Seconds: " << tv.tv_sec << ", Microseconds: " << tv.tv_usec << std::endl;
    return 0;
}

