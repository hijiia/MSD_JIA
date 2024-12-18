import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Request{ // get user input and filename to open
    private final String path;

    public Request(InputStream inputStream) throws IOException {
        Scanner scanner = new Scanner(inputStream);
        if (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            String[] split = message.split(" ");
            this.path = split[1];
        }else{
            throw new IOException("Invalid request");
        }
    }

    public String getPath(){
        return this.path;
    }
}