#include "expr.h"
#include "val.h"
#include "env.h"

std::string Expr::to_string() {
    std::stringstream st;
    this->printExp(st);
    return st.str();
}

// NUM
NumExpr::NumExpr(int num) : num(num) {}

bool NumExpr::equals(Expr* rhs) {
    NumExpr* n = dynamic_cast<NumExpr*>(rhs);
    return n && this->num == n->num;
}

Val* NumExpr::interp(Env* env) {
    return new NumVal(num);
}

bool NumExpr::has_variable() { return false; }

Expr* NumExpr::subst(std::string var, Expr* replacement) {
    return new NumExpr(this->num);
}

precedence_t NumExpr::precedence() { return prec_none; }

void NumExpr::printExp(std::ostream &ot) const {
    ot << num;
}

std::string NumExpr::pretty_print() const { return std::to_string(num); }

// ADD
AddExpr::AddExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

bool AddExpr::equals(Expr* rhs) {
    if (rhs == nullptr) return false;
    AddExpr* n = dynamic_cast<AddExpr*>(rhs);
    return n && lhs->equals(n->lhs.get()) && this->rhs->equals(n->rhs.get());
}

Val* AddExpr::interp(Env* env) {
    std::unique_ptr<Val> leftVal(lhs->interp(env));
    std::unique_ptr<Val> rightVal(rhs->interp(env));
    return leftVal->add_to(rightVal.get());
}

bool AddExpr::has_variable() { return lhs->has_variable() || rhs->has_variable(); }

Expr* AddExpr::subst(std::string var, Expr* replacement) {
    return new AddExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

precedence_t AddExpr::precedence() { return prec_add; }

void AddExpr::printExp(std::ostream &ot) const {
    lhs->printExp(ot);
    ot << " + ";
    rhs->printExp(ot);
}

std::string AddExpr::pretty_print() const {
    return lhs->pretty_print() + " + " + rhs->pretty_print();
}

// MULT
MultExpr::MultExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

bool MultExpr::equals(Expr* rhs) {
    MultExpr* n = dynamic_cast<MultExpr*>(rhs);
    return n && lhs->equals(n->lhs.get()) && this->rhs->equals(n->rhs.get());
}

Val* MultExpr::interp(Env* env) {
    std::unique_ptr<Val> leftVal(lhs->interp(env));
    std::unique_ptr<Val> rightVal(rhs->interp(env));
    return leftVal->mult_with(rightVal.get());
}

bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

Expr* MultExpr::subst(std::string var, Expr* replacement) {
    return new MultExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

precedence_t MultExpr::precedence() { return prec_mult; }

void MultExpr::printExp(std::ostream &ot) const {
    lhs->printExp(ot);
    ot << " * ";
    rhs->printExp(ot);
}

std::string MultExpr::pretty_print() const {
    std::string left = lhs->pretty_print();
    std::string right = rhs->pretty_print();
    
    if (lhs->precedence() < prec_mult) left = "(" + left + ")";
    if (rhs->precedence() < prec_mult) right = "(" + right + ")";
    
    return left + " * " + right;
}

// VAR
VarExpr::VarExpr(std::string name) : name(name) {}

bool VarExpr::equals(Expr* rhs) {
    VarExpr* n = dynamic_cast<VarExpr*>(rhs);
    return n && name == n->name;
}

Val* VarExpr::interp(Env* env) {
    return env->lookup(name);
}

bool VarExpr::has_variable() { return true; }

Expr* VarExpr::subst(std::string var, Expr* replacement) {
    if (name == var) return replacement;
    return new VarExpr(name);
}

precedence_t VarExpr::precedence() { return prec_none; }

void VarExpr::printExp(std::ostream &ot) const {
    ot << name;
}

std::string VarExpr::pretty_print() const { return name; }

// LET
LetExpr::LetExpr(std::string var, Expr* rhs, Expr* body)
    : var(var), rhs(rhs), body(body) {}

bool LetExpr::equals(Expr* rhs) {
    LetExpr* other = dynamic_cast<LetExpr*>(rhs);
    return other && var == other->var &&
           this->rhs->equals(other->rhs.get()) &&
           this->body->equals(other->body.get());
}

Val* LetExpr::interp(Env* env) {
    Val* rhs_val = rhs->interp(env);
    Env new_env(var, rhs_val, env);
    return body->interp(&new_env);
}

bool LetExpr::has_variable() {
    return rhs->has_variable() || body->has_variable();
}

Expr* LetExpr::subst(std::string var, Expr* replacement) {
    if (this->var == var) {
        return new LetExpr(var, rhs->subst(var, replacement), body.get());
    }
    return new LetExpr(var, rhs->subst(var, replacement),
                     body->subst(var, replacement));
}

precedence_t LetExpr::precedence() { return prec_let; }

void LetExpr::printExp(std::ostream &ot) const {
    ot << "(_let " << var << "=";
    rhs->printExp(ot);
    ot << " _in ";
    body->printExp(ot);
    ot << ")";
}

std::string LetExpr::pretty_print() const {
    std::string body_str = body->pretty_print();
    if (body->precedence() < prec_let) {
        body_str = "(" + body_str + ")";
    }
    return "(_let " + var + " = " + rhs->pretty_print() + " _in " + body_str + ")";
}

// BOOL
BoolExpr::BoolExpr(bool value) : value(value) {}

bool BoolExpr::equals(Expr* rhs) {
    BoolExpr* b = dynamic_cast<BoolExpr*>(rhs);
    return b && value == b->value;
}

Val* BoolExpr::interp(Env* env) {
    return new BoolVal(value);
}

bool BoolExpr::has_variable() { return false; }

Expr* BoolExpr::subst(std::string var, Expr* replacement) {
    return this;
}

void BoolExpr::printExp(std::ostream &out) const {
    out << (value ? "_true" : "_false");
}

precedence_t BoolExpr::precedence() { return prec_none; }

std::string BoolExpr::pretty_print() const {
    return value ? "_true" : "_false";
}

// EQ
EqExpr::EqExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

bool EqExpr::equals(Expr* rhs) {
    EqExpr* e = dynamic_cast<EqExpr*>(rhs);
    return e && lhs->equals(e->lhs.get()) && this->rhs->equals(e->rhs.get());
}

Val* EqExpr::interp(Env* env) {
    Val* lhs_val = lhs->interp(env);
    Val* rhs_val = rhs->interp(env);
    return new BoolVal(lhs_val->equals(rhs_val));
}

bool EqExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

Expr* EqExpr::subst(std::string var, Expr* replacement) {
    return new EqExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

void EqExpr::printExp(std::ostream &out) const {
    out << "(";
    lhs->printExp(out);
    out << " == ";
    rhs->printExp(out);
    out << ")";
}

precedence_t EqExpr::precedence() { return prec_none; }

std::string EqExpr::pretty_print() const {
    return "(" + lhs->pretty_print() + " == " + rhs->pretty_print() + ")";
}

// IF
IfExpr::IfExpr(Expr* cond, Expr* then, Expr* else_)
    : cond(cond), then(then), else_(else_) {}

bool IfExpr::equals(Expr* rhs) {
    IfExpr* i = dynamic_cast<IfExpr*>(rhs);
    return i && cond->equals(i->cond.get()) &&
           then->equals(i->then.get()) &&
           else_->equals(i->else_.get());
}

Val* IfExpr::interp(Env* env) {
    Val* cond_val = cond->interp(env);
    BoolVal* b = dynamic_cast<BoolVal*>(cond_val);
    if (!b) throw std::runtime_error("Condition must be boolean");
    return b->is_true() ? then->interp(env) : else_->interp(env);
}

bool IfExpr::has_variable() {
    return cond->has_variable() || then->has_variable() || else_->has_variable();
}

Expr* IfExpr::subst(std::string var, Expr* replacement) {
    return new IfExpr(cond->subst(var, replacement),
                     then->subst(var, replacement),
                     else_->subst(var, replacement));
}

void IfExpr::printExp(std::ostream &out) const {
    out << "(_if ";
    cond->printExp(out);
    out << " _then ";
    then->printExp(out);
    out << " _else ";
    else_->printExp(out);
    out << ")";
}

precedence_t IfExpr::precedence() { return prec_none; }

std::string IfExpr::pretty_print() const {
    return "(_if " + cond->pretty_print() +
           "\n    _then " + then->pretty_print() +
           "\n    _else " + else_->pretty_print() + ")";
}

// FUN
FunExpr::FunExpr(std::string arg, Expr* body) : arg(arg), body(body) {}

bool FunExpr::equals(Expr* rhs) {
    FunExpr* f = dynamic_cast<FunExpr*>(rhs);
    return f && arg == f->arg && body->equals(f->body.get());
}

Val* FunExpr::interp(Env* env) {
    return new FunVal(arg, body.get(), env);
}

bool FunExpr::has_variable() {
    return body->has_variable();
}

Expr* FunExpr::subst(std::string var, Expr* replacement) {
    if (var == arg) return this;
    return new FunExpr(arg, body->subst(var, replacement));
}

void FunExpr::printExp(std::ostream &ot) const {
    ot << "(_fun (" << arg << ") ";
    body->printExp(ot);
    ot << ")";
}

precedence_t FunExpr::precedence() { return prec_fun; }

std::string FunExpr::pretty_print() const {
    std::string body_str = body->pretty_print();
    // Indent body
    size_t pos = 0;
    while ((pos = body_str.find("\n", pos)) {
        body_str.insert(pos + 1, "  ");
        pos += 3;
    }
    return "(_fun (" + arg + ")\n  " + body_str + ")";
}

// CALL
CallExpr::CallExpr(Expr* fun, Expr* arg) : fun(fun), arg(arg) {}

bool CallExpr::equals(Expr* rhs) {
    CallExpr* c = dynamic_cast<CallExpr*>(rhs);
    return c && fun->equals(c->fun.get()) && arg->equals(c->arg.get());
}

Val* CallExpr::interp(Env* env) {
    Val* fun_val = fun->interp(env);
    FunVal* f = dynamic_cast<FunVal*>(fun_val);
    if (!f) throw std::runtime_error("Cannot call non-function");
    Val* arg_val = arg->interp(env);
    return f->call(arg_val);
}

bool CallExpr::has_variable() {
    return fun->has_variable() || arg->has_variable();
}

Expr* CallExpr::subst(std::string var, Expr* replacement) {
    return new CallExpr(fun->subst(var, replacement), arg->subst(var, replacement));
}

void CallExpr::printExp(std::ostream &ot) const {
    fun->printExp(ot);
    ot << "(";
    arg->printExp(ot);
    ot << ")";
}

precedence_t CallExpr::precedence() { return prec_none; }

std::string CallExpr::pretty_print() const {
    return fun->pretty_print() + "(" + arg->pretty_print() + ")";
}
