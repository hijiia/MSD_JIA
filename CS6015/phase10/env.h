#ifndef ENV_H
#define ENV_H

#include "val.h"
#include <string>
#include <memory>
#include <map>

class Env {
    std::map<std::string, Val*> bindings;
    Env* parent;
public:
    Env(Env* parent = nullptr) : parent(parent) {}
    Env(std::string name, Val* val, Env* parent = nullptr)
        : parent(parent) { bindings[name] = val; }
    
    Val* lookup(std::string name) {
        auto it = bindings.find(name);
        if (it != bindings.end()) return it->second;
        if (parent) return parent->lookup(name);
        throw std::runtime_error("Unbound variable: " + name);
    }
    
    void extend(std::string name, Val* val) {
        bindings[name] = val;
    }
};

#endif 
