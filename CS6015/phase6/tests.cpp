//
//  test.cpp
//  Phase1
//
//  Created by Jia Gao on 1/18/25.
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

TEST_CASE("Pretty Print") {
    CHECK((new Mult(new Num(1), new Add(new Num(2), new Num(3))))->to_pretty_string() == "1 * (2 + 3)");
    CHECK((new Mult(new Mult(new Num(8), new Num(1)), new VarExpr("y")))->to_pretty_string() == "(8 * 1) * y");
    CHECK((new Mult(new Add(new Num(3), new Num(5)), new Mult(new Num(6), new Num(1))))->to_pretty_string() == "(3 + 5) * 6 * 1");
    CHECK((new Mult(new Mult(new Num(7), new Num(7)), new Add(new Num(9), new Num(2))))->to_pretty_string() == "(7 * 7) * (9 + 2)");
}
TEST_CASE("LetExpr") {
    SECTION("equals") {
        LetExpr let1("x", new Num(5), new Add(new VarExpr("x"), new Num(2)));
        LetExpr let2("x", new Num(5), new Add(new VarExpr("x"), new Num(2)));
        LetExpr let3("y", new Num(5), new Add(new VarExpr("x"), new Num(2)));
        CHECK(let1.equals(&let2) == true);
        CHECK(let1.equals(&let3) == false);
    }

    SECTION("interp") {
        LetExpr let1("x", new Num(5), new Add(new VarExpr("x"), new Num(2)));
        CHECK(let1.interp() == 7);

        LetExpr let2("x", new Num(3), new Mult(new VarExpr("x"), new Num(4)));
        CHECK(let2.interp() == 12);
    }

    SECTION("has_variable") {
        LetExpr let1("x", new Num(5), new Add(new VarExpr("x"), new Num(2)));
        CHECK(let1.has_variable() == true);

        LetExpr let2("x", new Num(5), new Num(2));
        CHECK(let2.has_variable() == false);
    }

    SECTION("subst") {
        LetExpr let1("x", new Num(5), new Add(new VarExpr("x"), new VarExpr("y")));
        Expr* substituted = let1.subst("y", new Num(3));
        CHECK(substituted->interp() == 8);
        delete substituted;
    }

    SECTION("printExp") {
        LetExpr let1("x", new Num(5), new Add(new VarExpr("x"), new Num(2)));
        std::stringstream ss;
        let1.printExp(ss);
        CHECK(ss.str() == "(_let x=5 _in (x+2))");
    }

    SECTION("pretty_print") {
        LetExpr let1("x", new Num(5), new Add(new VarExpr("x"), new Num(2)));
        std::stringstream ss;
        let1.pretty_print(ss);
        CHECK(ss.str() == "_let x = 5\n_in  x + 2");
    }
}
TEST_CASE("Parse valid numbers") {
    CHECK(parse_str("42")->equals(new Num(42)));
    CHECK(parse_str("0")->equals(new Num(0)));
    CHECK(parse_str("123456")->equals(new Num(123456)));
}

TEST_CASE("Parse valid variables") {
    CHECK(parse_str("x")->equals(new VarExpr("x")));
    CHECK(parse_str("abc")->equals(new VarExpr("abc")));
}

TEST_CASE("Parse simple addition") {
    CHECK(parse_str("3+5")->equals(new Add(new Num(3), new Num(5))));
    CHECK(parse_str("10+20")->equals(new Add(new Num(10), new Num(20))));
}

TEST_CASE("Parse simple multiplication") {
    CHECK(parse_str("4*2")->equals(new Mult(new Num(4), new Num(2))));
    CHECK(parse_str("7*8")->equals(new Mult(new Num(7), new Num(8))));
}

TEST_CASE("Parse combined addition and multiplication") {
    CHECK(parse_str("3+4*5")->equals(
        new Add(new Num(3), new Mult(new Num(4), new Num(5)))
    ));
    CHECK(parse_str("2*3+4")->equals(
        new Add(new Mult(new Num(2), new Num(3)), new Num(4))
    ));
}

TEST_CASE("Parse negative numbers") {
    CHECK(parse_str("-5")->equals(new Num(-5)));
    CHECK(parse_str("-10")->equals(new Num(-10)));
}

TEST_CASE("Parse parentheses") {
    CHECK(parse_str("(3+4)")->equals(new Add(new Num(3), new Num(4))));
    CHECK(parse_str("(2*3)")->equals(new Mult(new Num(2), new Num(3))));
}

TEST_CASE("Parse nested parentheses") {
    CHECK(parse_str("(1+(2*3))")->equals(
        new Add(new Num(1), new Mult(new Num(2), new Num(3)))
    ));
    CHECK(parse_str("((1+2)*3)")->equals(
        new Mult(new Add(new Num(1), new Num(2)), new Num(3))
    ));
}

TEST_CASE("Parse invalid input") {
    CHECK_THROWS_WITH( parse_str("*3"), "invalid input" );
    CHECK_THROWS_WITH( parse_str("4**5"), "invalid input" );
    CHECK_THROWS_WITH( parse_str("(3+4"), "invalid input" );
    CHECK_THROWS_WITH( parse_str(")3+4("), "invalid input" );
    CHECK_THROWS_WITH( parse_str("( )"), "invalid input" );
}
