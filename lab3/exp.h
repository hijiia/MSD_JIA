#ifndef EXP_H
#define EXP_H

#include <string>
#include <stdexcept>

class Expr {
public:
    virtual ~Expr() = default;

    virtual bool equals(Expr *e) = 0;
    virtual int interp() = 0;
    virtual bool has_variable() = 0;
    virtual Expr* subst(const std::string& variable, Expr* replacement) = 0;
};

class Num : public Expr {
private:
    int val;

public:
    Num(int val);
    bool equals(Expr *e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& variable, Expr* replacement) override;
};

class Add : public Expr {
private:
    Expr *lhs;
    Expr *rhs;

public:
    Add(Expr *lhs, Expr *rhs);
    bool equals(Expr *e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& variable, Expr* replacement) override;
};

class Mult : public Expr {
private:
    Expr *lhs;
    Expr *rhs;

public:
    Mult(Expr *lhs, Expr *rhs);
    bool equals(Expr *e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& variable, Expr* replacement) override;
};

class VarExpr : public Expr {
private:
    std::string name;

public:
    VarExpr(const std::string &name);
    bool equals(Expr *e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& variable, Expr* replacement) override;
};

#endif // EXP_H
