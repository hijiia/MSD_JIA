import java.io.*;

public class DNSHeader {
    private int id;               // 16 bits, transaction ID
    private int flags;            // 16 bits, DNS flags
    private int questionCount;    // 16 bits, number of questions
    private int answerCount;      // 16 bits, number of answers
    private int authorityCount;   // 16 bits, number of authority records
    private int additionalCount;  // 16 bits, number of additional records

    // Constructor
    public DNSHeader(int id, int flags, int questionCount, int answerCount, int authorityCount, int additionalCount) {
        this.id = id;
        this.flags = flags;
        this.questionCount = questionCount;
        this.answerCount = answerCount;
        this.authorityCount = authorityCount;
        this.additionalCount = additionalCount;
    }

    // Decode the DNS header from an InputStream
    public static DNSHeader decodeHeader(InputStream input) throws IOException {
        DataInputStream dataInput = new DataInputStream(input);

        int id = dataInput.readUnsignedShort();          // Transaction ID
        int flags = dataInput.readUnsignedShort();       // Flags
        int questionCount = dataInput.readUnsignedShort();  // Question Count
        int answerCount = dataInput.readUnsignedShort();    // Answer Count
        int authorityCount = dataInput.readUnsignedShort(); // Authority Record Count
        int additionalCount = dataInput.readUnsignedShort(); // Additional Record Count

        return new DNSHeader(id, flags, questionCount, answerCount, authorityCount, additionalCount);
    }

    public static DNSHeader buildHeaderForResponse(DNSHeader request, DNSRecord[] response) {
        // Copy transaction ID and question count from the request
        int id = request.getId();  // Directly access the transaction ID from the request
        int flags = 0x8000;  // Set the response flag in the header (0x8000 is the "Response" flag in DNS)
        int questionCount = request.getQuestionCount();
        int answerCount = response.length;
        int authorityCount = 0;
        int additionalCount = 0;

        return new DNSHeader(id, flags, questionCount, answerCount, authorityCount, additionalCount);
    }

    // Write the DNS header to an OutputStream
    public void writeBytes(OutputStream output) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(output);

        dataOutput.writeShort(id);               // Transaction ID
        dataOutput.writeShort(flags);            // Flags
        dataOutput.writeShort(questionCount);    // Question Count
        dataOutput.writeShort(answerCount);      // Answer Count
        dataOutput.writeShort(authorityCount);   // Authority Record Count
        dataOutput.writeShort(additionalCount);  // Additional Record Count
    }

    // Convert the DNSHeader object to a human-readable string
    @Override
    public String toString() {
        return "DNSHeader{" +
                "id=" + id +
                ", flags=" + Integer.toHexString(flags) +
                ", questionCount=" + questionCount +
                ", answerCount=" + answerCount +
                ", authorityCount=" + authorityCount +
                ", additionalCount=" + additionalCount +
                '}';
    }

    // Getters for DNSHeader fields
    public int getId() {
        return id;
    }

    public int getFlags() {
        return flags;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public int getAuthorityCount() {
        return authorityCount;
    }

    public int getAdditionalCount() {
        return additionalCount;
    }
}