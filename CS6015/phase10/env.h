// env.h
#ifndef ENV_H
#define ENV_H

#include <string>
#include <map>
#include <stdexcept>
#include "val.h"

class Env {
    std::map<std::string, Val*> bindings;
    Env* parent;
    
public:
    Env(Env* parent = nullptr);
    Env(std::string name, Val* val, Env* parent = nullptr);
    ~Env();
    Val* lookup(std::string name);
    void extend(std::string name, Val* val);
};

#endif
