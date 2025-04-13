#include "expr.h"
#include "pointer.h"
#include "val.h"
#include "env.h"
#include <stdexcept>
#include <sstream>
#include <cctype>

PTR(Expr) parse_inner(std::istream& in);

//Expr
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

// NumExpr
NumExpr::NumExpr(int val) {
    this->val = val;
}

bool NumExpr::equals(PTR(Expr) e) {
    PTR(NumExpr) n = CAST(NumExpr)(e);
    return n != nullptr && this->val == n->val;
}

PTR(Val) NumExpr::interp(PTR(Env) env) {
    (void)env; // Unused parameter
    return NEW(NumVal)(val);
}

bool NumExpr::has_variable() {
    return false;
}

void NumExpr::printExp(std::ostream& ot) const {
    ot << val;
}

void NumExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    (void)prec;
    ot << val;
}

//Add
AddExpr::AddExpr(PTR(Expr) lhs, PTR(Expr) rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool AddExpr::equals(PTR(Expr) e) {
    PTR(AddExpr) a = CAST(AddExpr)(e);
    return a != nullptr && lhs->equals(a->lhs) && rhs->equals(a->rhs);
}

PTR(Val) AddExpr::interp(PTR(Env) env) {
    PTR(Val) lhs_val = lhs->interp(env);
    PTR(Val) rhs_val = rhs->interp(env);
    return lhs_val->add_to(rhs_val);
}

bool AddExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

void AddExpr::printExp(std::ostream& ot) const {
    ot << "(";
    lhs->printExp(ot);
    ot << "+";
    rhs->printExp(ot);
    ot << ")";
}

void AddExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    if (prec > prec_add) ot << "(";
    lhs->pretty_print_at(ot, prec_add);
    ot << " + ";
    rhs->pretty_print_at(ot, prec_none);
    if (prec > prec_add) ot << ")";
}

// MultExpr
MultExpr::MultExpr(PTR(Expr) lhs, PTR(Expr) rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool MultExpr::equals(PTR(Expr) e) {
    PTR(MultExpr) m = CAST(MultExpr)(e);
    return m != nullptr && lhs->equals(m->lhs) && rhs->equals(m->rhs);
}

PTR(Val) MultExpr::interp(PTR(Env) env) {
    PTR(Val) lhs_val = lhs->interp(env);
    PTR(Val) rhs_val = rhs->interp(env);
    return lhs_val->mult_with(rhs_val);
}

bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

void MultExpr::printExp(std::ostream& ot) const {
    ot << "(";
    lhs->printExp(ot);
    ot << "*";
    rhs->printExp(ot);
    ot << ")";
}

void MultExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    if (prec >= prec_mult) ot << "(";

    PTR(AddExpr) lhs_add = CAST(AddExpr)(lhs);
    if (lhs_add != nullptr) {
        ot << "(";
        lhs->pretty_print_at(ot, prec_add);
        ot << ")";
    } else {
        lhs->pretty_print_at(ot, prec_mult);
    }
    
    ot << " * ";
    
    PTR(AddExpr) rhs_add = CAST(AddExpr)(rhs);
    if (rhs_add != nullptr) {
        ot << "(";
        rhs->pretty_print_at(ot, prec_add);
        ot << ")";
    } else {
        rhs->pretty_print_at(ot, prec_none);
    }
    
    if (prec >= prec_mult) ot << ")";
}

//Var
VarExpr::VarExpr(const std::string &name) {
    this->name = name;
}

bool VarExpr::equals(PTR(Expr) e) {
    PTR(VarExpr) v = CAST(VarExpr)(e);
    return v != nullptr && this->name == v->name;
}

PTR(Val) VarExpr::interp(PTR(Env) env) {
    return env->lookup(name);
}

bool VarExpr::has_variable() {
    return true;
}

void VarExpr::printExp(std::ostream& ot) const {
    ot << name;
}

void VarExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    (void)prec;
    ot << name;
}

// Clone implementation
PTR(Expr) NumExpr::clone() const {
    return NEW(NumExpr)(val);
}

PTR(Expr) AddExpr::clone() const {
    return NEW(AddExpr)(lhs->clone(), rhs->clone());
}

PTR(Expr) MultExpr::clone() const {
    return NEW(MultExpr)(lhs->clone(), rhs->clone());
}

PTR(Expr) VarExpr::clone() const {
    return NEW(VarExpr)(name);
}

PTR(Expr) LetExpr::clone() const {
    return NEW(LetExpr)(var, rhs->clone(), body->clone());
}

PTR(Expr) BoolExpr::clone() const {
    return NEW(BoolExpr)(val);
}

PTR(Expr) EqExpr::clone() const {
    return NEW(EqExpr)(lhs->clone(), rhs->clone());
}

PTR(Expr) IfExpr::clone() const {
    return NEW(IfExpr)(test_part->clone(), then_part->clone(), else_part->clone());
}

PTR(Expr) FunExpr::clone() const {
    return NEW(FunExpr)(formal_arg, body->clone());
}

PTR(Expr) CallExpr::clone() const {
    return NEW(CallExpr)(to_be_called->clone(), actual_arg->clone());
}

// Let
LetExpr::LetExpr(const std::string& var, PTR(Expr) rhs, PTR(Expr) body)
    : var(var), rhs(rhs), body(body) {}

bool LetExpr::equals(PTR(Expr) e) {
    PTR(LetExpr) l = CAST(LetExpr)(e);
    return l != nullptr && var == l->var && rhs->equals(l->rhs) && body->equals(l->body);
}

PTR(Val) LetExpr::interp(PTR(Env) env) {
    PTR(Val) rhs_val = rhs->interp(env);
    PTR(Env) new_env = NEW(ExtendedEnv)(var, rhs_val, env);
    return body->interp(new_env);
}

bool LetExpr::has_variable() {
    return rhs->has_variable() || body->has_variable();
}

void LetExpr::printExp(std::ostream& ot) const {
    ot << "(_let " << var << "=";
    rhs->printExp(ot);
    ot << " _in ";
    body->printExp(ot);
    ot << ")";
}

void LetExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    (void)prec;
    ot << "_let " << var << " = ";
    rhs->pretty_print_at(ot, prec_none);
    ot << "\n_in  ";
    body->pretty_print_at(ot, prec_none);
}

// Bool
BoolExpr::BoolExpr(bool val) {
    this->val = val;
}

bool BoolExpr::equals(PTR(Expr) e) {
    PTR(BoolExpr) b = CAST(BoolExpr)(e);
    return b != nullptr && this->val == b->val;
}

PTR(Val) BoolExpr::interp(PTR(Env) env) {
    (void)env; // Unused parameter
    return NEW(BoolVal)(val);
}

bool BoolExpr::has_variable() {
    return false;
}

void BoolExpr::printExp(std::ostream& ot) const {
    ot << (val ? "_true" : "_false");
}

void BoolExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    (void)prec;
    ot << (val ? "_true" : "_false");
}

// EqExpr
EqExpr::EqExpr(PTR(Expr) lhs, PTR(Expr) rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool EqExpr::equals(PTR(Expr) e) {
    PTR(EqExpr) eq = CAST(EqExpr)(e);
    return eq != nullptr && lhs->equals(eq->lhs) && rhs->equals(eq->rhs);
}

PTR(Val) EqExpr::interp(PTR(Env) env) {
    PTR(Val) lhs_val = lhs->interp(env);
    PTR(Val) rhs_val = rhs->interp(env);
    return lhs_val->equals_to(rhs_val);
}

bool EqExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

void EqExpr::printExp(std::ostream& ot) const {
    ot << "(";
    lhs->printExp(ot);
    ot << "==";
    rhs->printExp(ot);
    ot << ")";
}

void EqExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    if (prec > prec_eq) ot << "(";
    lhs->pretty_print_at(ot, prec_eq);
    ot << " == ";
    rhs->pretty_print_at(ot, prec_none);
    if (prec > prec_eq) ot << ")";
}

//If
IfExpr::IfExpr(PTR(Expr) test_part, PTR(Expr) then_part, PTR(Expr) else_part) {
    this->test_part = test_part;
    this->then_part = then_part;
    this->else_part = else_part;
}

bool IfExpr::equals(PTR(Expr) e) {
    PTR(IfExpr) i = CAST(IfExpr)(e);
    return i != nullptr &&
           test_part->equals(i->test_part) &&
           then_part->equals(i->then_part) &&
           else_part->equals(i->else_part);
}

PTR(Val) IfExpr::interp(PTR(Env) env) {
    PTR(Val) test_val = test_part->interp(env);
    
    if (test_val->is_true()) {
        return then_part->interp(env);
    } else {
        return else_part->interp(env);
    }
}

bool IfExpr::has_variable() {
    return test_part->has_variable() || then_part->has_variable() || else_part->has_variable();
}

void IfExpr::printExp(std::ostream& ot) const {
    ot << "(_if ";
    test_part->printExp(ot);
    ot << " _then ";
    then_part->printExp(ot);
    ot << " _else ";
    else_part->printExp(ot);
    ot << ")";
}

void IfExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    if (prec > prec_none) ot << "(";
    ot << "_if ";
    test_part->pretty_print_at(ot, prec_none);
    ot << "\n_then ";
    then_part->pretty_print_at(ot, prec_none);
    ot << "\n_else ";
    else_part->pretty_print_at(ot, prec_none);
    if (prec > prec_none) ot << ")";
}

//FunExpr
FunExpr::FunExpr(const std::string& formal_arg, PTR(Expr) body)
    : formal_arg(formal_arg), body(body) {}

bool FunExpr::equals(PTR(Expr) e) {
    PTR(FunExpr) f = CAST(FunExpr)(e);
    return f != nullptr && formal_arg == f->formal_arg && body->equals(f->body);
}

PTR(Val) FunExpr::interp(PTR(Env) env) {
    return NEW(FunVal)(formal_arg, body, env);
}

bool FunExpr::has_variable() {
    return body->has_variable();
}

void FunExpr::printExp(std::ostream& ot) const {
    ot << "(_fun (" << formal_arg << ") ";
    body->printExp(ot);
    ot << ")";
}

void FunExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    if (prec > prec_none) ot << "(";
    ot << "_fun (" << formal_arg << ")\n  ";
    body->pretty_print_at(ot, prec_none);
    if (prec > prec_none) ot << ")";
}

//CallExpr
CallExpr::CallExpr(PTR(Expr) to_be_called, PTR(Expr) actual_arg)
    : to_be_called(to_be_called), actual_arg(actual_arg) {}

bool CallExpr::equals(PTR(Expr) e) {
    PTR(CallExpr) c = CAST(CallExpr)(e);
    return c != nullptr && to_be_called->equals(c->to_be_called) && actual_arg->equals(c->actual_arg);
}

PTR(Val) CallExpr::interp(PTR(Env) env) {
    PTR(Val) func_val = to_be_called->interp(env);
    PTR(Val) arg_val = actual_arg->interp(env);
    return func_val->call(arg_val);
}

bool CallExpr::has_variable() {
    return to_be_called->has_variable() || actual_arg->has_variable();
}

void CallExpr::printExp(std::ostream& ot) const {
    to_be_called->printExp(ot);
    ot << "(";
    actual_arg->printExp(ot);
    ot << ")";
}

void CallExpr::pretty_print_at(std::ostream& ot, precedence_t prec) const {
    (void)prec;
    to_be_called->pretty_print_at(ot, prec_none);
    ot << "(";
    actual_arg->pretty_print_at(ot, prec_none);
    ot << ")";
}

// Parsing functions
PTR(Expr) parse_keyword(std::istream &in, char prefix) {
    std::string keyword;
    while (isalpha(in.peek())) {
        keyword += in.get();
    }

    if (keyword == "let") {
        return parse_let(in);
    }
    else if (keyword == "if") {
        return parse_if(in);
    }
    else if (keyword == "true") {
        return NEW(BoolExpr)(true);
    }
    else if (keyword == "false") {
        return NEW(BoolExpr)(false);
    }
    else if (keyword == "fun") {
        return parse_fun(in);
    }
    else {
        throw std::runtime_error("invalid keyword: " + keyword);
    }
}

PTR(Expr) parse_let(std::istream &in) {
    consume_whitespace(in);
    
    // Parse the variable name
    std::string var_name;
    while (std::isalnum(in.peek())) {
        var_name += in.get();
    }
    
    consume_whitespace(in);
    
    // Parse the equals sign
    if (in.peek() != '=') {
        throw std::runtime_error("expected '=' after variable in let expression");
    }
    in.get(); // consume '='
    
    consume_whitespace(in);
    
    // Parse the right-hand side expression
    PTR(Expr) rhs = parse_expr(in);
    
    consume_whitespace(in);
    
    // Parse the "_in" or "*in" keyword
    char prefix = in.peek();
    if (prefix != '_' && prefix != '*') {
        throw std::runtime_error("expected '_in' or '*in' after right-hand side in let expression");
    }
    in.get(); // consume '_' or '*'
    
    // Read "in"
    std::string keyword;
    while (std::isalpha(in.peek())) {
        keyword += in.get();
    }
    
    if (keyword != "in") {
        throw std::runtime_error("expected '_in' or '*in' after right-hand side in let expression");
    }
    
    consume_whitespace(in);
    
    // Parse the body expression
    PTR(Expr) body = parse_expr(in);
    
    return NEW(LetExpr)(var_name, rhs, body);
}

PTR(Expr) parse_if(std::istream &in) {
    consume_whitespace(in);
    
    // Parse test condition
    PTR(Expr) test_expr = parse_expr(in);
    
    consume_whitespace(in);
    
    // Parse "_then" or "*then" keyword
    char prefix = in.peek();
    if (prefix != '_' && prefix != '*') {
        throw std::runtime_error("expected '_then' or '*then' after test condition in if expression");
    }
    in.get(); // consume '_' or '*'
    
    // Read "then"
    std::string keyword;
    while (std::isalpha(in.peek())) {
        keyword += in.get();
    }
    
    if (keyword != "then") {
        throw std::runtime_error("expected '_then' or '*then' after test condition in if expression");
    }
    
    consume_whitespace(in);
    
    // Parse then branch
    PTR(Expr) then_expr = parse_expr(in);
    
    consume_whitespace(in);
    
    // Parse "_else" or "*else" keyword
    prefix = in.peek();
    if (prefix != '_' && prefix != '*') {
        throw std::runtime_error("expected '_else' or '*else' after then branch in if expression");
    }
    in.get(); // consume '_' or '*'
    
    // Read "else"
    keyword.clear();
    while (std::isalpha(in.peek())) {
        keyword += in.get();
    }
    
    if (keyword != "else") {
        throw std::runtime_error("expected '_else' or '*else' after then branch in if expression");
    }
    
    consume_whitespace(in);
    
    // Parse else branch
    PTR(Expr) else_expr = parse_expr(in);
    
    return NEW(IfExpr)(test_expr, then_expr, else_expr);
}

PTR(Expr) parse_fun(std::istream& in) {
    consume_whitespace(in);
    if (in.peek() != '(') throw std::runtime_error("expected (");
    in.get(); // consume '('
    
    consume_whitespace(in);
    
    std::string var;
    while (std::isalnum(in.peek())) var += in.get();
    
    consume_whitespace(in);
    
    if (in.peek() != ')') throw std::runtime_error("expected )");
    in.get(); // consume ')'
    
    consume_whitespace(in);
    
    PTR(Expr) body = parse_expr(in);
    return NEW(FunExpr)(var, body);
}

PTR(Expr) parse(std::istream &in) {
    consume_whitespace(in);
    return parse_expr(in);
}

PTR(Expr) parse_str(const std::string &s) {
    std::istringstream in(s);
    try {
        return parse(in);
    } catch (const std::invalid_argument& e) {
        throw std::runtime_error(std::string("invalid input"));
    } catch (const std::runtime_error& e) {
        throw std::runtime_error(std::string("invalid input"));
    }
}

PTR(Expr) parse_expr(std::istream &in) {
    PTR(Expr) lhs = parse_comparg(in);
    consume_whitespace(in);
    
    // Check for equality operator
    if (in.peek() == '=') {
        in.get();
        if (in.peek() == '=') {
            in.get();
            consume_whitespace(in);
            PTR(Expr) rhs = parse_expr(in); // Right recursive for equality
            return NEW(EqExpr)(lhs, rhs);
        } else {
            // Single '=' is not valid here
            throw std::runtime_error("invalid input: expected '=='");
        }
    }
    
    return lhs;
}

PTR(Expr) parse_comparg(std::istream &in) {
    PTR(Expr) lhs = parse_addend(in);
    consume_whitespace(in);
    
    while (in.peek() == '+') {
        in.get();
        consume_whitespace(in);
        PTR(Expr) rhs = parse_addend(in);
        lhs = NEW(AddExpr)(lhs, rhs);
        consume_whitespace(in);
    }
    
    return lhs;
}

PTR(Expr) parse_addend(std::istream &in) {
    PTR(Expr) lhs = parse_multicand(in);
    consume_whitespace(in);
    
    while (in.peek() == '*') {
        in.get();
        consume_whitespace(in);
        PTR(Expr) rhs = parse_multicand(in);
        lhs = NEW(MultExpr)(lhs, rhs);
        consume_whitespace(in);
    }
    
    return lhs;
}

PTR(Expr) parse_multicand(std::istream &in) {
    PTR(Expr) expr = parse_inner(in);
    consume_whitespace(in);
    
    while (in.peek() == '(') {
        in.get(); // consume '('
        consume_whitespace(in);
        
        PTR(Expr) arg = parse_expr(in);
        consume_whitespace(in);
        
        if (in.peek() != ')') {
            throw std::runtime_error("expected closing parenthesis in function call");
        }
        in.get(); // consume ')'
        
        expr = NEW(CallExpr)(expr, arg);
        consume_whitespace(in);
    }
    
    return expr;
}

PTR(Expr) parse_inner(std::istream& in) {
    consume_whitespace(in);
    
    char next = in.peek();
    
    if (next == '(') {
        in.get(); // consume '('
        consume_whitespace(in);
        
        if (in.peek() == ')') {
            in.get(); // consume ')'
            throw std::runtime_error("empty parentheses are not allowed");
        }
        
        PTR(Expr) expr = parse_expr(in);
        consume_whitespace(in);
        
        if (in.peek() != ')') {
            throw std::runtime_error("expected closing parenthesis");
        }
        in.get(); // consume ')'
        return expr;
    } else if (isdigit(next)) {
        return parse_num(in);
    } else if (next == '_' || next == '*') {
        char prefix = next;
        in.get(); // consume '_' or '*'
        return parse_keyword(in, prefix);
    } else if (isalpha(next)) {
        return parse_var(in);
    } else if (next == '-') {
        in.get(); // consume '-'
        if (isdigit(in.peek())) {
            int num;
            in >> num;
            return NEW(NumExpr)(-num);
        } else {
            throw std::runtime_error("invalid negative number format");
        }
    } else {
        throw std::runtime_error("unexpected character in expression");
    }
}

PTR(Expr) parse_var(std::istream &in) {
    std::string var;
    while (std::isalnum(in.peek())) {
        var += in.get();
    }
    
    if (var.empty()) {
        throw std::runtime_error("expected variable name");
    }
    
    return NEW(VarExpr)(var);
}

PTR(Expr) parse_num(std::istream &in) {
    int num;
    if (!(in >> num)) {
        throw std::runtime_error("invalid number format");
    }
    return NEW(NumExpr)(num);
}

void consume_whitespace(std::istream &in) {
    while (std::isspace(in.peek())) in.get();
}
