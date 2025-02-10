import java.util.HashMap;

public class DNSCache {
    private static HashMap<DNSQuestion, DNSRecord> cache = new HashMap<>();

    public static synchronized DNSRecord lookup(DNSQuestion question) {
        DNSRecord record = cache.get(question);
        if (record == null || record.isExpired()) {
            cache.remove(question);
            return null;
        }
        return record;
    }

    public static synchronized void store(DNSQuestion question, DNSRecord record) {
        cache.put(question, record);
    }
}