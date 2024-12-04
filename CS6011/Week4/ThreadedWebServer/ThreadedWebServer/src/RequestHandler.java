import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private final Socket clientsocket;
    public RequestHandler(Socket socket) {
        this.clientsocket = socket;
    }
    public void run() {
        try(InputStream in = clientsocket.getInputStream();
        OutputStream out = clientsocket.getOutputStream();
        ) {
            File file = new File("public/capybara0.png");
            FileInputStream fileInputStream = new FileInputStream(file);
            int fileSize = (int) file.length();
            for (int i = 0; i < fileSize; i++) {
                out.write(fileInputStream.read());
                out.flush();
                Thread.sleep(10);  // Simulate delay to test concurrency
            }

            fileInputStream.close();
            clientsocket.close();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        }
    }
}