import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RainData {
    private ArrayList<String> fileData;
    private ArrayList<Float> rainData;
    private ArrayList<Integer> yearData;
    private Scanner filereader;

    public RainData(String filename) throws FileNotFoundException {
        File file = new File(filename);
        filereader = new Scanner (file);
        fileData = new ArrayList<>();
        rainData = new ArrayList<>();
        yearData = new ArrayList<>();
    }

    public void close() throws IOException {
        filereader.close();
    }

    public void readfile(){
        String word = null;
        Float rain = null;
        int year = 0;
        while (filereader.hasNext()) {
            word = filereader.next();
            year = filereader.nextInt();
            rain = filereader.nextFloat();
            fileData.add(word);
            yearData.add(year);
            rainData.add(rain);
        }
    }

    public float AveRainData(){
        float sum = 0;
        for (Float rainDatum : rainData) {
            sum += rainDatum;
        }
        return sum / rainData.size();
    }

    public float AveRainDataMon(String month){
        float sum = 0;
        int count = 0;
        for (int i = 0; i < yearData.size(); i++){
            if (fileData.get(i).equals(month)){
                sum += rainData.get(i);
                count++;
            }
        }
        return sum / count;
    }
}