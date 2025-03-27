#include "env.h"
#include "val.h"


Env::Env(Env* parent) : parent(parent) {}

//initial binding
Env::Env(std::string name, Val* val, Env* parent)
    : parent(parent) {
    bindings[name] = val;
}

// Look up in the environment
Val* Env::lookup(std::string name) {
    auto it = bindings.find(name);
    if (it != bindings.end()) {
        return it->second;
    }
    if (parent) {
        return parent->lookup(name);
    }
    throw std::runtime_error("Unbound variable: " + name);
}

// Add a new binding
void Env::extend(std::string name, Val* val) {
    bindings[name] = val;
}
