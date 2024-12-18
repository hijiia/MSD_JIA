package assign01;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Represents a grayscale (black and white) image as a 2D array of "pixel" brightnesses
 * 255 is "white" 127 is "gray" 0 is "black" with intermediate values in between
 * Author: Ben Jones and Jia Gao
 */
public class GrayscaleImage {
    private double[][] imageData;
    public GrayscaleImage(double[][] data){
        if(data.length == 0 || data[0].length == 0){
            throw new IllegalArgumentException("Image is empty");
        }
        imageData = new double[data.length][data[0].length];
        for(var row = 0; row < imageData.length; row++){
            if(data[row].length != imageData[row].length){
                throw new IllegalArgumentException("All rows must have the same length");
            }
            for(var col = 0; col < imageData[row].length; col++){
                imageData[row][col] = data[row][col];
            }
        }
    }

    public GrayscaleImage(URL url) throws IOException {
        var inputImage = ImageIO.read(url);
        var grayImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d= grayImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, null);
        g2d.dispose();
        imageData = new double[grayImage.getHeight()][grayImage.getWidth()];


        var grayRaster = grayImage.getRaster();
        for(var row = 0; row < imageData.length; row++){
            for(var col = 0; col < imageData[0].length; col++){
                //getSample parameters are x (our column) and y (our row), so they're "backwards"
                imageData[row][col] = grayRaster.getSampleDouble(col, row, 0);
            }
        }
    }

    public void savePNG(File filename) throws IOException {
        var outputImage = new BufferedImage(imageData[0].length, imageData.length, BufferedImage.TYPE_BYTE_GRAY);
        var raster = outputImage.getRaster();
        for(var row = 0; row < imageData.length; row++){
            for(var col = 0; col < imageData[0].length; col++){
                raster.setSample(col, row, 0, imageData[row][col]);
            }
        }
        ImageIO.write(outputImage, "png", filename);
    }

    public double getPixel (int x, int y) {
        if (x < 0 || x >= imageData[0].length || y < 0 || y >= imageData.length) {
            throw new IllegalArgumentException("Pixel coordinates are out of bounds.");
        }
        return imageData[y][x];
    }



    @Override
    public boolean equals(Object other) {
        if (!(other instanceof GrayscaleImage)) {
            return false;
        }

        GrayscaleImage otherImage = (GrayscaleImage) other;

        if (imageData.length != otherImage.imageData.length || imageData[0].length != otherImage.imageData[0].length) {
            return false;
        }

        for (int row = 0; row < imageData.length; row++) {
            for (int col = 0; col < imageData[0].length; col++) {
                if (imageData[row][col] != otherImage.imageData[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }


    public double averageBrightness() {
        double sum = 0;
        int pixelCount = 0;
        for (double[] row : imageData) {
            for (double value : row) {
                sum += value;
                pixelCount++;
            }
        }
        return sum / pixelCount;
    }


    public GrayscaleImage normalized() {
        double currentAverage = averageBrightness();
        double scaleFactor = 127 / currentAverage;
        double[][] normalizedData = new double[imageData.length][imageData[0].length];

        for (int row = 0; row < imageData.length; row++) {
            for (int col = 0; col < imageData[0].length; col++) {
                normalizedData[row][col] = imageData[row][col] * scaleFactor;
            }
        }
        return new GrayscaleImage(normalizedData);
    }


    public GrayscaleImage mirrored() {
        double[][] mirroredData = new double[imageData.length][imageData[0].length];

        for (int row = 0; row < imageData.length; row++) {
            for (int col = 0; col < imageData[0].length; col++) {
                mirroredData[row][col] = imageData[row][imageData[0].length - 1 - col];
            }
        }
        return new GrayscaleImage(mirroredData);
    }


    public GrayscaleImage cropped(int startRow, int startCol, int width, int height) {
        if (startRow < 0 || startCol < 0 || startRow + height > imageData.length || startCol + width > imageData[0].length) {
            throw new IllegalArgumentException("Crop rectangle is out of bounds.");
        }

        double[][] croppedData = new double[height][width];
        for (int row = 0; row < height; row++) {
            System.arraycopy(imageData[startRow + row], startCol, croppedData[row], 0, width);
        }
        return new GrayscaleImage(croppedData);
    }

    public GrayscaleImage squarified() {
        int height = imageData.length;
        int width = imageData[0].length;

        if (height == width) {
            return new GrayscaleImage(imageData); // Already square
        }

        if (width > height) {
            int cropStart = (width - height) / 2;
            return cropped(0, cropStart, height, height);
        } else {
            int cropStart = (height - width) / 2;
            return cropped(cropStart, 0, width, width);
        }
    }


}