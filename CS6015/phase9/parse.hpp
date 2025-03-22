#ifndef PARSE_H
#define PARSE_H

#include <iostream>
#include <sstream>
#include <string>
#include <stdexcept>
#include "expr.h"


void consume(std::istream& in, char expect);
void skip_whitespace(std::istream& in);

Expr* parse_num(std::istream& in);
Expr* parse_bool(std::istream& in);
Expr* parse_primary(std::istream& in);
Expr* parse_mult(std::istream& in);
Expr* parse_add(std::istream& in);
Expr* parse_expr(std::istream& in);
Expr* parse_str(const std::string& input);

#endif 
