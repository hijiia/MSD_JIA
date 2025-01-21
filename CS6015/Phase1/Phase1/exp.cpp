//
//  exp.cpp
//  Phase1
//
//  Created by Jia Gao on 1/18/25.
//

#include "exp.h"

// Num Implementation
Num::Num(int val) {
    this->val = val;
}

bool Num::equals(Expr *e) {
    Num *n = dynamic_cast<Num*>(e);
    return n != nullptr && this->val == n->val;
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

// Mult Implementation
Mult::Mult(Expr *lhs, Expr *rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool Mult::equals(Expr *e) {
    Mult *m = dynamic_cast<Mult*>(e);
    return m != nullptr && lhs->equals(m->lhs) && rhs->equals(m->rhs);
}

// VarExpr Implementation
VarExpr::VarExpr(const std::string &name) {
    this->name = name;
}

bool VarExpr::equals(Expr *e) {
    VarExpr *v = dynamic_cast<VarExpr*>(e);
    return v != nullptr && this->name == v->name;
}
