#include <iostream>
#include <sstream>
#include <cctype>
#include <stdexcept>
#include "expr.h"
#include "parse.hpp"

void consume(std::istream& in, char expect) {
    char c = in.get();
    if (c != expect) {
        throw std::runtime_error("invalid input");
    }
}

void skip_whitespace(std::istream& in) {
    while (isspace(in.peek())) in.get();
}

Expr* parse_num(std::istream& in) {
    int n = 0;
    bool negative = false;
    
    if (in.peek() == '-') {
        negative = true;
        in.get();
    }
    
    if (!isdigit(in.peek())) {
        throw std::runtime_error("invalid number");
    }
    
    while (isdigit(in.peek())) {
        n = n * 10 + (in.get() - '0');
    }
    
    return new NumExpr(negative ? -n : n);
}

Expr* parse_var(std::istream& in) {
    std::string var;
    while (isalpha(in.peek()) || in.peek() == '_') {
        var += in.get();
    }
    return new VarExpr(var);
}


Expr* parse_bool(std::istream& in) {
    std::string token;
    while (isalpha(in.peek()) || in.peek() == '_') {
        token += in.get();
    }

    if (token == "_true") return new BoolExpr(true);
    if (token == "_false") return new BoolExpr(false);
    throw std::runtime_error("invalid input");
}

Expr* parse_fun(std::istream& in) {
    skip_whitespace(in);
    consume(in, '(');
    skip_whitespace(in);
    
    std::string arg;
    while (isalpha(in.peek())) {
        arg += in.get();
    }
    
    skip_whitespace(in);
    consume(in, ')');
    skip_whitespace(in);
    
    Expr* body = parse_expr(in);
    return new FunExpr(arg, body);
}

Expr* parse_call(Expr* fun, std::istream& in) {
    consume(in, '(');
    skip_whitespace(in);
    Expr* arg = parse_expr(in);
    skip_whitespace(in);
    consume(in, ')');
    return new CallExpr(fun, arg);
}
Expr* parse_primary(std::istream& in) {
    skip_whitespace(in);
    char c = in.peek();

    if (isdigit(c) || c == '-') {
        return parse_num(in);
    }
    else if (c == '(') {
        consume(in, '(');
        Expr* expr = parse_expr(in);
        skip_whitespace(in);
        consume(in, ')');
        
        // Handle function calls after parentheses
        skip_whitespace(in);
        if (in.peek() == '(') {
            return parse_call(expr, in);
        }
        return expr;
    }
    else if (isalpha(c) || c == '_') {
        std::string token;
        while (isalpha(in.peek()) || in.peek() == '_' || isdigit(in.peek())) {
            token += in.get();
        }

        // Handle keywords
        if (token == "_true") return new BoolExpr(true);
        if (token == "_false") return new BoolExpr(false);
        if (token == "_fun") return parse_fun(in);
        if (token == "_let") return parse_let(in);
        if (token == "_if") return parse_if(in);

        // Handle function calls (f(x))
        skip_whitespace(in);
        if (in.peek() == '(') {
            Expr* fun = new VarExpr(token);
            return parse_call(fun, in);
        }

        // Regular variable
        return new VarExpr(token);
    }
    else {
        throw std::runtime_error("invalid input");
    }
}


Expr* parse_mult(std::istream& in) {
    Expr* lhs = parse_primary(in);
    skip_whitespace(in);

    while (in.peek() == '*') {
        consume(in, '*');
        skip_whitespace(in);
        Expr* rhs = parse_primary(in);
        lhs = new MultExpr(lhs, rhs);
        skip_whitespace(in);
    }

    return lhs;
}

Expr* parse_add(std::istream& in) {
    Expr* lhs = parse_mult(in);
    skip_whitespace(in);

    while (in.peek() == '+') {
        consume(in, '+');
        skip_whitespace(in);
        Expr* rhs = parse_mult(in);
        lhs = new AddExpr(lhs, rhs);
        skip_whitespace(in);
    }

    return lhs;
}

Expr* parse_let(std::istream& in) {
    skip_whitespace(in);
    std::string var = parse_var(in)->to_string();
    
    skip_whitespace(in);
    consume(in, '=');
    Expr* rhs = parse_expr(in);
    
    skip_whitespace(in);
    if (in.peek() != '_') throw std::runtime_error("expected _in");
    std::string in_keyword;
    while (isalpha(in.peek()) || in.peek() == '_') {
        in_keyword += in.get();
    }
    if (in_keyword != "_in") throw std::runtime_error("expected _in");
    
    Expr* body = parse_expr(in);
    return new LetExpr(var, rhs, body);
}

Expr* parse_if(std::istream& in) {
    // Parse _if keyword
    skip_whitespace(in);
    std::string keyword;
    char c;
    
    // Read the keyword
    while ((c = in.peek()) && (isalpha(c) || c == '_')) {
        keyword += in.get();
    }
    
    if (keyword != "_if") {
        // Put back characters
        for (auto it = keyword.rbegin(); it != keyword.rend(); ++it) {
            in.putback(*it);
        }
        throw std::runtime_error("expected _if");
    }

    // Parse condition expression
    skip_whitespace(in);
    Expr* cond = parse_expr(in);
    
    // Parse _then keyword
    skip_whitespace(in);
    keyword.clear();
    while ((c = in.peek()) && (isalpha(c) || c == '_')) {
        keyword += in.get();
    }
    if (keyword != "_then") {
        throw std::runtime_error("expected _then");
    }
    
    // Parse then branch
    skip_whitespace(in);
    Expr* then_branch = parse_expr(in);
    
    // Parse _else keyword
    skip_whitespace(in);
    keyword.clear();
    while ((c = in.peek()) && (isalpha(c) || c == '_')) {
        keyword += in.get();
    }
    if (keyword != "_else") {
        throw std::runtime_error("expected _else");
    }
    
    skip_whitespace(in);
    Expr* else_branch = parse_expr(in);
    
    return new IfExpr(cond, then_branch, else_branch);
}

Expr* parse_expr(std::istream& in) {
    skip_whitespace(in);
    
    if (in.peek() == '_') {
        std::streampos pos = in.tellg();
        std::string keyword;
        while (isalpha(in.peek()) || in.peek() == '_') {
            keyword += in.get();
        }
        
        if (keyword == "_let") {
            return parse_let(in);
        }
        else if (keyword == "_if") {
            return parse_if(in);
        }
        else if (keyword == "_true" || keyword == "_false") {
            in.seekg(pos);
            return parse_bool(in);
        }
        else if (keyword == "_fun") {
            return parse_fun(in);
        }
        else {
            in.seekg(pos);
        }
    }
    
    return parse_add(in);
}

Expr* parse_str(const std::string& input) {
    std::istringstream in(input);
    Expr* expr = parse_expr(in);
    skip_whitespace(in);
    
    if (in.peek() != EOF) {
        throw std::runtime_error("invalid input");
    }
    
    return expr;
}
