#include "catch.h"
#include "expr.h"
#include "parse.hpp"

TEST_CASE("Basic expression equality") {
    CHECK( (new VarExpr("x"))->equals(new VarExpr("x")) == true );
    CHECK( (new VarExpr("x"))->equals(new VarExpr("y")) == false );
    CHECK( (new NumExpr(1))->equals(new VarExpr("x")) == false );
    CHECK( (new NumExpr(3))->equals(new NumExpr(3)) == true );
}

TEST_CASE("Add expressions") {
    CHECK( (new AddExpr(new NumExpr(2),new NumExpr(3)))->equals(new AddExpr(new NumExpr(2),new NumExpr(3))) == true );
    CHECK( (new AddExpr(new NumExpr(2),new NumExpr(3)))->equals(new AddExpr(new NumExpr(3),new NumExpr(2))) == false );
}

TEST_CASE("Mult expressions") {
    CHECK( (new MultExpr(new NumExpr(2),new NumExpr(3)))->equals(new MultExpr(new NumExpr(2),new NumExpr(3))) == true );
    CHECK( (new MultExpr(new NumExpr(2),new NumExpr(3)))->equals(new MultExpr(new NumExpr(3),new NumExpr(2))) == false );
    CHECK( (new MultExpr(new NumExpr(2),new NumExpr(3)))->equals(new AddExpr(new NumExpr(3),new NumExpr(2))) == false );
}

TEST_CASE("Substitution") {
    CHECK( (new AddExpr(new VarExpr("x"), new NumExpr(7)))
       ->subst("x", new VarExpr("y"))
       ->equals(new AddExpr(new VarExpr("y"), new NumExpr(7))) );
    CHECK( (new VarExpr("x"))
       ->subst("x", new AddExpr(new VarExpr("y"),new NumExpr(7)))
       ->equals(new AddExpr(new VarExpr("y"),new NumExpr(7))) );
}

TEST_CASE("Print expressions") {
    CHECK( (new NumExpr(5))->to_string() == "5" );
    CHECK( (new AddExpr(new NumExpr(2), new NumExpr(3)))->to_string() == "2 + 3" );
    CHECK( (new MultExpr(new NumExpr(2), new NumExpr(3)))->to_string() == "2 * 3" );
}

TEST_CASE("Pretty print") {
    CHECK( (new MultExpr(new NumExpr(1), new AddExpr(new NumExpr(2), new NumExpr(3))))->pretty_print() == "1 * (2 + 3)" );
    CHECK( (new LetExpr("x", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1))))->pretty_print() == "(_let x=5 _in (x + 1))" );
}

TEST_CASE("Function expressions") {
    SECTION("Basic function") {
        Expr* fun = new FunExpr("x", new AddExpr(new VarExpr("x"), new NumExpr(1)));
        CHECK( fun->to_string() == "(_fun (x) x + 1)" );
        CHECK( fun->pretty_print() == "(_fun (x)\n  x + 1)" );
    }

    SECTION("Nested function") {
        Expr* fun = new FunExpr("x",
                         new FunExpr("y",
                             new AddExpr(new VarExpr("x"), new VarExpr("y"))));
        CHECK( fun->to_string() == "(_fun (x) (_fun (y) x + y))" );
        CHECK( fun->pretty_print() == "(_fun (x)\n  (_fun (y)\n    x + y))" );
    }
}

TEST_CASE("Function calls") {
    SECTION("Basic call") {
        Expr* call = new CallExpr(new VarExpr("f"), new NumExpr(5));
        CHECK( call->to_string() == "f(5)" );
        CHECK( call->pretty_print() == "f(5)" );
    }

    SECTION("Chained calls") {
        Expr* call = new CallExpr(
                        new CallExpr(new VarExpr("f"), new NumExpr(1)),
                        new NumExpr(2));
        CHECK( call->to_string() == "f(1)(2)" );
        CHECK( call->pretty_print() == "f(1)(2)" );
    }
}

TEST_CASE("Factorial example") {
    Expr* fact = parse_str(
        "_let factrl = _fun (factrl) "
        "              _fun (x) "
        "                _if x == 1 "
        "                _then 1 "
        "                _else x * factrl(factrl)(x + -1) "
        "_in  factrl(factrl)(10)");
    
    CHECK( fact->pretty_print() ==
        "(_let factrl=_fun (factrl)\n"
        "  (_fun (x)\n"
        "    (_if x == 1\n"
        "     _then 1\n"
        "     _else x * factrl(factrl)(x + -1)))\n"
        " _in factrl(factrl)(10))");
}

TEST_CASE("Parsing") {
    SECTION("Basic parsing") {
        CHECK( parse_str("1")->equals(new NumExpr(1)) );
        CHECK( parse_str("x")->equals(new VarExpr("x")) );
        CHECK( parse_str("x + y")->equals(new AddExpr(new VarExpr("x"), new VarExpr("y"))) );
    }

    SECTION("Function parsing") {
        CHECK( parse_str("_fun (x) x + 1")->equals(
            new FunExpr("x", new AddExpr(new VarExpr("x"), new NumExpr(1)))) );
        CHECK( parse_str("f(x)")->equals(
            new CallExpr(new VarExpr("f"), new VarExpr("x"))) );
    }

    SECTION("Invalid input") {
        CHECK_THROWS_WITH( parse_str("()"), "invalid input" );
        CHECK_THROWS_WITH( parse_str("_fun x x"), "invalid input" );
        CHECK_THROWS_WITH( parse_str("f x"), "invalid input" );
    }
}

TEST_CASE("Let expressions with functions") {
    Expr* letWithFun = parse_str(
        "_let f = _fun (x) x * 2 "
        "_in f(5)");
    
    CHECK( letWithFun->pretty_print() ==
        "(_let f=_fun (x)\n"
        "  x * 2\n"
        " _in f(5))");
}
