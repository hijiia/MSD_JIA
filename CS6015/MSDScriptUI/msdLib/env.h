#ifndef ENV_H
#define ENV_H

#include "pointer.h"
#include <string>

class Val;
class Expr;

CLASS(Env) {
public:
    virtual ~Env() = default;
    
    virtual PTR(Val) lookup(const std::string& name) = 0;
    virtual bool equals(PTR(Env) other) = 0;
    
    static PTR(Env) empty;
};

class EmptyEnv : public Env {
public:
    PTR(Val) lookup(const std::string& name) override;
    bool equals(PTR(Env) other) override;
};

class ExtendedEnv : public Env {
public:
    std::string name;
    PTR(Val) val;
    PTR(Env) rest;
    
    ExtendedEnv(const std::string& name, PTR(Val) val, PTR(Env) rest);
    PTR(Val) lookup(const std::string& name) override;
    bool equals(PTR(Env) other) override;
};

#endif // ENV_H
