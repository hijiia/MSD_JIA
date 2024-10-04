import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter("rainfall_results.txt"));
        RainData rain = new RainData("rainfall_data.txt");
        rain.readfile();
        float aveRain = rain.AveRainData();
        float JanRain = rain.AveRainDataMon("January");
        float FebRain = rain.AveRainDataMon("February");
        float MarchRain = rain.AveRainDataMon("March");
        float AprilRain = rain.AveRainDataMon("April");
        float MayRain = rain.AveRainDataMon("May");
        float JuneRain = rain.AveRainDataMon("June");
        float JulyRain = rain.AveRainDataMon("July");
        float AugustRain = rain.AveRainDataMon("August");
        float SepRain = rain.AveRainDataMon("September");
        float OctRain = rain.AveRainDataMon("October");
        float NovemberRain = rain.AveRainDataMon("November");
        float DecemberRain = rain.AveRainDataMon("December");
        //The average rainfall amount for January is 6.33 inches.
        String str = "The overall average rainfall amount is " + aveRain + " inches.";
        str += "\nThe average rainfall amount for January is " + JanRain + " inches.";
        str += "\nThe average rainfall amount for February is " + FebRain + " inches.";
        str += "\nThe average rainfall amount for March is " + MarchRain + " inches.";
        str += "\nThe average rainfall amount for April is " + AprilRain + " inches.";
        str += "\nThe average rainfall amount for May is " + MayRain + " inches.";
        str += "\nThe average rainfall amount for June is " + JuneRain + " inches.";
        str += "\nThe average rainfall amount for July is " + JulyRain + " inches.";
        str += "\nThe average rainfall amount for August is " + AugustRain + " inches.";
        str += "\nThe average rainfall amount for September is " + SepRain + " inches.";
        str += "\nThe average rainfall amount for October is " + OctRain + " inches.";
        str += "\nThe average rainfall amount for November is " + NovemberRain + " inches.";
        str += "\nThe average rainfall amount for December is " + DecemberRain + " inches.";
        pw.println(str);
        rain.close();
        pw.close();


    }
}