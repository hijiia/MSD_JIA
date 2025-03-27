#ifndef EXPR_H
#define EXPR_H

#include <iostream>
#include <sstream>
#include <string>
#include <stdexcept>
#include <memory>

class Val;
class Env;

typedef enum {
    prec_none,  // = 0 num and var type
    prec_add,   // = 1 add type
    prec_mult,  // = 2 mult type
    prec_let,   // = 3 let type
    prec_fun    // = 4 function type
} precedence_t;

class Expr {
public:
    virtual ~Expr() {}
    virtual bool equals(Expr* rhs) = 0;
    virtual Val* interp(Env* env) = 0;
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
    Val* interp(Env* env) override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    std::string pretty_print() const override;
    precedence_t precedence() override;
};

class AddExpr : public Expr {
public:
    std::unique_ptr<Expr> lhs;
    std::unique_ptr<Expr> rhs;
    AddExpr(Expr* lhs, Expr* rhs);
    bool equals(Expr* rhs) override;
    Val* interp(Env* env) override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    std::string pretty_print() const override;
    precedence_t precedence() override;
};

class MultExpr : public Expr {
public:
    std::unique_ptr<Expr> lhs;
    std::unique_ptr<Expr> rhs;
    MultExpr(Expr* lhs, Expr* rhs);
    bool equals(Expr* rhs) override;
    Val* interp(Env* env) override;
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
    Val* interp(Env* env) override;
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
    std::unique_ptr<Expr> body;  

    LetExpr(std::string var, Expr* rhs, Expr* body);
    bool equals(Expr* rhs) override;
    Val* interp(Env* env) override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    std::string pretty_print() const override;
    precedence_t precedence() override;
};

class BoolExpr : public Expr {
public:
    bool value;

    BoolExpr(bool value);
    bool equals(Expr* rhs) override;
    Val* interp(Env* env) override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &out) const override;
    precedence_t precedence() override;
    std::string pretty_print() const override;
};

class EqExpr : public Expr {
public:
    std::unique_ptr<Expr> lhs;
    std::unique_ptr<Expr> rhs;

    EqExpr(Expr* lhs, Expr* rhs);
    bool equals(Expr* rhs) override;
    Val* interp(Env* env) override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &out) const override;
    precedence_t precedence() override;
    std::string pretty_print() const override;
};

class IfExpr : public Expr {
public:
    std::unique_ptr<Expr> cond;
    std::unique_ptr<Expr> then;
    std::unique_ptr<Expr> else_;

    IfExpr(Expr* cond, Expr* then, Expr* else_);
    bool equals(Expr* rhs) override;
    Val* interp(Env* env) override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &out) const override;
    precedence_t precedence() override;
    std::string pretty_print() const override;
};

class FunExpr : public Expr {
public:
    std::string arg;
    std::string formal_arg;
    std::unique_ptr<Expr> body;

    FunExpr(std::string arg, Expr* body);
    bool equals(Expr* rhs) override;
    Val* interp(Env* env) override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    precedence_t precedence() override;
    std::string pretty_print() const override;
    std::string pretty_print_at(precedence_t prec) const;
};

class CallExpr : public Expr {
public:
    std::unique_ptr<Expr> fun;
    std::unique_ptr<Expr> arg;

    CallExpr(Expr* fun, Expr* arg);
    bool equals(Expr* rhs) override;
    Val* interp(Env* env) override;
    bool has_variable() override;
    Expr* subst(std::string var, Expr* replacement) override;
    void printExp(std::ostream &ot) const override;
    precedence_t precedence() override;
    std::string pretty_print() const override;
};

#endif
