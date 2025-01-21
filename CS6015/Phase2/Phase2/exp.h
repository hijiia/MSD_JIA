//
//  exp.h
//  Phase2
//
//  Created by Jia Gao on 1/21/25.
//

#ifndef exp_hpp
#define exp_hpp

#include <string>

class Expr {
public:
    virtual ~Expr() = default;
    virtual bool equals(Expr *e) = 0;
};

class Num : public Expr {
public:
    int val;
    Num(int val);
    bool equals(Expr *e) override;
};

class Add : public Expr {
public:
    Expr *lhs;
    Expr *rhs;
    Add(Expr *lhs, Expr *rhs);
    bool equals(Expr *e) override;
};

class Mult : public Expr {
public:
    Expr *lhs;
    Expr *rhs;
    Mult(Expr *lhs, Expr *rhs);
    bool equals(Expr *e) override;
};

class VarExpr : public Expr {
public:
    std::string name;
    VarExpr(const std::string &name);
    bool equals(Expr *e) override;
};

#endif
