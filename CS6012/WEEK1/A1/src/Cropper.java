import assign01.GrayscaleImage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;


public class Cropper {
    public static void main(String[] args)  {
        String url;
        if(args.length > 1){
            url = args[1];
        } else {
            System.out.println("Enter input image URL");
            var scanner = new Scanner(System.in);
            url = scanner.nextLine();
        }


        try {
            var gi = new GrayscaleImage(new URL(url));
            gi.squarified().normalized().mirrored().savePNG(new File("outputImage.png"));
        } catch(IOException ex){
            System.out.println("Failed to download or save file: " + ex.getMessage());
        }
    }
}
