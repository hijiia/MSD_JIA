#ifndef EXP_H
#define EXP_H

#include <string>
#include <iostream>
#include <sstream>
#include <stdexcept>

typedef enum {
    prec_none,      // = 0
    prec_add,       // = 1
    prec_mult       // = 2
} precedence_t;

// Base class for expressions
class Expr {
public:
    // Virtual destructor
    virtual ~Expr() = default;

    // Pure virtual methods
    virtual bool equals(Expr* e) = 0; // Check if two expressions are equal
    virtual int interp() = 0;        // Interpret the expression to a value
    virtual bool has_variable() = 0; // Check if the expression contains a variable
    virtual Expr* subst(const std::string& variable, Expr* replacement) = 0; // Substitute a variable with an expression

    // Print the expression to an output stream
    virtual void printExp(std::ostream& ot) const = 0;

    // Convert the expression to a string
    std::string to_string() const;

    // Pretty-print the expression to an output stream
    void pretty_print(std::ostream& ot) const;

    // Convert the pretty-printed expression to a string
    std::string to_pretty_string() const;

    // Helper method for pretty-printing with precedence
    virtual void pretty_print_at(std::ostream& ot, precedence_t prec) const = 0;
};

// Number expression
class Num : public Expr {
public:
    int val; // The value of the number

    Num(int val); // Constructor
    bool equals(Expr* e) override; // Override equals
    int interp() override;         // Override interp
    bool has_variable() override;  // Override has_variable
    Expr* subst(const std::string& variable, Expr* replacement) override; // Override subst

    void printExp(std::ostream& ot) const override; // Override printExp
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override; // Override pretty_print_at
};

// Addition expression
class Add : public Expr {
public:
    Expr* lhs; // Left-hand side of the addition
    Expr* rhs; // Right-hand side of the addition

    Add(Expr* lhs, Expr* rhs); // Constructor
    bool equals(Expr* e) override; // Override equals
    int interp() override;         // Override interp
    bool has_variable() override;  // Override has_variable
    Expr* subst(const std::string& variable, Expr* replacement) override; // Override subst

    void printExp(std::ostream& ot) const override; // Override printExp
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override; // Override pretty_print_at
};

// Multiplication expression
class Mult : public Expr {
public:
    Expr* lhs; // Left-hand side of the multiplication
    Expr* rhs; // Right-hand side of the multiplication

    Mult(Expr* lhs, Expr* rhs); // Constructor
    bool equals(Expr* e) override; // Override equals
    int interp() override;         // Override interp
    bool has_variable() override;  // Override has_variable
    Expr* subst(const std::string& variable, Expr* replacement) override; // Override subst

    void printExp(std::ostream& ot) const override; // Override printExp
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override; // Override pretty_print_at
};

// Variable expression
class VarExpr : public Expr {
public:
    std::string name; // The name of the variable

    VarExpr(const std::string& name); // Constructor
    bool equals(Expr* e) override; // Override equals
    int interp() override;         // Override interp
    bool has_variable() override;  // Override has_variable
    Expr* subst(const std::string& variable, Expr* replacement) override; // Override subst

    void printExp(std::ostream& ot) const override; // Override printExp
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override; // Override pretty_print_at
};

class LetExpr : public Expr {
private:
    std::string var;
    Expr* rhs;
    Expr* body;

public:
    LetExpr(const std::string& var, Expr* rhs, Expr* body);
    ~LetExpr();

    bool equals(Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& variable, Expr* replacement) override;
    void printExp(std::ostream& ot) const override;
    void pretty_print_at(std::ostream& ot, precedence_t prec) const override;
};

#endif // EXP_H
