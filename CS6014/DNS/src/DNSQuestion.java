import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class DNSQuestion {
    private String[] domainName;
    private int type;
    private int recordClass;

    // Constructor
    public DNSQuestion(String[] domainName, int type, int recordClass) {
        this.domainName = domainName;
        this.type = type;
        this.recordClass = recordClass;
    }

    // Decodes a question from the input stream
    public static DNSQuestion decodeQuestion(InputStream input, DNSMessage message) throws IOException {
        String[] domainName = message.readDomainName(input);  // Read the domain name using DNSMessage's method
        DataInputStream dataInput = new DataInputStream(input);
        int type = dataInput.readUnsignedShort();  // Read the type of the question (e.g., A, MX)
        int recordClass = dataInput.readUnsignedShort();  // Read the class (usually IN)
        return new DNSQuestion(domainName, type, recordClass);
    }

    // Writes the question bytes to the output stream
    public void writeBytes(ByteArrayOutputStream output, HashMap<String, Integer> domainNameLocations) throws IOException {
        DNSMessage.writeDomainName(output, domainNameLocations, domainName);  // Write the domain name using DNSMessage's method
        DataOutputStream dataOutput = new DataOutputStream(output);
        dataOutput.writeShort(type);  // Write the type of the question
        dataOutput.writeShort(recordClass);  // Write the class of the question
    }

    // Converts the DNSQuestion to a human-readable string
    @Override
    public String toString() {
        return "DNSQuestion{" +
                "domainName=" + String.join(".", domainName) +
                ", type=" + type +
                ", recordClass=" + recordClass +
                '}';
    }

    // Checks if two DNSQuestions are equal based on their domainName, type, and recordClass
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DNSQuestion that = (DNSQuestion) obj;
        return type == that.type &&
                recordClass == that.recordClass &&
                Objects.equals(String.join(".", domainName), String.join(".", that.domainName));
    }

    // Generates a hashCode for the DNSQuestion, needed for usage as a key in a HashMap
    @Override
    public int hashCode() {
        return Objects.hash(String.join(".", domainName), type, recordClass);
    }

    // Getters for domainName, type, and recordClass
    public String[] getDomainName() {
        return domainName;
    }

    public int getType() {
        return type;
    }

    public int getRecordClass() {
        return recordClass;
    }
}