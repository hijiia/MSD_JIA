#include "val.h"
#include "expr.h"
#include <stdexcept>
#include <sstream>


// NumVal Implementation
NumVal::NumVal(int val) : val(val) {}

bool NumVal::equals(PTR(Val) other) {
    PTR(NumVal) num_val = CAST(NumVal)(other);
    if (num_val == nullptr) {
        return false;
    }
    return this->val == num_val->val;
}

PTR(Expr) NumVal::to_expr() {
    return NEW(NumExpr)(val);
}

std::string NumVal::to_string() {
    return std::to_string(val);
}

PTR(Val) NumVal::add_to(PTR(Val) other) {
    PTR(NumVal) other_num = CAST(NumVal)(other);
    if (other_num == nullptr) {
        throw std::runtime_error("can only add numbers");
    }
    return NEW(NumVal)(this->val + other_num->val);
}

PTR(Val) NumVal::mult_with(PTR(Val) other) {
    PTR(NumVal) other_num = CAST(NumVal)(other);
    if (other_num == nullptr) {
        throw std::runtime_error("can only multiply numbers");
    }
    return NEW(NumVal)(this->val * other_num->val);
}

PTR(Val) NumVal::equals_to(PTR(Val) other) {
    PTR(NumVal) other_num = CAST(NumVal)(other);
    if (other_num == nullptr) {
        return NEW(BoolVal)(false);
    }
    return NEW(BoolVal)(this->val == other_num->val);
}

bool NumVal::is_true() {
    throw std::runtime_error("number cannot be used as condition");
}

PTR(Val) NumVal::call(PTR(Val) actual_arg_val) {
    (void)actual_arg_val;
    throw std::runtime_error("cannot call a number");
}

// BoolVal Implementation
BoolVal::BoolVal(bool val) : val(val) {}

bool BoolVal::equals(PTR(Val) other) {
    PTR(BoolVal) bool_val = CAST(BoolVal)(other);
    if (bool_val == nullptr) {
        return false;
    }
    return this->val == bool_val->val;
}

PTR(Expr) BoolVal::to_expr() {
    return NEW(BoolExpr)(val);
}

std::string BoolVal::to_string() {
    return val ? "_true" : "_false";
}

PTR(Val) BoolVal::add_to(PTR(Val) other) {
    (void)other;
    throw std::runtime_error("cannot add boolean values");
}

PTR(Val) BoolVal::mult_with(PTR(Val) other) {
    (void)other;
    throw std::runtime_error("cannot multiply boolean values");
}

PTR(Val) BoolVal::equals_to(PTR(Val) other) {
    PTR(BoolVal) other_bool = CAST(BoolVal)(other);
    if (other_bool == nullptr) {
        return NEW(BoolVal)(false);
    }
    return NEW(BoolVal)(this->val == other_bool->val);
}

bool BoolVal::is_true() {
    return val;
}

PTR(Val) BoolVal::call(PTR(Val) actual_arg_val) {
    (void)actual_arg_val;
    throw std::runtime_error("cannot call a boolean");
}

// FunVal Implementation
FunVal::FunVal(const std::string& formal_arg, PTR(Expr) body)
    : formal_arg(formal_arg), body(body) {}

bool FunVal::equals(PTR(Val) other) {
    PTR(FunVal) f = CAST(FunVal)(other);
    return f != nullptr && formal_arg == f->formal_arg && body->equals(f->body);
}

PTR(Expr) FunVal::to_expr() {
    return NEW(FunExpr)(formal_arg, body->clone());
}

std::string FunVal::to_string() {
    std::stringstream ss;
    ss << "(_fun (" << formal_arg << ") ";
    ss << body->to_string();
    ss << ")";
    return ss.str();
}

PTR(Val) FunVal::add_to(PTR(Val) other) {
    (void)other;
    throw std::runtime_error("cannot add function");
}

PTR(Val) FunVal::mult_with(PTR(Val) other) {
    (void)other;
    throw std::runtime_error("cannot multiply function");
}

PTR(Val) FunVal::equals_to(PTR(Val) other) {
    return NEW(BoolVal)(this->equals(other));
}

bool FunVal::is_true() {
    throw std::runtime_error("function cannot be used as condition");
}

PTR(Val) FunVal::call(PTR(Val) actual_arg_val) {
    PTR(Expr) arg_expr = actual_arg_val->to_expr();
    PTR(Expr) substituted = body->subst(formal_arg, arg_expr);
    return substituted->interp();
}
