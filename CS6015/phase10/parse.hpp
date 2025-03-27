#ifndef PARSE_HPP
#define PARSE_HPP

#include <iostream>
#include <string>
#include "expr.h"

Expr* parse_str(const std::string& input);
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
void consume(std::istream& in, char expect);
void skip_whitespace(std::istream& in);

#endif 
