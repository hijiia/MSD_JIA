
public class Main {
    public static void main(String[] args) {


        Fraction frac1 = new Fraction(1, 2);  // 1/2
        Fraction frac2 = new Fraction(3, 4);  // 3/4

        Fraction sum = frac1.plus(frac2);
        Fraction difference = frac1.minus(frac2);
        Fraction product = frac1.times(frac2);
        Fraction quotient = frac1.dividedBy(frac2);
        Fraction reciprocal1 = frac1.reciprocal();
        Fraction reciprocal2 = frac2.reciprocal();


        System.out.println("Sum: " + sum);
        System.out.println("Difference: " + difference);
        System.out.println("Product: " + product);
        System.out.println("Quotient: " + quotient);
        System.out.println("Reciprocal of Fraction 1: " + reciprocal1);
        System.out.println("Reciprocal of Fraction 2: " + reciprocal2);
        System.out.println("Fraction 1 as double: " + frac1.toDouble());
        System.out.println("Fraction 2 as double: " + frac2.toDouble());



    }
}




