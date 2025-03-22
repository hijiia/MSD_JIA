#include <iostream>
#include <sstream>
#include <cctype>
#include <stdexcept>
#include "expr.h"
#include "parse.hpp"


Expr* parse_expr(std::istream& in);

void consume(std::istream& in, char expect) {
    char c = in.get();
    if (c != expect) {
        throw std::runtime_error("invalid input");
    }
}

void skip_whitespace(std::istream& in) {
    while (isspace(in.peek())) in.get();
}

//parse numbers, extending to handle negative integers
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

// parse boolean expressions
Expr* parse_bool(std::istream& in) {
    std::string token;
    while (isalpha(in.peek()) || in.peek() == '_') {
        token.push_back(in.get());
    }
    if (token == "_true") {
        return new BoolExpr(true);
    } else if (token == "_false") {
        return new BoolExpr(false);
    } else if (token.find('_') != std::string::npos) {
        throw std::runtime_error("invalid input");
    } else {
        return new VarExpr(token);
    }
}

// parse primary expressions
Expr* parse_primary(std::istream& in) {
    skip_whitespace(in);
    char c = in.peek();

    if (isdigit(c) || c == '-') {
        return parse_num(in);
    } else if (isalpha(c) || c == '_') {
        return parse_bool(in);
    } else if (c == '(') {
        consume(in, '(');
        skip_whitespace(in);
        if (in.peek() == ')') {
            throw std::runtime_error("invalid input");
        }
        Expr* expr = parse_expr(in);
        skip_whitespace(in);
        consume(in, ')');
        return expr;
    } else {
        throw std::runtime_error("invalid input");
    }
}

//parse multiplication expressions
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

//parse addition expressions
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

Expr* parse_expr(std::istream& in) {
    return parse_add(in);
}

// initiate parsing 
Expr* parse_str(const std::string& input) {
    std::istringstream in(input);
    try {
        Expr* expr = parse_expr(in);
        skip_whitespace(in);

        if (in.peek() != std::istream::traits_type::eof()) {
            throw std::runtime_error("invalid input");
        }

        return expr;
    } catch (const std::exception& e) {
        throw std::runtime_error("invalid input");
    }
}
