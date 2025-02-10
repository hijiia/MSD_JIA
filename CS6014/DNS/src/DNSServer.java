import java.io.*;
import java.net.*;

public class DNSServer {
    private static final int PORT = 8053;
    private static final String FORWARDING_DNS = "8.8.8.8";

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(PORT);
        byte[] buffer = new byte[512];

        while (true) {
            DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(requestPacket);

            byte[] requestData = requestPacket.getData();
            DNSMessage requestMessage = DNSMessage.decodeMessage(requestData);

            DNSRecord answer = DNSCache.lookup(requestMessage.questions[0]);
            if (answer == null) {
                answer = forwardToGoogle(requestData);
                DNSCache.store(requestMessage.questions[0], answer);
            }

            byte[] responseData = requestMessage.toBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, requestPacket.getAddress(), requestPacket.getPort());
            socket.send(responsePacket);
        }
    }

    private static DNSRecord forwardToGoogle(byte[] requestData) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress googleDNS = InetAddress.getByName(FORWARDING_DNS);
        DatagramPacket packet = new DatagramPacket(requestData, requestData.length, googleDNS, 53);
        socket.send(packet);
        socket.receive(packet);
        return DNSRecord.decodeRecord(new ByteArrayInputStream(packet.getData()), null);
    }
}