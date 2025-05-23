#ifndef EXPR_H
#define EXPR_H

#include "pointer.h"
#include "val.h"
#include <string>
#include <iostream>
#include <sstream>
#include <stdexcept>

class Val;

typedef enum {
    prec_none,
    prec_eq,
    prec_add,
    prec_mult
} precedence_t;

// Base class
CLASS(Expr) {
public:
    virtual ~Expr() = default;
    
    virtual bool equals(PTR(Expr) e) = 0;
    virtual PTR(Val) interp() = 0;
    virtual bool has_variable() = 0;
    virtual PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) = 0;
    virtual void printExp(std::ostream& ot) const = 0;
    virtual void pretty_print_at(std::ostream& ot, precedence_t prec) const = 0;
    virtual PTR(Expr) clone() const = 0;

    std::string to_string() const;
    void pretty_print(std::ostream& ot) const;
    std::string to_pretty_string() const;
};

// Numeric expression
class NumExpr : public Expr {
public:
    int val;
    
    NumExpr(int val);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// Addition expression
class AddExpr : public Expr {
public:
    PTR(Expr) lhs;
    PTR(Expr) rhs;
    
    AddExpr(PTR(Expr) lhs, PTR(Expr) rhs);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// Multiplication expression
class MultExpr : public Expr {
public:
    PTR(Expr) lhs;
    PTR(Expr) rhs;
    
    MultExpr(PTR(Expr) lhs, PTR(Expr) rhs);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// Variable expression
class VarExpr : public Expr {
public:
    std::string name;
    
    VarExpr(const std::string& name);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// Let expression
class LetExpr : public Expr {
public:
    std::string var;
    PTR(Expr) rhs;
    PTR(Expr) body;
    
    LetExpr(const std::string& var, PTR(Expr) rhs, PTR(Expr) body);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// Boolean expression
class BoolExpr : public Expr {
public:
    bool val;
    
    BoolExpr(bool val);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// Equality expression
class EqExpr : public Expr {
public:
    PTR(Expr) lhs;
    PTR(Expr) rhs;
    
    EqExpr(PTR(Expr) lhs, PTR(Expr) rhs);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// If expression
class IfExpr : public Expr {
public:
    PTR(Expr) test_part;
    PTR(Expr) then_part;
    PTR(Expr) else_part;
    
    IfExpr(PTR(Expr) test_part, PTR(Expr) then_part, PTR(Expr) else_part);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// Function expression
class FunExpr : public Expr {
public:
    std::string formal_arg;
    PTR(Expr) body;

    FunExpr(const std::string& formal_arg, PTR(Expr) body);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// Function call expression
class CallExpr : public Expr {
public:
    PTR(Expr) to_be_called;
    PTR(Expr) actual_arg;

    CallExpr(PTR(Expr) to_be_called, PTR(Expr) actual_arg);
    bool equals(PTR(Expr) e) override;
    PTR(Val) interp() override;
    bool has_variable() override;
    PTR(Expr) subst(const std::string& variable, PTR(Expr) replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
    PTR(Expr) clone() const override;
};

// Parsing functions
void consume_whitespace(std::istream& in);
PTR(Expr) parse_str(const std::string& s);
PTR(Expr) parse(std::istream& in);
PTR(Expr) parse_expr(std::istream& in);
PTR(Expr) parse_comparg(std::istream& in);
PTR(Expr) parse_addend(std::istream& in);
PTR(Expr) parse_multicand(std::istream& in);
PTR(Expr) parse_inner(std::istream& in);
PTR(Expr) parse_var(std::istream& in);
PTR(Expr) parse_num(std::istream& in);
PTR(Expr) parse_let(std::istream& in);
PTR(Expr) parse_if(std::istream& in);
PTR(Expr) parse_fun(std::istream& in);
PTR(Expr) parse_keyword(std::istream& in, char prefix);

#endif // EXPR_H
