#include "catch.h"
#include "expr.h"
#include "parse.hpp"

TEST_CASE("Test of set experiment") { 
    CHECK( (new VarExpr("x"))->equals(new VarExpr("x")) == true );
    CHECK( (new VarExpr("x"))->equals(new VarExpr("y")) == false );
    CHECK( (new NumExpr(1))->equals(new VarExpr("x")) == false );
    CHECK( (new NumExpr(3))->equals(new NumExpr(3)) == true );
}

TEST_CASE("Test of add num experiment"){
    CHECK( (new AddExpr(new NumExpr(2),new NumExpr(3)))->equals(new AddExpr(new NumExpr(2),new NumExpr(3)))==true );
    CHECK( (new AddExpr(new NumExpr(2),new NumExpr(3)))->equals(new AddExpr(new NumExpr(3),new NumExpr(2)))==false );
}

TEST_CASE("Test of multi num experiment"){
    CHECK( (new MultExpr(new NumExpr(2),new NumExpr(3)))->equals(new MultExpr(new NumExpr(2),new NumExpr(3)))==true );
    CHECK( (new MultExpr(new NumExpr(2),new NumExpr(3)))->equals(new MultExpr(new NumExpr(3),new NumExpr(2)))==false );
    CHECK( (new MultExpr(new NumExpr(2),new NumExpr(3)))->equals(new AddExpr(new NumExpr(3),new NumExpr(2)))==false );
    CHECK( (new MultExpr(new VarExpr("x"),new VarExpr("y")))->equals(new AddExpr(new NumExpr(3),new NumExpr(2)))==false );    
}

TEST_CASE("for has_variable()"){
      CHECK( (new AddExpr(new VarExpr("x"), new NumExpr(1)))->has_variable() == true );
      CHECK( (new MultExpr(new NumExpr(2), new NumExpr(1)))->has_variable() == false );
}

TEST_CASE("for replace function"){
    CHECK( (new AddExpr(new VarExpr("x"), new NumExpr(7)))
       ->subst("x", new VarExpr("y"))
       ->equals(new AddExpr(new VarExpr("y"), new NumExpr(7))) );
    CHECK( (new VarExpr("x"))
       ->subst("x", new AddExpr(new VarExpr("y"),new NumExpr(7)))
       ->equals(new AddExpr(new VarExpr("y"),new NumExpr(7))) );
    CHECK( (new MultExpr(new VarExpr("x"), new VarExpr("y")))   
       ->subst("x", new AddExpr(new NumExpr(10),new NumExpr(7)))        
       ->equals(new MultExpr(new AddExpr(new NumExpr(10),new NumExpr(7)), new VarExpr("y"))) );
}

TEST_CASE("for print expression"){
    CHECK ( (new MultExpr(new NumExpr(1), new AddExpr(new NumExpr(2), new NumExpr(3))))->pretty_print() ==  "1 * (2 + 3)" );
    CHECK ( (new MultExpr(new MultExpr(new NumExpr(8), new NumExpr(1)), new VarExpr("y")))->pretty_print() ==  "(8 * 1) * y" );
    CHECK ( (new MultExpr(new AddExpr(new NumExpr(3), new NumExpr(5)), new MultExpr(new NumExpr(6), new NumExpr(1))))->pretty_print() ==  "3 + 5 * 6 * 1" );
    CHECK ( (new MultExpr(new MultExpr(new NumExpr(7), new NumExpr(7)), new AddExpr(new NumExpr(9), new NumExpr(2))) )->pretty_print() ==  "(7 * 7) * (9 + 2)" );
}

TEST_CASE("for let binding"){
    CHECK ( (new LetExpr("x", new NumExpr(5), new AddExpr(new LetExpr("y", new NumExpr(3), new AddExpr(new VarExpr("y"), new NumExpr(2))), new VarExpr("x")))) -> pretty_print() == "(_let x=5 _in ((_let y=3 _in (y + 2)) + x))");
}



TEST_CASE("parse"){
  CHECK_THROWS_WITH( parse_str("()"), "invalid input" );
  
  CHECK( parse_str("(1)")->equals(new NumExpr(1)) );
  CHECK( parse_str("(((1)))")->equals(new NumExpr(1)) );
  
  CHECK_THROWS_WITH( parse_str("(1"), "invalid input" );
  
  CHECK( parse_str("1")->equals(new NumExpr(1)) );
  CHECK( parse_str("10")->equals(new NumExpr(10)) );
  CHECK( parse_str("-3")->equals(new NumExpr(-3)) );
  CHECK( parse_str("  \n 5  ")->equals(new NumExpr(5)) );
  CHECK_THROWS_WITH( parse_str("-"), "invalid input" );


  
  CHECK_THROWS_WITH( parse_str(" -   5  "), "invalid input" );
  
  CHECK( parse_str("x")->equals(new VarExpr("x")) );
  CHECK( parse_str("xyz")->equals(new VarExpr("xyz")) );
  CHECK( parse_str("xYz")->equals(new VarExpr("xYz")) );
  CHECK_THROWS_WITH( parse_str("x_z"), "invalid input" );
  
  CHECK( parse_str("x + y")->equals(new AddExpr(new VarExpr("x"), new VarExpr("y"))) );

  CHECK( parse_str("x * y")->equals(new MultExpr(new VarExpr("x"), new VarExpr("y"))) );

  CHECK( parse_str("z * x + y")
        ->equals(new AddExpr(new MultExpr(new VarExpr("z"), new VarExpr("x")),
                         new VarExpr("y"))) );
  
  CHECK( parse_str("z * (x + y)")
        ->equals(new MultExpr(new VarExpr("z"),
                          new AddExpr(new VarExpr("x"), new VarExpr("y"))) ));

}
