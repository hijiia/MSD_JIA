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
    Num(int val) : val(val) {}
    bool equals(Expr *e) override;
    int interp() override { return val; }
    bool has_variable() override { return false; }
    Expr* subst(const std::string& variable, Expr* replacement) override { return new Num(val); }
};

class Add : public Expr {
private:
    Expr *lhs;
    Expr *rhs;

public:
    Add(Expr *lhs, Expr *rhs) : lhs(lhs), rhs(rhs) {}
    bool equals(Expr *e) override;
    int interp() override { return lhs->interp() + rhs->interp(); }
    bool has_variable() override { return lhs->has_variable() || rhs->has_variable(); }
    Expr* subst(const std::string& variable, Expr* replacement) override {
        return new Add(lhs->subst(variable, replacement), rhs->subst(variable, replacement));
    }
};

class Mult : public Expr {
private:
    Expr *lhs;
    Expr *rhs;

public:
    Mult(Expr *lhs, Expr *rhs) : lhs(lhs), rhs(rhs) {}
    bool equals(Expr *e) override;
    int interp() override { return lhs->interp() * rhs->interp(); }
    bool has_variable() override { return lhs->has_variable() || rhs->has_variable(); }
    Expr* subst(const std::string& variable, Expr* replacement) override {
        return new Mult(lhs->subst(variable, replacement), rhs->subst(variable, replacement));
    }
};

class VarExpr : public Expr {
private:
    std::string name;

public:
    VarExpr(const std::string &name) : name(name) {}
    bool equals(Expr *e) override;
    int interp() override { throw std::runtime_error("Variable has no value"); }
    bool has_variable() override { return true; }
    Expr* subst(const std::string& variable, Expr* replacement) override {
        if (name == variable) {
            return replacement;
        } else {
            return new VarExpr(name);
        }
    }
    ~VarExpr() = default; // Ensure the destructor matches the base class
};

#endif // EXP_H
