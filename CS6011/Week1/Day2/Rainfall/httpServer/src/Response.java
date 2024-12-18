import java.io.*;

public class Response{
    private OutputStream output;

    public Response(OutputStream outputStream){
        this.output = outputStream;
    }

    public void sendFile(String path) throws IOException {
        File file = new File("src" + path);
        FileInputStream fileInputStream = new FileInputStream(file);

        output.write("HTTP/1.1 200 OK \r\n".getBytes());
        output.write("Content-Type: text/html\r\n".getBytes());
        output.write(("Content-Length: " + file.length() + "\r\n").getBytes());
        output.write("\r\n".getBytes());
        fileInputStream.transferTo(output);
        fileInputStream.close();
        output.flush();
    }

    public void sendErrorResponse(int errorCode, String error) throws IOException {
        String statusMessage = "HTTP/1.1 " + errorCode + " ";
        if (errorCode == 404){
            statusMessage += "Not Found";
        }
        if (errorCode == 500){
            statusMessage += "Server Error";
        }
        output.write((statusMessage+"\r\n").getBytes());
        output.write("Content-Type: text/html\r\n".getBytes());
        output.write(("Content-Length: " + error.length() + "\r\n").getBytes());
        output.write("\r\n".getBytes());
        output.write(("<h1>"+error+"</h1>").getBytes());
        output.flush();
    }
}