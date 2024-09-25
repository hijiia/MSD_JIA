import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        //Q2
        int[] numbers = new int[10];
        Random randNum = new Random();
        int sum = 0;
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = randNum.nextInt(100);
            sum += numbers[i];
        }
        System.out.println("Random Numbers = " + Arrays.toString(numbers));
        System.out.println("Sum = " + sum);
        //Q3
        Scanner userInputname = new Scanner(System.in);
        System.out.println("Enter username");
        String username = userInputname.nextLine();
        Scanner userInputage = new Scanner(System.in);
        System.out.println("Enter age");
        int userage = userInputname.nextInt();
        if ( userage >= 18 ) {
            System.out.println("You can vote");
        }
        else{
            System.out.println("You can not vote");
        }
        if (userage >= 96) {
            System.out.println ("Greatest Generation") ;
          } else if (userage >= 78) {
            System.out.println ("Boomer");
        } else if (userage >= 58) {
            System.out.println ("Gen X");
        } else if (userage >= 27) {
            System.out.println ("Millennial");
        } else {
            System.out.println ("iGen");
        }





    }


    }


