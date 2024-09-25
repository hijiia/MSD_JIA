//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Fraction {
    private long nominator;
    private long denominator;
    private long GCD () {
        long gcd = this.nominator;
        long remainder = this.denominator;
        while (remainder != 0) {
            long temp = remainder;
            remainder = gcd % remainder;
            gcd = temp;
        }
        return gcd;
    }
    private void reduce() {
        long gcd = GCD();
        this.nominator /= gcd;
        this.denominator /= gcd;
        if (this.nominator == 0) {
            System.out.println("invalid number");
        }
        if (this.denominator < 0) {
            this.nominator = -this.nominator;
            this.denominator = -this.denominator;
        }
    }
    // Default
    public Fraction() {
        this.nominator = 0;
        this.denominator = 1;
    }

    public Fraction(long n, long d) {
        this.nominator = n;
        this.denominator = d;
        reduce();
        if (d == 0) {
            System.out.println("invalid number");
        }
        if (d < 0) {
            this.nominator = -n;
            this.denominator = -d;
        }

    }


    Fraction plus(Fraction rhs) {
        long newnumerator = this.nominator * rhs.denominator + rhs.nominator * this.denominator;
        long newdenominator = this.denominator * rhs.denominator;
        return new Fraction(newnumerator, newdenominator);
    }

    Fraction minus(Fraction rhs) {
        long newnumerator = this.nominator * rhs.denominator - rhs.nominator * this.denominator;
        long newdenominator = this.denominator * rhs.denominator;
        return new Fraction(newnumerator, newdenominator);

    }

    Fraction times(Fraction rhs) {
        long newnumerator = this.nominator * rhs.nominator;
        long newdenominator = this.denominator * rhs.denominator;
        return new Fraction(newnumerator, newdenominator);
    }

    Fraction dividedBy(Fraction rhs) {
        if (this.nominator == 0) {
            System.out.println("can not divide by zero");
        }
        long newnumerator = this.nominator * rhs.denominator;
        long newdenominator = this.denominator * rhs.nominator;
        return new Fraction(newnumerator, newdenominator);
    }

    Fraction reciprocal() {
        if (this.nominator == 0) {
            System.out.println("can not divide by zero");
        }
        long newnumerator = this.denominator;
        long newdenominator = this.nominator;
        return new Fraction(newnumerator, newdenominator);

    }

    public String toString() {
        return nominator + "/" + denominator;
    }

    public double toDouble() {
        return (double) nominator / denominator;
    }


}



