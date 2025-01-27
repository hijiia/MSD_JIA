#include "exp.h"
#include <stdexcept>

// Num Implementation
Num::Num(int val) {
    this->val = val;
}

bool Num::equals(Expr *e) {
    Num *n = dynamic_cast<Num*>(e);
    return n != nullptr && this->val == n->val;
}

int Num::interp() {
    return val; // The value of a number is the number itself
}

bool Num::has_variable() {
    return false; // A number never contains a variable
}

Expr* Num::subst(const std::string& variable, Expr* replacement) {
    return new Num(val); // Substitution doesn't change a number
}

// Add Implementation
Add::Add(Expr *lhs, Expr *rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool Add::equals(Expr *e) {
    Add *a = dynamic_cast<Add*>(e);
    return a != nullptr && lhs->equals(a->lhs) && rhs->equals(a->rhs);
}

int Add::interp() {
    return lhs->interp() + rhs->interp(); // The value of an addition is the sum of its subexpressions
}

bool Add::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // True if either subexpression contains a variable
}

Expr* Add::subst(const std::string& variable, Expr* replacement) {
    return new Add(lhs->subst(variable, replacement), rhs->subst(variable, replacement)); // Substitute in both subexpressions
}

// Mult Implementation
Mult::Mult(Expr *lhs, Expr *rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool Mult::equals(Expr *e) {
    Mult *m = dynamic_cast<Mult*>(e);
    return m != nullptr && lhs->equals(m->lhs) && rhs->equals(m->rhs);
}

int Mult::interp() {
    return lhs->interp() * rhs->interp(); // The value of a multiplication is the product of its subexpressions
}

bool Mult::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // True if either subexpression contains a variable
}

Expr* Mult::subst(const std::string& variable, Expr* replacement) {
    return new Mult(lhs->subst(variable, replacement), rhs->subst(variable, replacement)); // Substitute in both subexpressions
}

// VarExpr Implementation
VarExpr::VarExpr(const std::string &name) {
    this->name = name;
}

bool VarExpr::equals(Expr *e) {
    VarExpr *v = dynamic_cast<VarExpr*>(e);
    return v != nullptr && this->name == v->name;
}

int VarExpr::interp() {
    throw std::runtime_error("no value for variable"); // A variable has no value
}

bool VarExpr::has_variable() {
    return true; // A variable always contains a variable
}

Expr* VarExpr::subst(const std::string& variable, Expr* replacement) {
    if (name == variable) {
        return replacement; // Replace the variable if it matches
    } else {
        return new VarExpr(name); // Otherwise, return a new VarExpr with the same name
    }
}
