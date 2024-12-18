import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost",8080);

        Scanner in = new Scanner(System.in);
        String message = "Hello Networld, I'm the client";

        socket.getOutputStream().write((message+"\n").getBytes());

        InputStream input = socket.getInputStream();
        Scanner scanner = new Scanner(input);
        while (socket.isConnected()){
            System.out.println(scanner.nextLine());
        }
    }
}