//
//  BadBlockCipher.cpp
//  BadBlockCipher
//
//  Created by Jia Gao on 2/23/25.
//
#include <iostream>
#include <array>
#include <vector>
#include <random>
#include <algorithm>
#include <cstring>

using Block = std::array<uint8_t, 8>;

// 64-bit key from a password
Block generate_key(const std::string& password) {
    Block key = {0};
    for (size_t i = 0; i < password.size(); i++) {
        key[i % 8] ^= password[i]; // XOR
    }
    return key;
}

//8 substitution tables
std::vector<std::array<uint8_t, 256>> generate_substitution_tables() {
    std::vector<std::array<uint8_t, 256>> tables(8);
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 256; j++) {
            tables[i][j] = j;
        }
        std::random_device rd;
        std::mt19937 g(rd());
        std::shuffle(tables[i].begin(), tables[i].end(), g);
    }
    return tables;
}

// Encrypt a 64-bit block
Block encrypt_block(Block block, const Block& key, const std::vector<std::array<uint8_t, 256>>& tables) {
    for (int round = 0; round < 16; round++) {
        for (int i = 0; i < 8; i++) {
            block[i] ^= key[i];  // XOR with key
        }
        for (int i = 0; i < 8; i++) {
            block[i] = tables[i][block[i]]; // Substitution step
        }
        uint8_t carry = block[0] >> 7;
        for (int i = 0; i < 7; i++) {
            block[i] = (block[i] << 1) | (block[i + 1] >> 7);
        }
        block[7] = (block[7] << 1) | carry;
    }
    return block;
}

// Generate reverse substitution tables
std::vector<std::array<uint8_t, 256>> generate_reverse_tables(const std::vector<std::array<uint8_t, 256>>& tables) {
    std::vector<std::array<uint8_t, 256>> reverse_tables(8);
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 256; j++) {
            reverse_tables[i][tables[i][j]] = j;
        }
    }
    return reverse_tables;
}

// Decrypt a 64-bit block
Block decrypt_block(Block block, const Block& key, const std::vector<std::array<uint8_t, 256>>& reverse_tables) {
    for (int round = 0; round < 16; round++) {
        // Right 1 bit
        uint8_t carry = block[7] & 1;
        for (int i = 7; i > 0; i--) {
            block[i] = (block[i] >> 1) | ((block[i - 1] & 1) << 7);
        }
        block[0] = (block[0] >> 1) | (carry << 7);

        for (int i = 0; i < 8; i++) {
            block[i] = reverse_tables[i][block[i]]; // Reverse substitution
        }
        for (int i = 0; i < 8; i++) {
            block[i] ^= key[i]; // XOR
        }
    }
    return block;
}

//test
int main() {
    std::string password = "mypassword";
    Block key = generate_key(password);
    auto tables = generate_substitution_tables();
    auto reverse_tables = generate_reverse_tables(tables);

    Block plaintext = {'H', 'e', 'l', 'l', 'o', '!', '!', '!'};
    std::cout << "Original Block: ";
    for (uint8_t c : plaintext) std::cout << c;
    std::cout << std::endl;

    Block encrypted = encrypt_block(plaintext, key, tables);
    std::cout << "Encrypted Block: ";
    for (uint8_t c : encrypted) std::cout << std::hex << (int)c << " ";
    std::cout << std::endl;

    Block decrypted = decrypt_block(encrypted, key, reverse_tables);
    std::cout << "Decrypted Block: ";
    for (uint8_t c : decrypted) std::cout << c;
    std::cout << std::endl;

    return 0;
}
