import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.FileOutputStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public class Main {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);

        while(true){
            Socket socket = serverSocket.accept();

            try {
               Request request = new Request(socket.getInputStream());
               Response response = new Response(socket.getOutputStream());
                File file = new File("src" + request.getPath());
                if (file.exists()){
                    response.sendFile(request.getPath());
                } else {
                    response.sendErrorResponse(404, "File Not Found");
                }
            }catch (FileNotFoundException e){
               Response response = new Response(socket.getOutputStream());
                response.sendErrorResponse(404, "File Not Found");
            }catch (IOException e){
                Response response = new Response(socket.getOutputStream());
                response.sendErrorResponse(500, "Internal Server Error");
            }
            socket.close();
        }
    }
}