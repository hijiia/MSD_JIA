#include "catch.h"
#include "expr.h"
#include "val.h"
#include "env.h"
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
    PTR(Val) result = n->interp(Env::empty);
    CHECK(result->equals(NEW(NumVal)(5)) == true);
}

TEST_CASE("Add interp") {
    PTR(AddExpr) a = NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(2));
    PTR(Val) result = a->interp(Env::empty);
    CHECK(result->equals(NEW(NumVal)(5)) == true);
}

TEST_CASE("Mult interp") {
    PTR(MultExpr) m = NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(2));
    PTR(Val) result = m->interp(Env::empty);
    CHECK(result->equals(NEW(NumVal)(6)) == true);
}

TEST_CASE("VarExpr interp") {
    CHECK_THROWS_WITH((NEW(VarExpr)("x"))->interp(Env::empty), "free variable: x");
}


TEST_CASE("Environment variable lookup") {
    // Test variable lookup in extended environment
    PTR(Val) val5 = NEW(NumVal)(5);
    PTR(Env) env = NEW(ExtendedEnv)("x", val5, Env::empty);
    
    PTR(Expr) expr = NEW(VarExpr)("x");
    CHECK(expr->interp(env)->equals(val5));
    
    // Test variable lookup for non-existent variable
    CHECK_THROWS_WITH((NEW(VarExpr)("y"))->interp(env), "free variable: y");
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
        PTR(Val) result1 = let1->interp(Env::empty);
        CHECK(result1->equals(NEW(NumVal)(7)) == true);

        PTR(LetExpr) let2 = NEW(LetExpr)("x", NEW(NumExpr)(3), NEW(MultExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(4)));
        PTR(Val) result2 = let2->interp(Env::empty);
        CHECK(result2->equals(NEW(NumVal)(12)) == true);
    }

    SECTION("nested let expressions") {
        // let x = 5 in (let y = 3 in x + y)
        PTR(LetExpr) nestedLet = NEW(LetExpr)(
            "x", NEW(NumExpr)(5),
            NEW(LetExpr)(
                "y", NEW(NumExpr)(3),
                NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))
            )
        );
        
        PTR(Val) result = nestedLet->interp(Env::empty);
        CHECK(result->equals(NEW(NumVal)(8)) == true);
    }

    SECTION("variable shadowing") {
        // let x = 5 in (let x = 10 in x) + x
        PTR(LetExpr) shadowingLet = NEW(LetExpr)(
            "x", NEW(NumExpr)(5),
            NEW(AddExpr)(
                NEW(LetExpr)("x", NEW(NumExpr)(10), NEW(VarExpr)("x")),
                NEW(VarExpr)("x")
            )
        );
        
        PTR(Val) result = shadowingLet->interp(Env::empty);
        CHECK(result->equals(NEW(NumVal)(15)) == true);
    }
}

TEST_CASE("FunExpr and CallExpr") {
    SECTION("simple function call") {
        // (_fun (x) x+1)(5) should evaluate to 6
        PTR(CallExpr) call = NEW(CallExpr)(
            NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))),
            NEW(NumExpr)(5)
        );
        
        PTR(Val) result = call->interp(Env::empty);
        CHECK(result->equals(NEW(NumVal)(6)) == true);
    }
    
    SECTION("function with environment capture") {
        // let y = 10 in (_fun (x) x+y)(5) should evaluate to 15
        PTR(LetExpr) letWithFunCall = NEW(LetExpr)(
            "y", NEW(NumExpr)(10),
            NEW(CallExpr)(
                NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))),
                NEW(NumExpr)(5)
            )
        );
        
        PTR(Val) result = letWithFunCall->interp(Env::empty);
        CHECK(result->equals(NEW(NumVal)(15)) == true);
    }
    
    SECTION("higher order function") {
        // let make_adder = (_fun (n) (_fun (x) x+n)) in
        // let add5 = make_adder(5) in
        // add5(10)  should evaluate to 15
        PTR(LetExpr) higherOrderFun = NEW(LetExpr)(
            "make_adder",
            NEW(FunExpr)("n",
                NEW(FunExpr)("x",
                    NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("n"))
                )
            ),
            NEW(LetExpr)(
                "add5",
                NEW(CallExpr)(NEW(VarExpr)("make_adder"), NEW(NumExpr)(5)),
                NEW(CallExpr)(NEW(VarExpr)("add5"), NEW(NumExpr)(10))
            )
        );
        
        PTR(Val) result = higherOrderFun->interp(Env::empty);
        CHECK(result->equals(NEW(NumVal)(15)) == true);
    }
}

TEST_CASE("Parse let expressions") {
    CHECK(parse_str("_let x = 5 _in x + 2")->equals(
        NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)))
    ));
}


