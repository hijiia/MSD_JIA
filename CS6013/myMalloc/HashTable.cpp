#include "HashTable.hpp"
#include <cstring>
#include <iostream>

// Hash function for pointers
size_t HashTable::hash(void* key) const {
    return (reinterpret_cast<size_t>(key) >> 12) % capacity;
}

HashTable::HashTable(size_t initialSize) : capacity(initialSize), size(0) {
    table = static_cast<Entry*>(mmap(nullptr, capacity * sizeof(Entry),
                                     PROT_READ | PROT_WRITE,
                                     MAP_PRIVATE | MAP_ANONYMOUS, -1, 0));
    if (table == MAP_FAILED) {
        perror("mmap");
        exit(1);
    }
    std::memset(table, 0, capacity * sizeof(Entry));
}

HashTable::~HashTable() {
    munmap(table, capacity * sizeof(Entry));
}

void HashTable::insert(void* key, size_t value) {
    if (size * 2 >= capacity) grow();
    size_t index = hash(key);

    while (table[index].isOccupied && !table[index].isDeleted) {
        index = (index + 1) % capacity;
    }

    table[index] = {key, value, true, false};
    size++;
}

bool HashTable::remove(void* key) {
    size_t index = hash(key);
    while (table[index].isOccupied) {
        if (table[index].key == key && !table[index].isDeleted) {
            table[index].isDeleted = true;
            return true;
        }
        index = (index + 1) % capacity;
    }
    return false;
}

size_t HashTable::find(void* key) const {
    size_t index = hash(key);
    while (table[index].isOccupied) {
        if (table[index].key == key && !table[index].isDeleted) {
            return table[index].value;
        }
        index = (index + 1) % capacity;
    }
    return 0;
}

void HashTable::grow() {
    size_t newCapacity = capacity * 2;
    Entry* newTable = static_cast<Entry*>(mmap(nullptr, newCapacity * sizeof(Entry),
                                               PROT_READ | PROT_WRITE,
                                               MAP_PRIVATE | MAP_ANONYMOUS, -1, 0));
    if (newTable == MAP_FAILED) {
        perror("mmap");
        exit(1);
    }

    std::memset(newTable, 0, newCapacity * sizeof(Entry));
    std::swap(table, newTable);
    std::swap(capacity, newCapacity);
    munmap(newTable, newCapacity * sizeof(Entry));
}
