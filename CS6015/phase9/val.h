#ifndef VAL_H
#define VAL_H

#include <string>
#include <stdexcept>
#include "expr.h"

class Expr;

class Val {
public:
    virtual ~Val() {}
    virtual bool equals(Val* v) = 0;
    virtual std::string to_string() = 0;
    virtual Expr* to_expr() = 0;
    virtual Val* add_to(Val* v) = 0;
    virtual Val* mult_with(Val* v) = 0;
    virtual int to_int() = 0;
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
};

// BoolVal: Represents boolean values
class BoolVal : public Val {
public:
    bool value;

    BoolVal(bool value);
    bool equals(Val* rhs) override;
    Val* add_to(Val* rhs) override;
    Val* mult_with(Val* rhs) override;
    bool is_true();
    void print(std::ostream &out);
    std::string to_string() override;
    Expr* to_expr() override;
    int to_int() override;
};


#endif /* VAL_H */
