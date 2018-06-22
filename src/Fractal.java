import java.awt.Color;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.commons.math3.complex.Complex;

public class Fractal {
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        int maxIterations = 20;
        int[] colorPalette = new int[maxIterations];
        for (int i = 0; i<maxIterations; i++) {
            colorPalette[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }
        int width = 1920;
        int height = 1080;
        double resolutionProportion = (double)height / width;
        double xLimitLow = 0;
        double xLimitHigh = 2;
        double yLimitLow = 0;
        double yLimitHigh = 2;
        double verticalStepBetweenPixels = (xLimitHigh - xLimitLow) / width;
        double horizontalStepBetweenPixels = (yLimitHigh - yLimitLow) / height;
        int calculationLimit = 16;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        double cImaginary;
        double cReal;
        for (int row = 0; row < height; row++) {
            System.out.println("Progress: " + (row + 1) + "/" + height);
            cImaginary = (yLimitLow + row * horizontalStepBetweenPixels) * resolutionProportion;

            for (int col = 0; col < width; col++) {
                cReal = xLimitLow + col * verticalStepBetweenPixels;
                Complex C = new Complex(cReal, cImaginary);
                Complex Z = new Complex(0, 0);
                Complex e = new Complex(Math.E, 0);

                int iterationCounter = 0;
                while (Z.getReal() * Z.getReal() + Z.getImaginary() * Z.getImaginary() < calculationLimit &&
                        iterationCounter < maxIterations) {
                    Z = C.multiply(Z.cos());
                    iterationCounter++;
                }
                if (iterationCounter < maxIterations){
                    image.setRGB(col, row, colorPalette[iterationCounter]);
                } else {
                    image.setRGB(col, row, 0);
                }
            }
        }

        System.out.println("Elapsed time: " + ((System.nanoTime() - startTime)/1000000000.0));
        ImageIO.write(image, "png", new File("mandelbrot3.png"));
    }
}
