#include "env.h"
#include "val.h"
#include <stdexcept>

PTR(Env) Env::empty = NEW(EmptyEnv)();

PTR(Val) EmptyEnv::lookup(const std::string& name) {
    throw std::runtime_error("free variable: " + name);
}

bool EmptyEnv::equals(PTR(Env) other) {
    // An empty environment equals another empty environment
    return CAST(EmptyEnv)(other) != nullptr;
}

ExtendedEnv::ExtendedEnv(const std::string& name, PTR(Val) val, PTR(Env) rest)
    : name(name), val(val), rest(rest) {}

PTR(Val) ExtendedEnv::lookup(const std::string& name) {
    if (this->name == name) {
        return val;
    } else {
        return rest->lookup(name);
    }
}

bool ExtendedEnv::equals(PTR(Env) other) {
    PTR(ExtendedEnv) other_ext = CAST(ExtendedEnv)(other);
    if (other_ext == nullptr) {
        return false;
    }
    
    // Compare name and rest of environment
    if (name != other_ext->name || !rest->equals(other_ext->rest)) {
        return false;
    }
    
    // Compare values
    return val->equals(other_ext->val);
}
