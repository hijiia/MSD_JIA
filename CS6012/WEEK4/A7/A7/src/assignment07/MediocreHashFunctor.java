package assignment07;

public class MediocreHashFunctor implements HashFunctor {
    @Override
    public int hash(String item) {
        int sum = 0;
        for ( char c : item.toCharArray() ) {
            sum += c;
        }
        return sum * item.charAt(0) * item.charAt(item.length() - 1);
    }
}
