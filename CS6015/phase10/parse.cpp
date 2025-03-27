#include <iostream>
#include <sstream>
#include <cctype>
#include <stdexcept>
#include "expr.h"
#include "parse.hpp"

Expr* parse_expr(std::istream& in);
Expr* parse_add(std::istream& in);
Expr* parse_mult(std::istream& in);
Expr* parse_primary(std::istream& in);
Expr* parse_num(std::istream& in);
Expr* parse_bool(std::istream& in);
Expr* parse_let(std::istream& in);
Expr* parse_if(std::istream& in);
Expr* parse_fun(std::istream& in);
Expr* parse_call(Expr* fun, std::istream& in);

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
        consume(in, '-');
        if (!isdigit(in.peek())) {
            throw std::runtime_error("invalid input");
        }
    }

    while (isdigit(in.peek())) {
        int c = in.get() - '0';
        n = n * 10 + c;
    }

    if (negative) n = -n;
    return new NumExpr(n);
}

Expr* parse_bool(std::istream& in) {
    std::string token;
    while (isalpha(in.peek()) || in.peek() == '_') {
        token.push_back(in.get());
    }

    if (token == "_true") {
        return new BoolExpr(true);
    }
    else if (token == "_false") {
        return new BoolExpr(false);
    }
    else {
        return new VarExpr(token);
    }
}

Expr* parse_fun(std::istream& in) {
    skip_whitespace(in);
    consume(in, '(');
    skip_whitespace(in);
    
    std::string arg;
    while (isalpha(in.peek())) {
        arg.push_back(in.get());
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
    
    // Handle chained calls like f(x)(y)
    skip_whitespace(in);
    if (in.peek() == '(') {
        return parse_call(new CallExpr(fun, arg), in);
    }
    
    return new CallExpr(fun, arg);
}

Expr* parse_primary(std::istream& in) {
    skip_whitespace(in);
    char c = in.peek();

    if (isdigit(c) || c == '-') {
        return parse_num(in);
    }
    else if (isalpha(c) || c == '_') {
        std::streampos pos = in.tellg();
        std::string token;
        while (isalpha(in.peek()) || in.peek() == '_') {
            token.push_back(in.get());
        }
        
        if (token == "_fun") {
            return parse_fun(in);
        }
        else {
            in.seekg(pos);
            return parse_bool(in);
        }
    }
    else if (c == '(') {
        consume(in, '(');
        skip_whitespace(in);
        Expr* expr = parse_expr(in);
        skip_whitespace(in);
        consume(in, ')');
        
        // Check for function call
        skip_whitespace(in);
        if (in.peek() == '(') {
            return parse_call(expr, in);
        }
        return expr;
    }
    else {
        throw std::runtime_error("invalid input");
    }
}

Expr* parse_mult(std::istream& in) {
    Expr* expr = parse_primary(in);
    skip_whitespace(in);

    while (in.peek() == '*') {
        consume(in, '*');
        Expr* rhs = parse_primary(in);
        expr = new MultExpr(expr, rhs);
        skip_whitespace(in);
    }

    return expr;
}

Expr* parse_add(std::istream& in) {
    Expr* expr = parse_mult(in);
    skip_whitespace(in);

    while (in.peek() == '+') {
        consume(in, '+');
        Expr* rhs = parse_mult(in);
        expr = new AddExpr(expr, rhs);
        skip_whitespace(in);
    }

    return expr;
}

Expr* parse_let(std::istream& in) {
    skip_whitespace(in);
    
    std::string var;
    while (isalpha(in.peek())) {
        var.push_back(in.get());
    }
    
    skip_whitespace(in);
    consume(in, '=');
    Expr* rhs = parse_expr(in);
    
    skip_whitespace(in);
    std::string in_keyword;
    while (isalpha(in.peek()) || in.peek() == '_') {
        in_keyword.push_back(in.get());
    }
    if (in_keyword != "_in") {
        throw std::runtime_error("expected _in keyword");
    }
    
    Expr* body = parse_expr(in);
    return new LetExpr(var, rhs, body);
}

Expr* parse_if(std::istream& in) {
    Expr* cond = parse_expr(in);
    
    skip_whitespace(in);
    std::string then_keyword;
    while (isalpha(in.peek()) || in.peek() == '_') {
        then_keyword.push_back(in.get());
    }
    if (then_keyword != "_then") {
        throw std::runtime_error("expected _then keyword");
    }
    
    Expr* then_branch = parse_expr(in);
    
    skip_whitespace(in);
    std::string else_keyword;
    while (isalpha(in.peek()) || in.peek() == '_') {
        else_keyword.push_back(in.get());
    }
    if (else_keyword != "_else") {
        throw std::runtime_error("expected _else keyword");
    }
    
    Expr* else_branch = parse_expr(in);
    return new IfExpr(cond, then_branch, else_branch);
}

Expr* parse_expr(std::istream& in) {
    skip_whitespace(in);
    
    if (in.peek() == '_') {
        std::streampos pos = in.tellg();
        std::string keyword;
        while (isalpha(in.peek()) || in.peek() == '_') {
            keyword.push_back(in.get());
        }
        
        if (keyword == "_let") {
            return parse_let(in);
        }
        else if (keyword == "_if") {
            return parse_if(in);
        }
        else {
            in.seekg(pos);
        }
    }
    
    return parse_add(in);
}

Expr* parse_str(const std::string& input) {
    std::istringstream in(input);
    try {
        Expr* expr = parse_expr(in);
        skip_whitespace(in);

        if (in.peek() != std::istream::traits_type::eof()) {
            throw std::runtime_error("invalid input");
        }

        return expr;
    }
    catch (const std::runtime_error& e) {
        throw std::runtime_error("invalid input");
    }
}
