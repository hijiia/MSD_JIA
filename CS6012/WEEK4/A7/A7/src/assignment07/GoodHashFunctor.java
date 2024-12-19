package assignment07;

public class GoodHashFunctor implements HashFunctor {
    @Override
    public int hash(String item) {
        long hash = 5381;
        for (char c : item.toCharArray()) {
            hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
        }
        return (int) hash;
    }
}
