import java.io.*;
import java.util.HashMap;
import java.util.Arrays;

public class DNSMessage {
    DNSHeader header;
    DNSQuestion[] questions;
    DNSRecord[] answers;
    DNSRecord[] authorityRecords;  // Not used, but included for completeness
    DNSRecord[] additionalRecords;  // Almost ignored, but included for completeness
    byte[] originalMessage;

    // Constructor
    public DNSMessage(DNSHeader header, DNSQuestion[] questions, DNSRecord[] answers, byte[] originalMessage) {
        this.header = header;
        this.questions = questions;
        this.answers = answers;
        this.authorityRecords = new DNSRecord[0];  // Empty array as it’s ignored
        this.additionalRecords = new DNSRecord[0];  // Empty array as it's almost ignored
        this.originalMessage = originalMessage;
    }

    // Decodes the DNS message
    public static DNSMessage decodeMessage(byte[] bytes) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        DNSHeader header = DNSHeader.decodeHeader(input);
        DNSQuestion[] questions = new DNSQuestion[header.getQuestionCount()];
        for (int i = 0; i < questions.length; i++) {
            questions[i] = DNSQuestion.decodeQuestion(input, null);
        }
        // We return a new DNSMessage object with decoded header, questions, and no answers
        return new DNSMessage(header, questions, new DNSRecord[0], bytes);
    }

    // Converts DNSMessage to bytes to send in a packet
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        header.writeBytes(output);
        HashMap<String, Integer> domainLocations = new HashMap<>();
        for (DNSQuestion question : questions) {
            question.writeBytes(output, domainLocations);
        }
        return output.toByteArray();
    }

    // Reads a domain name from the input stream starting at the current position
    public String[] readDomainName(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int length;
        while ((length = input.read()) != 0) {  // Reading each segment of the domain name
            if (length < 0) {
                throw new IOException("Error reading domain name, unexpected end of stream");
            }
            byte[] domainSegment = new byte[length];
            input.read(domainSegment);
            output.write(domainSegment);
            output.write('.');
        }
        String domainName = output.toString("UTF-8").replaceAll("\\.$", "");  // Removing the trailing dot
        return domainName.split("\\.");  // Split by dot
    }

    // Reads a domain name from a specific byte position (used in case of compression)
    public String[] readDomainName(int firstByte) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(originalMessage, firstByte, originalMessage.length - firstByte);
        return readDomainName(byteStream);
    }

    // Builds a response message based on the original request and the provided answers
    public static DNSMessage buildResponse(DNSMessage request, DNSRecord[] answers) throws IOException {
        DNSHeader responseHeader = DNSHeader.buildHeaderForResponse(request.header, answers);
        return new DNSMessage(responseHeader, request.questions, answers, request.originalMessage);
    }

    // Writes the domain name into the output stream, using compression if necessary
    public static void writeDomainName(ByteArrayOutputStream output, HashMap<String, Integer> domainLocations, String[] domainPieces) throws IOException {
        String domain = joinDomainName(domainPieces);
        if (!domainLocations.containsKey(domain)) {
            domainLocations.put(domain, output.size());  // Add the domain to the locations map
            for (String piece : domainPieces) {
                output.write(piece.length());  // Write the length of each piece
                output.write(piece.getBytes());  // Write the bytes of each piece
            }
            output.write(0);  // Null byte to indicate end of domain name
        } else {
            int pointer = domainLocations.get(domain);  // Retrieve the pointer for compression
            output.write(0xC0);  // Compression pointer flag
            output.write((pointer >> 8) & 0xFF);  // High byte of the pointer
            output.write(pointer & 0xFF);  // Low byte of the pointer
        }
    }

    // domain name pieces into a single string
    public static String joinDomainName(String[] pieces) {
        return String.join(".", pieces);
    }


    public String toString() {
        return "DNSMessage{" +
                "header=" + header +
                ", questions=" + Arrays.toString(questions) +
                ", answers=" + Arrays.toString(answers) +
                ", authorityRecords=" + Arrays.toString(authorityRecords) +
                ", additionalRecords=" + Arrays.toString(additionalRecords) +
                '}';
    }

    public DNSHeader getHeader() {
        return this.header;
    }
}