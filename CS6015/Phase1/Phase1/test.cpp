//
//  test.cpp
//  Phase1
//
//  Created by Jia Gao on 1/18/25.
//
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
