#include "exp.h"
#include <stdexcept>
#include <sstream>
#include <cctype>

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
    return lhs->interp() + rhs->interp();
}

bool Add::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

Expr* Add::subst(const std::string& variable, Expr* replacement) {
    return new Add(lhs->subst(variable, replacement), rhs->subst(variable, replacement));
}

void Add::printExp(std::ostream& ot) const {
    ot << "(";
    lhs->printExp(ot);
    ot << "+";
    rhs->printExp(ot);
    ot << ")";
}

void Add::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    if (prec > prec_none) ot << "(";
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
    return lhs->interp() * rhs->interp();
}

bool Mult::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

Expr* Mult::subst(const std::string& variable, Expr* replacement) {
    return new Mult(lhs->subst(variable, replacement), rhs->subst(variable, replacement));
}

void Mult::printExp(std::ostream& ot) const {
    ot << "(";
    lhs->printExp(ot);
    ot << "*";
    rhs->printExp(ot);
    ot << ")";
}

void Mult::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    if (prec > prec_add) ot << "(";
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
    throw std::runtime_error("no value for variable");
}

bool VarExpr::has_variable() {
    return true;
}

Expr* VarExpr::subst(const std::string& variable, Expr* replacement) {
    if (name == variable) {
        return replacement;
    } else {
        return new VarExpr(name);
    }
}

void VarExpr::printExp(std::ostream& ot) const {
    ot << name;
}

void VarExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    ot << name;
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
    ot << "_let " << var << " = ";
    rhs->pretty_print_at(ot, prec);
    ot << "\n_in  "; 
    body->pretty_print_at(ot, prec);
}

Expr* parse(std::istream &in) {
    consume_whitespace(in);
    return parse_expr(in);
}

Expr* parse_str(const std::string &s) {
    std::istringstream in(s);
    try {
        return parse(in);
    } catch (const std::invalid_argument& e) {
        throw std::runtime_error("invalid input");
    } catch (const std::runtime_error& e) {
        throw std::runtime_error("invalid input");
    }
}

Expr* parse_expr(std::istream &in) {
    Expr* lhs = parse_addend(in);
    consume_whitespace(in);
    while (in.peek() == '+') {
        in.get();
        Expr* rhs = parse_addend(in);
        lhs = new Add(lhs, rhs);
        consume_whitespace(in);
    }
    return lhs;
}

Expr* parse_addend(std::istream &in) {
    Expr* lhs = parse_mult(in);
    consume_whitespace(in);
    while (in.peek() == '*') {
        in.get();
        Expr* rhs = parse_mult(in);
        lhs = new Mult(lhs, rhs);
        consume_whitespace(in);
    }
    return lhs;
}



Expr* parse_mult(std::istream &in) {
    consume_whitespace(in);

    char next = in.peek();

    
    if (next == '*' || next == ')') {
        throw std::runtime_error("Unexpected character");
    }

    if (next == '-') {
        in.get(); //  '-'
        consume_whitespace(in);

        if (std::isdigit(in.peek())) {
            int num;
            in >> num;
            return new Num(-num);
        } else if (std::isalpha(in.peek())) {
            return new VarExpr("-" + std::string(1, in.get()));
            
        } else {
            throw std::runtime_error("Unexpected character");
        }
    }

    if (std::isalpha(in.peek())) return parse_var(in);
    if (std::isdigit(in.peek())) return parse_num(in);
    if (in.peek() == '(') {
        in.get(); //'('
        consume_whitespace(in);
        if (in.peek() == ')') {
            throw std::runtime_error("bad input");
        }
        Expr* expr = parse_expr(in);
        consume_whitespace(in);
        if (in.peek() != ')') {
            throw std::runtime_error("bad input"); // parentheses closure
        }
        in.get(); // ')'
        return expr;
    }

    throw std::runtime_error("Unexpected character"); // Default case
}



Expr* parse_var(std::istream &in) {
    std::string var;
    while (std::isalpha(in.peek()) || in.peek() == '_') {
        var += in.get();
    }

    // invalid variable names
    if (var.find('_') != std::string::npos) {
        throw std::runtime_error("invalid input");
    }
    
    return new VarExpr(var);
}

Expr* parse_num(std::istream &in) {
    int num;
    in >> num;
    return new Num(num);
}

void consume_whitespace(std::istream &in) {
    while (std::isspace(in.peek())) in.get();
}
