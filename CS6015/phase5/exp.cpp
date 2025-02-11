#include "exp.h"
#include <stdexcept>
#include <sstream>


// Num Implementation
Num::Num(int val) {
    this->val = val;
}

bool Num::equals(Expr *e) {
    Num *n = dynamic_cast<Num*>(e);
    return n != nullptr && this->val == n->val;
}

int Num::interp() {
    return val; // The value of a number is the number itself
}

bool Num::has_variable() {
    return false; // A number never contains a variable
}

Expr* Num::subst(const std::string& variable, Expr* replacement) {
    (void)variable;
    (void)replacement;
    return new Num(val); // Substitution doesn't change a number
}

void Num::printExp(std::ostream& ot) const {
    ot << val; // Print the number value
}

void Num::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    (void)prec;
    ot << val; // Numbers don't need parentheses
}

// Add Implementation
Add::Add(Expr *lhs, Expr *rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool Add::equals(Expr *e) {
    Add *a = dynamic_cast<Add*>(e);
    return a != nullptr && lhs->equals(a->lhs) && rhs->equals(a->rhs);
}

int Add::interp() {
    return lhs->interp() + rhs->interp(); // The value of an addition is the sum of its subexpressions
}

bool Add::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // True if either subexpression contains a variable
}

Expr* Add::subst(const std::string& variable, Expr* replacement) {
    (void)variable;
    (void)replacement;
    
    return new Add(lhs->subst(variable, replacement), rhs->subst(variable, replacement)); // Substitute in both subexpressions
}

void Add::printExp(std::ostream& ot) const {
    ot << "(";
    lhs->printExp(ot);
    ot << "+";
    rhs->printExp(ot);
    ot << ")";
}

void Add::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    (void)prec;
    if (prec > prec_none) ot << "("; // Add parentheses if needed
    lhs->pretty_print_at(ot, prec_add);
    ot << " + ";
    rhs->pretty_print_at(ot, prec_none);
    if (prec > prec_none) ot << ")";
}

// Mult Implementation
Mult::Mult(Expr *lhs, Expr *rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool Mult::equals(Expr *e) {
    Mult *m = dynamic_cast<Mult*>(e);
    return m != nullptr && lhs->equals(m->lhs) && rhs->equals(m->rhs);
}

int Mult::interp() {
    return lhs->interp() * rhs->interp(); // The value of a multiplication is the product of its subexpressions
}

bool Mult::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // True if either subexpression contains a variable
}

Expr* Mult::subst(const std::string& variable, Expr* replacement) {
    (void)variable;
    (void)replacement;
    return new Mult(lhs->subst(variable, replacement), rhs->subst(variable, replacement)); // Substitute in both subexpressions
}

void Mult::printExp(std::ostream& ot) const {
    ot << "(";
    lhs->printExp(ot);
    ot << "*";
    rhs->printExp(ot);
    ot << ")";
}

void Mult::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    (void)prec;
    if (prec > prec_add) ot << "("; // Add parentheses if needed
    lhs->pretty_print_at(ot, prec_mult);
    ot << " * ";
    rhs->pretty_print_at(ot, prec_add);
    if (prec > prec_add) ot << ")";
}

// VarExpr Implementation
VarExpr::VarExpr(const std::string &name) {
    this->name = name;
}

bool VarExpr::equals(Expr *e) {
    VarExpr *v = dynamic_cast<VarExpr*>(e);
    return v != nullptr && this->name == v->name;
}

int VarExpr::interp() {
    throw std::runtime_error("no value for variable"); // A variable has no value
}

bool VarExpr::has_variable() {
    return true; // A variable always contains a variable
}

Expr* VarExpr::subst(const std::string& variable, Expr* replacement) {
    (void)variable;
    (void)replacement;
    
    if (name == variable) {
        return replacement; // Replace the variable if it matches
    } else {
        return new VarExpr(name); // Otherwise, return a new VarExpr with the same name
    }
}

void VarExpr::printExp(std::ostream& ot) const {
    ot << name; // Print the variable name
}

void VarExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    (void)prec;
    ot << name; // Variables don't need parentheses
}

// Expr Implementation
std::string Expr::to_string() const {
    std::stringstream ss;
    this->printExp(ss);
    return ss.str();
}

void Expr::pretty_print(std::ostream& ot) const {
    pretty_print_at(ot, prec_none);
}

std::string Expr::to_pretty_string() const {
    std::stringstream ss;
    this->pretty_print(ss);
    return ss.str();
}

// LetExpr Implementation
LetExpr::LetExpr(const std::string& var, Expr* rhs, Expr* body)
    : var(var), rhs(rhs), body(body) {}

LetExpr::~LetExpr() {
    delete rhs;
    delete body;
}

bool LetExpr::equals(Expr* e) {
    LetExpr* l = dynamic_cast<LetExpr*>(e);
    return l != nullptr && var == l->var && rhs->equals(l->rhs) && body->equals(l->body);
}

int LetExpr::interp() {
    Expr* substitutedBody = body->subst(var, new Num(rhs->interp()));
    int result = substitutedBody->interp();
    delete substitutedBody;
    return result;
}

bool LetExpr::has_variable() {
    return rhs->has_variable() || body->has_variable();
}

Expr* LetExpr::subst(const std::string& variable, Expr* replacement) {
    if (var == variable) {
        return new LetExpr(var, rhs->subst(variable, replacement), body);
    } else {
        return new LetExpr(var, rhs->subst(variable, replacement), body->subst(variable, replacement));
    }
}

void LetExpr::printExp(std::ostream& ot) const {
    ot << "(_let " << var << "=";
    rhs->printExp(ot);
    ot << " _in ";
    body->printExp(ot);
    ot << ")";
}

void LetExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    if (prec > prec_none) {
        ot << "(";
    }
    ot << "_let " << var << " = ";
    rhs->pretty_print_at(ot, prec_none);

    // Track the position after printing "_let x = <rhs>"
    std::streampos startPos = ot.tellp();

    // Print a newline and align "_in" under "_let"
    ot << "\n";
    ot << std::string(startPos, ' ') << "_in  ";

    body->pretty_print_at(ot, prec_none);

    if (prec > prec_none) {
        ot << ")";
    }
}
