import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {

        ArrayList<Fraction> flist = new ArrayList<>();
        Fraction f1 = new Fraction(1, 2);
        flist.add(f1);
        Fraction f2 = new Fraction(1, 3);
        flist.add(f2);
        Fraction f3 = new Fraction(1, 4);
        flist.add(f3);
        Fraction f4 = new Fraction(1, 5);
        flist.add(f4);
        Fraction f5 = new Fraction(1, 6);
        flist.add(f5);
        for (Fraction fractions : flist) {
            System.out.print(fractions.toString() + " ");
        }
        Collections.sort(flist);
        System.out.println();
        for (Fraction fractions : flist) {
            System.out.print(fractions.toString() + " ");
        }
    }


}

