import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
class FractionTest {

    @Test
    public void runATest() {
        // Test 1: Test fraction addition
        Fraction frac1 = new Fraction(1, 2);
        Fraction frac2 = new Fraction(1, 3);
        Fraction expectedSum = new Fraction(5, 6); // 1/2 + 1/3 = 5/6
        Assertions.assertEquals(expectedSum.toString(), frac1.plus(frac2).toString());

        // Test 2: Test fraction subtraction
        Fraction expectedDifference = new Fraction(1, 6); // 1/2 - 1/3 = 1/6
        Assertions.assertEquals(expectedDifference.toString(), frac1.minus(frac2).toString());

        // Test 3: Test fraction multiplication
        Fraction expectedProduct = new Fraction(1, 6); // 1/2 * 1/3 = 1/6
        Assertions.assertEquals(expectedProduct.toString(), frac1.times(frac2).toString());

        // Test 4: Test fraction division
        Fraction expectedQuotient = new Fraction(3, 2); // 1/2 ÷ 1/3 = 3/2
        Assertions.assertEquals(expectedQuotient.toString(), frac1.dividedBy(frac2).toString());

        // Test 5: Test fraction reciprocal
        Fraction frac3 = new Fraction(2, 3);
        Fraction expectedReciprocal = new Fraction(3, 2); // Reciprocal of 2/3 = 3/2
        Assertions.assertEquals(expectedReciprocal.toString(), frac3.reciprocal().toString());

    }
}