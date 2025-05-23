#ifndef VAL_H
#define VAL_H

#include "pointer.h"
#include "expr.h"
#include <string>
#include <stdexcept>
#include <sstream>


class Expr;
class NumExpr;
class BoolExpr;
class FunExpr;

// Base class
CLASS(Val) {
public:
    virtual ~Val() = default;
    
    virtual bool equals(PTR(Val) other) = 0;
    virtual PTR(Expr) to_expr() = 0;
    virtual std::string to_string() = 0;
    virtual PTR(Val) add_to(PTR(Val) other) = 0;
    virtual PTR(Val) mult_with(PTR(Val) other) = 0;
    virtual PTR(Val) equals_to(PTR(Val) other) = 0;
    virtual bool is_true() = 0;
    virtual PTR(Val) call(PTR(Val) actual_arg_val) = 0;
};

// Numeric value
class NumVal : public Val {
public:
    int val;
    
    NumVal(int val);
    bool equals(PTR(Val) other) override;
    PTR(Expr) to_expr() override;
    std::string to_string() override;
    PTR(Val) add_to(PTR(Val) other) override;
    PTR(Val) mult_with(PTR(Val) other) override;
    PTR(Val) equals_to(PTR(Val) other) override;
    bool is_true() override;
    PTR(Val) call(PTR(Val) actual_arg_val) override;
};

// Boolean value
class BoolVal : public Val {
public:
    bool val;
    
    BoolVal(bool val);
    bool equals(PTR(Val) other) override;
    PTR(Expr) to_expr() override;
    std::string to_string() override;
    PTR(Val) add_to(PTR(Val) other) override;
    PTR(Val) mult_with(PTR(Val) other) override;
    PTR(Val) equals_to(PTR(Val) other) override;
    bool is_true() override;
    PTR(Val) call(PTR(Val) actual_arg_val) override;
};

// Function value
class FunVal : public Val {
public:
    std::string formal_arg;
    PTR(Expr) body;
    
    FunVal(const std::string& formal_arg, PTR(Expr) body);
    bool equals(PTR(Val) other) override;
    PTR(Expr) to_expr() override;
    std::string to_string() override;
    PTR(Val) add_to(PTR(Val) other) override;
    PTR(Val) mult_with(PTR(Val) other) override;
    PTR(Val) equals_to(PTR(Val) other) override;
    bool is_true() override;
    PTR(Val) call(PTR(Val) actual_arg_val) override;
};

#endif // VAL_H
