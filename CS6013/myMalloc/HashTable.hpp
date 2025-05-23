#ifndef HASH_TABLE_HPP
#define HASH_TABLE_HPP

#include <cstddef>
#include <sys/mman.h>

class HashTable {
private:
    struct Entry {
        void* key;
        size_t value;
        bool isOccupied;
        bool isDeleted;
    };

    Entry* table;
    size_t capacity;
    size_t size;
    
    size_t hash(void* key) const;
    void grow();

public:
    HashTable(size_t initialSize = 1024);
    ~HashTable();

    void insert(void* key, size_t value);
    bool remove(void* key);
    size_t find(void* key) const;
};

#endif
