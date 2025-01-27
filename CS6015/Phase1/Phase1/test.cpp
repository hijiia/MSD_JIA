//
//  test.cpp
//  Phase1
//
//  Created by Jia Gao on 1/18/25.

#define CATCH_CONFIG_MAIN
#include <stdio.h>
#include "catch.h"
#include "exp.h"

TEST_CASE("Num equals") {
    CHECK((new Num(5))->equals(new Num(5)) == true);
    CHECK((new Num(5))->equals(new Num(10)) == false);
    CHECK((new Num(5))->equals(new Add(new Num(5), new Num(5))) == false);
}

TEST_CASE("Add equals") {
    CHECK((new Add(new Num(1), new Num(2)))->equals(new Add(new Num(1), new Num(2))) == true);
    CHECK((new Add(new Num(1), new Num(2)))->equals(new Add(new Num(2), new Num(1))) == false);
}

TEST_CASE("Mult equals") {
    CHECK((new Mult(new Num(2), new Num(3)))->equals(new Mult(new Num(2), new Num(3))) == true);
    CHECK((new Mult(new Num(2), new Num(3)))->equals(new Mult(new Num(3), new Num(2))) == false);
}

TEST_CASE("VarExpr equals") {
    CHECK((new VarExpr("x"))->equals(new VarExpr("x")) == true);
    CHECK((new VarExpr("x"))->equals(new VarExpr("y")) == false);
    CHECK((new VarExpr("x"))->equals(new Num(1)) == false);
}


TEST_CASE("Num interp") {
    CHECK((new Num(5))->interp() == 5);
}

TEST_CASE("Add interp") {
    CHECK((new Add(new Num(3), new Num(2)))->interp() == 5);
}

TEST_CASE("Mult interp") {
    CHECK((new Mult(new Num(3), new Num(2)))->interp() == 6);
}

TEST_CASE("VarExpr interp") {
    CHECK_THROWS_WITH((new VarExpr("x"))->interp(), "no value for variable");
}

TEST_CASE("has_variable") {
    CHECK((new Add(new VarExpr("x"), new Num(1)))->has_variable() == true);
    CHECK((new Mult(new Num(2), new Num(1)))->has_variable() == false);
}

TEST_CASE("subst") {
    CHECK((new Add(new VarExpr("x"), new Num(7)))
          ->subst("x", new VarExpr("y"))
          ->equals(new Add(new VarExpr("y"), new Num(7))));
    CHECK((new VarExpr("x"))
          ->subst("x", new Add(new VarExpr("y"), new Num(7)))
          ->equals(new Add(new VarExpr("y"), new Num(7))));
}
