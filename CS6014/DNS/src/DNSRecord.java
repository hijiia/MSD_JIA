import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DNSRecord {
    private String[] domainName;
    private int type;
    private int recordClass;
    private int ttl;
    private byte[] rdata;
    private Date createdAt;

    // Constructor to initialize the DNSRecord
    public DNSRecord(String[] domainName, int type, int recordClass, int ttl, byte[] rdata) {
        this.domainName = domainName;
        this.type = type;
        this.recordClass = recordClass;
        this.ttl = ttl;
        this.rdata = rdata;
        this.createdAt = new Date(); // Set creation time as the current time
    }

    // Decodes a DNS record from the input stream
    public static DNSRecord decodeRecord(InputStream input, DNSMessage message) throws IOException {
        String[] domainName = message.readDomainName(input);  // Read the domain name using DNSMessage's method
        DataInputStream dataInput = new DataInputStream(input);
        int type = dataInput.readUnsignedShort();  // Read the type of the record (e.g., A, MX)
        int recordClass = dataInput.readUnsignedShort();  // Read the class (usually IN)
        int ttl = dataInput.readInt();  // Read the TTL (Time to Live)
        int rdLength = dataInput.readUnsignedShort();  // Read the length of the record data (rdata)
        byte[] rdata = new byte[rdLength];  // Allocate a byte array for rdata
        dataInput.readFully(rdata);  // Read the rdata into the byte array
        return new DNSRecord(domainName, type, recordClass, ttl, rdata);  // Return a new DNSRecord object
    }

    // Writes the record bytes to the output stream
    public void writeBytes(ByteArrayOutputStream output, HashMap<String, Integer> domainLocations) throws IOException {
        DNSMessage.writeDomainName(output, domainLocations, domainName);  // Write the domain name using DNSMessage's method
        DataOutputStream dataOutput = new DataOutputStream(output);
        dataOutput.writeShort(type);  // Write the type of the record
        dataOutput.writeShort(recordClass);  // Write the class of the record
        dataOutput.writeInt(ttl);  // Write the TTL of the record
        dataOutput.writeShort(rdata.length);  // Write the length of the rdata
        dataOutput.write(rdata);  // Write the rdata itself
    }

    // Converts the DNSRecord to a human-readable string
    @Override
    public String toString() {
        return "DNSRecord{" +
                "domainName=" + String.join(".", domainName) +
                ", type=" + type +
                ", recordClass=" + recordClass +
                ", ttl=" + ttl +
                ", createdAt=" + createdAt +
                '}';
    }

    // Checks if the DNS record is expired based on its TTL and creation date
    public boolean isExpired() {
        Calendar calendar = Calendar.getInstance();  // Create a calendar instance
        calendar.setTime(createdAt);  // Set the time to the record's creation time
        calendar.add(Calendar.SECOND, ttl);  // Add TTL (in seconds) to the creation time
        return calendar.getTime().before(new Date());  // Check if the record has expired
    }

    // Getters for DNSRecord fields
    public String[] getDomainName() {
        return domainName;
    }

    public int getType() {
        return type;
    }

    public int getRecordClass() {
        return recordClass;
    }

    public int getTtl() {
        return ttl;
    }

    public byte[] getRdata() {
        return rdata;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}