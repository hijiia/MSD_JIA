#include "val.h"
#include "expr.h"
#include "env.h"


NumVal::NumVal(int value) : value(value) {}

bool NumVal::equals(Val* v) {
    NumVal* n = dynamic_cast<NumVal*>(v);
    return n && value == n->value;
}

std::string NumVal::to_string() {
    return std::to_string(value);
}

Expr* NumVal::to_expr() {
    return new NumExpr(value);
}

Val* NumVal::add_to(Val* v) {
    NumVal* n = dynamic_cast<NumVal*>(v);
    if (!n) throw std::runtime_error("Cannot add non-numeric values");
    return new NumVal(value + n->value);
}

Val* NumVal::mult_with(Val* v) {
    NumVal* n = dynamic_cast<NumVal*>(v);
    if (!n) throw std::runtime_error("Cannot multiply non-numeric values");
    return new NumVal(value * n->value);
}

int NumVal::to_int() {
    return value;
}

BoolVal::BoolVal(bool value) : value(value) {}
bool BoolVal::equals(Val* rhs) {
    BoolVal* bool_rhs = dynamic_cast<BoolVal*>(rhs);
    return bool_rhs && (this->value == bool_rhs->value);
}
Val* BoolVal::add_to(Val* rhs) {
    throw std::runtime_error("Cannot add boolean values");
}
Val* BoolVal::mult_with(Val* rhs) {
    throw std::runtime_error("Cannot multiply boolean values");
}
bool BoolVal::is_true() {
    return value;
}
void BoolVal::print(std::ostream &out) {
    out << (value ? "_true" : "_false");
}
std::string BoolVal::to_string() {
    return value ? "_true" : "_false";
}
Expr* BoolVal::to_expr() {
    return new BoolExpr(value);
}
int BoolVal::to_int() {
    throw std::runtime_error("Boolean value cannot be converted to int");
}


FunVal::FunVal(std::string formal_arg, Expr* body, Env* env)
    : formal_arg(formal_arg), body(body), env(env ? env : new Env()) {}

bool FunVal::equals(Val* v) {
    FunVal* f = dynamic_cast<FunVal*>(v);
    return f && formal_arg == f->formal_arg && body->equals(f->body.get());
}

std::string FunVal::to_string() {
    return "[function]";
}

Expr* FunVal::to_expr() {
    return new FunExpr(formal_arg, body.get());
}

Val* FunVal::add_to(Val* v) {
    throw std::runtime_error("Cannot add to function value");
}

Val* FunVal::mult_with(Val* v) {
    throw std::runtime_error("Cannot multiply function value");
}

int FunVal::to_int() {
    throw std::runtime_error("Cannot convert function to int");
}

void FunVal::print(std::ostream &out) {
    out << to_string();
}

Val* FunVal::call(Val* arg) {
    Env* new_env = new Env(formal_arg, arg, env.get());
    return body->interp(new_env);  
}
