#ifndef VAL_H
#define VAL_H

#include "expr.h"
#include <string>
#include <memory>

class Val {
public:
    virtual ~Val() {}
    virtual bool equals(Val* v) = 0;
    virtual std::string to_string() = 0;
    virtual Expr* to_expr() = 0;
    virtual Val* add_to(Val* v) = 0;
    virtual Val* mult_with(Val* v) = 0;
    virtual int to_int() = 0;
    virtual bool is_true() { throw std::runtime_error("Not a boolean value"); }
    virtual void print(std::ostream &out) = 0;
    virtual Val* call(Val* arg) { throw std::runtime_error("Not a function value"); }
};

class NumVal : public Val {
public:
    int value;
    NumVal(int value);
    bool equals(Val* v) override;
    std::string to_string() override;
    Expr* to_expr() override;
    Val* add_to(Val* v) override;
    Val* mult_with(Val* v) override;
    int to_int() override;
    void print(std::ostream &out) override;
};

class BoolVal : public Val {
public:
    bool value;
    BoolVal(bool value);
    bool equals(Val* rhs) override;
    Val* add_to(Val* rhs) override;
    Val* mult_with(Val* rhs) override;
    bool is_true() override;
    void print(std::ostream &out) override;
    std::string to_string() override;
    Expr* to_expr() override;
    int to_int() override;
};

class FunVal : public Val {
public:
    std::string formal_arg;
    std::unique_ptr<Expr> body;
    std::unique_ptr<Env> env;  
    
    FunVal(std::string formal_arg, Expr* body, Env* env = nullptr);
    bool equals(Val* v) override;
    std::string to_string() override;
    Expr* to_expr() override;
    Val* add_to(Val* v) override;
    Val* mult_with(Val* v) override;
    int to_int() override;
    void print(std::ostream &out) override;
    Val* call(Val* arg) override;
};

#endif /* VAL_H */
