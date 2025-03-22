//
//  RC4.cpp
//  RC4
//
//  Created by Jia Gao on 3/7/25.
//
#include <iostream>
#include <vector>
#include <string>

class RC4 {
private:
    std::vector<uint8_t> S;
    int i, j;

public:
    RC4(const std::string& key) {
        S.resize(256);
        for (int i = 0; i < 256; i++) S[i] = i;

        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + S[i] + key[i % key.size()]) % 256;
            std::swap(S[i], S[j]);
        }

        this->i = 0;
        this->j = 0;
    }

    uint8_t get_byte() {
        i = (i + 1) % 256;
        j = (j + S[i]) % 256;
        std::swap(S[i], S[j]);
        return S[(S[i] + S[j]) % 256];
    }

    std::vector<uint8_t> encrypt_decrypt(const std::vector<uint8_t>& input) {
        std::vector<uint8_t> output(input.size());
        for (size_t k = 0; k < input.size(); k++) {
            output[k] = input[k] ^ get_byte();
        }
        return output;
    }
};

int main() {
    std::string key = "secretkey";
    RC4 cipher(key);

    std::string message = "Your salary is $1000";
    std::vector<uint8_t> plaintext(message.begin(), message.end());

    // Encrypt the message
    auto encrypted = cipher.encrypt_decrypt(plaintext);
    
    // Attack
    std::string target_original = "1000";
    std::string target_new = "9999";
    
    for (size_t i = 0; i < plaintext.size(); i++) {
        if (message.substr(i, 4) == target_original) {
            for (int j = 0; j < 4; j++) {
                encrypted[i + j] ^= (target_original[j] ^ target_new[j]);
            }
            break;
        }
    }

    // Decrypt
    RC4 decrypt_cipher(key);
    auto decrypted = decrypt_cipher.encrypt_decrypt(encrypted);

    // Display
    std::cout << "Original Message: " << message << std::endl;
    std::cout << "Encrypted Message (Hex): ";
    for (uint8_t c : encrypted) std::cout << std::hex << (int)c << " ";
    std::cout << std::endl;
    
    std::cout << "Decrypted Modified Message: " << std::string(decrypted.begin(), decrypted.end()) << std::endl;

    return 0;
}
