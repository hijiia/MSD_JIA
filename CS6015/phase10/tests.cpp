#include "catch.h"
#include "expr.h"
#include "val.h"
#include "pointer.h"

TEST_CASE("Num equals") {
    CHECK((NEW(NumExpr)(5))->equals(NEW(NumExpr)(5)) == true);
    CHECK((NEW(NumExpr)(5))->equals(NEW(NumExpr)(10)) == false);
    CHECK((NEW(NumExpr)(5))->equals(NEW(AddExpr)(NEW(NumExpr)(5), NEW(NumExpr)(5))) == false);
}

TEST_CASE("Add equals") {
    CHECK((NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)))->equals(
        NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2))) == true);
    CHECK((NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)))->equals(
        NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(1))) == false);
}

TEST_CASE("Mult equals") {
    CHECK((NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3)))->equals(
        NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3))) == true);
    CHECK((NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3)))->equals(
        NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(2))) == false);
}

TEST_CASE("VarExpr equals") {
    CHECK((NEW(VarExpr)("x"))->equals(NEW(VarExpr)("x")) == true);
    CHECK((NEW(VarExpr)("x"))->equals(NEW(VarExpr)("y")) == false);
    CHECK((NEW(VarExpr)("x"))->equals(NEW(NumExpr)(1)) == false);
}

TEST_CASE("NumVal equals") {
    PTR(NumVal) num5 = NEW(NumVal)(5);
    PTR(NumVal) num5_2 = NEW(NumVal)(5);
    PTR(NumVal) num10 = NEW(NumVal)(10);
    
    CHECK(num5->equals(num5_2) == true);
    CHECK(num5->equals(num10) == false);
    CHECK(num5->equals(nullptr) == false);
}

TEST_CASE("Num interp") {
    PTR(NumExpr) n = NEW(NumExpr)(5);
    PTR(Val) result = n->interp();
    CHECK(result->equals(NEW(NumVal)(5)) == true);
}

TEST_CASE("Add interp") {
    PTR(AddExpr) a = NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(2));
    PTR(Val) result = a->interp();
    CHECK(result->equals(NEW(NumVal)(5)) == true);
}

TEST_CASE("Mult interp") {
    PTR(MultExpr) m = NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(2));
    PTR(Val) result = m->interp();
    CHECK(result->equals(NEW(NumVal)(6)) == true);
}

TEST_CASE("VarExpr interp") {
    CHECK_THROWS_WITH((NEW(VarExpr)("x"))->interp(), "no value for variable");
}

TEST_CASE("has_variable") {
    CHECK((NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))->has_variable() == true);
    CHECK((NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(1)))->has_variable() == false);
}

TEST_CASE("subst") {
    CHECK((NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)))
          ->subst("x", NEW(VarExpr)("y"))
          ->equals(NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))));
    CHECK((NEW(VarExpr)("x"))
          ->subst("x", NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7)))
          ->equals(NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))));
}

TEST_CASE("Pretty Print") {
    CHECK((NEW(MultExpr)(NEW(NumExpr)(1), NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3))))->to_pretty_string() == "1 * (2 + 3)");
    CHECK((NEW(MultExpr)(NEW(MultExpr)(NEW(NumExpr)(8), NEW(NumExpr)(1)), NEW(VarExpr)("y")))->to_pretty_string() == "(8 * 1) * y");
    CHECK((NEW(MultExpr)(NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(5)), NEW(MultExpr)(NEW(NumExpr)(6), NEW(NumExpr)(1))))->to_pretty_string() == "(3 + 5) * 6 * 1");
    CHECK((NEW(MultExpr)(NEW(MultExpr)(NEW(NumExpr)(7), NEW(NumExpr)(7)), NEW(AddExpr)(NEW(NumExpr)(9), NEW(NumExpr)(2))))->to_pretty_string() == "(7 * 7) * (9 + 2)");
}

TEST_CASE("NumVal methods") {
    PTR(NumVal) num5 = NEW(NumVal)(5);
    
    SECTION("to_string") {
        CHECK(num5->to_string() == "5");
    }
    
    SECTION("to_expr") {
        PTR(Expr) expr = num5->to_expr();
        CHECK(expr->equals(NEW(NumExpr)(5)) == true);
    }
    
    SECTION("add_to") {
        PTR(NumVal) num10 = NEW(NumVal)(10);
        PTR(Val) result = num5->add_to(num10);
        CHECK(result->equals(NEW(NumVal)(15)) == true);
    }
    
    SECTION("mult_with") {
        PTR(NumVal) num10 = NEW(NumVal)(10);
        PTR(Val) result = num5->mult_with(num10);
        CHECK(result->equals(NEW(NumVal)(50)) == true);
    }
}

TEST_CASE("LetExpr") {
    SECTION("equals") {
        PTR(LetExpr) let1 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)));
        PTR(LetExpr) let2 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)));
        PTR(LetExpr) let3 = NEW(LetExpr)("y", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)));
        CHECK(let1->equals(let2) == true);
        CHECK(let1->equals(let3) == false);
    }

    SECTION("interp") {
        PTR(LetExpr) let1 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)));
        PTR(Val) result1 = let1->interp();
        CHECK(result1->equals(NEW(NumVal)(7)) == true);

        PTR(LetExpr) let2 = NEW(LetExpr)("x", NEW(NumExpr)(3), NEW(MultExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(4)));
        PTR(Val) result2 = let2->interp();
        CHECK(result2->equals(NEW(NumVal)(12)) == true);
    }

    SECTION("has_variable") {
        PTR(LetExpr) let1 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)));
        CHECK(let1->has_variable() == true);

        PTR(LetExpr) let2 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(NumExpr)(2));
        CHECK(let2->has_variable() == false);
    }

    SECTION("subst") {
        PTR(LetExpr) let1 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y")));
        PTR(Expr) substituted = let1->subst("y", NEW(NumExpr)(3));
        PTR(Val) result = substituted->interp();
        CHECK(result->equals(NEW(NumVal)(8)) == true);
    }

    SECTION("printExp") {
        PTR(LetExpr) let1 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)));
        std::stringstream ss;
        let1->printExp(ss);
        CHECK(ss.str() == "(_let x=5 _in (x+2))");
    }

    SECTION("pretty_print") {
        PTR(LetExpr) let1 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)));
        std::stringstream ss;
        let1->pretty_print(ss);
        CHECK(ss.str() == "_let x = 5\n_in  x + 2");
    }
}

TEST_CASE("Parse valid numbers") {
    CHECK(parse_str("42")->equals(NEW(NumExpr)(42)));
    CHECK(parse_str("0")->equals(NEW(NumExpr)(0)));
    CHECK(parse_str("123456")->equals(NEW(NumExpr)(123456)));
}

TEST_CASE("Parse valid variables") {
    CHECK(parse_str("x")->equals(NEW(VarExpr)("x")));
    CHECK(parse_str("abc")->equals(NEW(VarExpr)("abc")));
}

TEST_CASE("Parse simple addition") {
    CHECK(parse_str("3+5")->equals(NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(5))));
    CHECK(parse_str("10+20")->equals(NEW(AddExpr)(NEW(NumExpr)(10), NEW(NumExpr)(20))));
}

TEST_CASE("Parse simple multiplication") {
    CHECK(parse_str("4*2")->equals(NEW(MultExpr)(NEW(NumExpr)(4), NEW(NumExpr)(2))));
    CHECK(parse_str("7*8")->equals(NEW(MultExpr)(NEW(NumExpr)(7), NEW(NumExpr)(8))));
}

TEST_CASE("Parse combined addition and multiplication") {
    CHECK(parse_str("3+4*5")->equals(
        NEW(AddExpr)(NEW(NumExpr)(3), NEW(MultExpr)(NEW(NumExpr)(4), NEW(NumExpr)(5)))
    ));
    CHECK(parse_str("2*3+4")->equals(
        NEW(AddExpr)(NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3)), NEW(NumExpr)(4))
    ));
}

TEST_CASE("Parse negative numbers") {
    CHECK(parse_str("-5")->equals(NEW(NumExpr)(-5)));
    CHECK(parse_str("-10")->equals(NEW(NumExpr)(-10)));
}

TEST_CASE("Parse parentheses") {
    CHECK(parse_str("(3+4)")->equals(NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4))));
    CHECK(parse_str("(2*3)")->equals(NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3))));
}

TEST_CASE("Parse nested parentheses") {
    CHECK(parse_str("(1+(2*3))")->equals(
        NEW(AddExpr)(NEW(NumExpr)(1), NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3)))
    ));
    CHECK(parse_str("((1+2)*3)")->equals(
        NEW(MultExpr)(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(NumExpr)(3))
    ));
}


TEST_CASE("Parse invalid input") {
    CHECK_THROWS_WITH( parse_str("*3"), "invalid input" );
    CHECK_THROWS_WITH( parse_str("4**5"), "invalid input" );
    CHECK_THROWS_WITH( parse_str("(3+4"), "invalid input" );
    CHECK_THROWS_WITH( parse_str(")3+4("), "invalid input" );
    CHECK_THROWS_WITH( parse_str("( )"), "invalid input" );
}
