import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SimpleHttpServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server is listening on port 8080");

        while (true) {
            try (Socket socket = serverSocket.accept()) {
                handleClientRequest(socket);
            }
        }
    }

    private static void handleClientRequest(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();

        Scanner scanner = new Scanner(input);
        String requestLine = scanner.nextLine();
        System.out.println("Request: " + requestLine);

        String[] requestParts = requestLine.split(" ");
        String filePath = requestParts[1];

        if (filePath.equals("/")) {
            filePath = "/index.html";  // 如果请求"/"，发送index.html
        }

        File file = new File("resources" + filePath);
        if (file.exists() && !file.isDirectory()) {
            sendResponse(output, "HTTP/1.1 200 OK", file);
        } else {
            send404(output);
        }
    }

    private static void sendResponse(OutputStream output, String status, File file) throws IOException {
        PrintWriter out = new PrintWriter(output, true);
        out.println(status);
        out.println("Content-Type: text/html");
        out.println("Content-Length: " + file.length());
        out.println();
        out.flush();

        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.transferTo(output);
        fileInputStream.close();
    }


    private static void send404(OutputStream output) throws IOException {
        PrintWriter out = new PrintWriter(output, true);
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<h1>404 - File Not Found</h1>");
        out.flush();
    }
}