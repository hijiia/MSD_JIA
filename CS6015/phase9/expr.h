#ifndef EXPR_H
#define EXPR_H

#include <iostream>
#include <sstream>
#include <string>
#include <stdexcept>
#include <memory>

class Val;


typedef enum {
    prec_none,  // = 0 num and var type
    prec_add,   // = 1 add type
    prec_mult,  // = 2 mult type
    prec_let    // = 3 let type
} precedence_t;

class Expr {
public:
    virtual ~Expr() {}
    virtual bool equals(Expr* rhs) = 0;
    virtual Val* interp() = 0;
    virtual bool has_variable() = 0;
    virtual Expr* subst(std::string var, Expr* replacement) = 0;
    virtual void printExp(std::ostream &ot) const = 0;
    virtual precedence_t precedence() = 0;
    virtual std::string pretty_print() const = 0;
    std::string to_string();
};

class NumExpr : public Expr {
public:
    int num;
    NumExpr(int num);
    bool equals(Expr* rhs) override;
    Val* interp() override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    std::string pretty_print() const override;
    precedence_t precedence() override;
};

class AddExpr : public Expr {
public:
    std::unique_ptr<Expr> lhs, rhs;
    AddExpr(Expr* lhs, Expr* rhs);
    bool equals(Expr* rhs) override;
    Val* interp() override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    std::string pretty_print() const override;
    precedence_t precedence() override;
};

class MultExpr : public Expr {
public:
    std::unique_ptr<Expr> lhs, rhs;
    MultExpr(Expr* lhs, Expr* rhs);
    bool equals(Expr* rhs) override;
    Val* interp() override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    std::string pretty_print() const override;
    precedence_t precedence() override;
};

class VarExpr : public Expr {
public:
    std::string name;
    VarExpr(std::string name);
    bool equals(Expr* rhs) override;
    Val* interp() override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    std::string pretty_print() const override;
    precedence_t precedence() override;
};

class LetExpr : public Expr {
public:
    std::string var;
    std::unique_ptr<Expr> rhs;
    std::unique_ptr<Expr> itself;

    LetExpr(std::string var, Expr* rhs, Expr* itself);
    bool equals(Expr* rhs) override;
    Val* interp() override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    std::string pretty_print() const override;
    precedence_t precedence() override;
};

// BoolExpr: Represents _true and _false expressions
class BoolExpr : public Expr {
public:
    bool value;

    BoolExpr(bool value);
    bool equals(Expr* rhs) override;
    Val* interp() override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &out) const override;
    precedence_t precedence() override;
    std::string pretty_print() const override;
};

// EqExpr: Represents == expressions
class EqExpr : public Expr {
public:
    Expr* lhs;
    Expr* rhs;

    EqExpr(Expr* lhs, Expr* rhs);
    bool equals(Expr* rhs) override;
    Val* interp() override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &out) const override;
    precedence_t precedence() override;
    std::string pretty_print() const override;
};

// IfExpr: Represents _if..._then..._else expressions
class IfExpr : public Expr {
public:
    Expr* condition;
    Expr* then_branch;
    Expr* else_branch;

    IfExpr(Expr* condition, Expr* then_branch, Expr* else_branch);
    bool equals(Expr* rhs) override;
    Val* interp() override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &out) const override;
    precedence_t precedence() override;
    std::string pretty_print() const override;
};



#endif /* EXPR_H */
