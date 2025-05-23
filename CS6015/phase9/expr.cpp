#include "expr.h"
#include "val.h"

std::string Expr::to_string() {
    std::stringstream st;
    this->printExp(st);
    return st.str();
}

//NUM

NumExpr::NumExpr(int num) : num(num) {}

bool NumExpr::equals(Expr* rhs) {
    NumExpr* n = dynamic_cast<NumExpr*>(rhs);
    return n && this->num == n->num;
}

Val* NumExpr::interp() {
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

std::string NumExpr::pretty_print () const { return std::to_string(num); }

//ADD

AddExpr::AddExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}


bool AddExpr::equals(Expr* rhs) {
    if (rhs == nullptr) {
        return false;
    }
    AddExpr* n = dynamic_cast<AddExpr*>(rhs);
    if (n == nullptr) {
        return false;
    }
    return lhs->equals(n->lhs.get()) && this->rhs->equals(n->rhs.get());
}

Val* AddExpr::interp() {
    std::unique_ptr<Val> leftVal(lhs->interp());
    std::unique_ptr<Val> rightVal(rhs->interp());
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
    std::string left_str = lhs->pretty_print();
    std::string right_str = rhs->pretty_print();
    return left_str + " + " + right_str;
}
//MULT

MultExpr::MultExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

bool MultExpr::equals(Expr* rhs) {
    MultExpr* n = dynamic_cast<MultExpr*>(rhs);
    return n && lhs->equals(n->lhs.get()) && this->rhs->equals(n->rhs.get());
}

Val* MultExpr::interp() {
    std::unique_ptr<Val> leftVal(lhs->interp());
    std::unique_ptr<Val> rightVal(rhs->interp());
    return leftVal->mult_with(rightVal.get());
}

bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

Expr* MultExpr::subst(std::string var, Expr* replacement) {
    return new MultExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

precedence_t MultExpr::precedence() {
    return prec_mult;
}

void MultExpr::printExp(std::ostream &ot) const {
    lhs->printExp(ot);
    ot << " * ";
    rhs->printExp(ot);
}


std::string MultExpr::pretty_print() const {
    std::string left_str = lhs->pretty_print();
    std::string right_str = rhs->pretty_print();

    // Add parentheses
    if (dynamic_cast<MultExpr*>(lhs.get()) != nullptr) {
        left_str = "(" + left_str + ")";
    }
    if (dynamic_cast<AddExpr*>(rhs.get()) != nullptr) {
        right_str = "(" + right_str + ")";
    }

    return left_str + " * " + right_str;
}


//var

VarExpr::VarExpr(std::string name) : name(name) {}

bool VarExpr::equals(Expr* rhs) {
    VarExpr* n = dynamic_cast<VarExpr*>(rhs);
    return n && name == n->name;
}

Val* VarExpr::interp() {
    throw std::runtime_error("Cannot interpret a variable");
}

bool VarExpr::has_variable() {
    return true;
}

Expr* VarExpr::subst(std::string var, Expr* replacement) {
    if (this->name == var) {
        return replacement;
    } else {
        return new VarExpr(this->name);
    }
}

precedence_t VarExpr::precedence() {
    return prec_none;
}

void VarExpr::printExp(std::ostream &ot) const {
    ot << name;
}

std::string VarExpr::pretty_print () const {
    return name;
}

//let

LetExpr::LetExpr(std::string var, Expr* rhs, Expr* itself)
    : var(var), rhs(std::unique_ptr<Expr>(rhs)), itself(std::unique_ptr<Expr>(itself)) {}

bool LetExpr::equals(Expr* rhs) {
    LetExpr* other = dynamic_cast<LetExpr*>(rhs);
    return other && var == other->var && this->rhs->equals(other->rhs.get()) && this->itself->equals(other->itself.get());
}
Val* LetExpr::interp() {
    std::unique_ptr<Val> rhsVal(rhs->interp());
    return itself->subst(var, new NumExpr(rhsVal->to_int()))->interp();
}
bool LetExpr::has_variable() {
    return rhs->has_variable() || itself->has_variable();
}
Expr* LetExpr::subst(std::string var, Expr* replacement) {
    if (this->var == var) {
        return new LetExpr(this->var, this->rhs->subst(var, replacement), this->itself.get());
    } else {
        return new LetExpr(this->var, this->rhs->subst(var, replacement), this->itself->subst(var, replacement));
    }
}
precedence_t LetExpr::precedence() { return prec_let; }


void LetExpr::printExp(std::ostream &ot) const {
    ot << "(_let " << var << "=";
    rhs->printExp(ot);
    ot << " _in ";
    itself->printExp(ot);
    ot << ")";
}
std::string LetExpr::pretty_print() const {
    std::string body_str = itself->pretty_print();
    if (dynamic_cast<AddExpr*>(itself.get()) != nullptr || dynamic_cast<MultExpr*>(itself.get()) != nullptr) {
        body_str = "(" + body_str + ")";
    }
    return "(_let " + var + "=" + rhs->pretty_print() + " _in " + body_str + ")";
}
//Bool

BoolExpr::BoolExpr(bool value) : value(value) {}
bool BoolExpr::equals(Expr* rhs) {
    BoolExpr* bool_rhs = dynamic_cast<BoolExpr*>(rhs);
    return bool_rhs && (this->value == bool_rhs->value);
}
Val* BoolExpr::interp() {
    return new BoolVal(value);
}
bool BoolExpr::has_variable() {
    return false;
}
Expr* BoolExpr::subst(std::string var, Expr* replacement) {
    return this;
}
void BoolExpr::printExp(std::ostream &out) const {
    out << (value ? "_true" : "_false");
}
precedence_t BoolExpr::precedence() {
    return prec_none;
}
std::string BoolExpr::pretty_print() const {
    return value ? "_true" : "_false";
}

// Eq

EqExpr::EqExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}
bool EqExpr::equals(Expr* rhs) {
    EqExpr* eq_rhs = dynamic_cast<EqExpr*>(rhs);
    return eq_rhs && lhs->equals(eq_rhs->lhs) && rhs->equals(eq_rhs->rhs);
}
Val* EqExpr::interp() {
    return new BoolVal(lhs->interp()->equals(rhs->interp()));
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
    out << "==";
    rhs->printExp(out);
    out << ")";
}
precedence_t EqExpr::precedence() {
    return prec_none;
}
std::string EqExpr::pretty_print() const {
    return "(" + lhs->pretty_print() + " == " + rhs->pretty_print() + ")";
}

// IfExpr
IfExpr::IfExpr(Expr* condition, Expr* then_branch, Expr* else_branch) : condition(condition), then_branch(then_branch), else_branch(else_branch) {}
bool IfExpr::equals(Expr* rhs) {
    IfExpr* if_rhs = dynamic_cast<IfExpr*>(rhs);
    return if_rhs && condition->equals(if_rhs->condition) && then_branch->equals(if_rhs->then_branch) && else_branch->equals(if_rhs->else_branch);
}
Val* IfExpr::interp() {
    Val* cond_val = condition->interp();
    BoolVal* bool_cond = dynamic_cast<BoolVal*>(cond_val);
    if (!bool_cond) {
        throw std::runtime_error("Condition in if expression must be a boolean");
    }
    return bool_cond->is_true() ? then_branch->interp() : else_branch->interp();
}
bool IfExpr::has_variable() {
    return condition->has_variable() || then_branch->has_variable() || else_branch->has_variable();
}
Expr* IfExpr::subst(std::string var, Expr* replacement) {
    return new IfExpr(condition->subst(var, replacement), then_branch->subst(var, replacement), else_branch->subst(var, replacement));
}
std::string IfExpr::pretty_print() const {
    std::ostringstream out;
    out << "(_if " << condition->pretty_print() << "\n     _then " << then_branch->pretty_print() << "\n     _else " << else_branch->pretty_print() << ")";
    return out.str();
}

void IfExpr::printExp(std::ostream &out) const {
    out << "(_if ";
    condition->printExp(out);
    out << " _then ";
    then_branch->printExp(out);
    out << " _else ";
    else_branch->printExp(out);
    out << ")";
}

precedence_t IfExpr::precedence(){
    return prec_none;
}

