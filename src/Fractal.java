import java.awt.Color;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.commons.math3.complex.Complex;

public class Fractal {
    public static void main(String[] args) throws Exception {
        int width = 1920, height = 1080, maxIterations = 50;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] colorPalette = new int[maxIterations];
        for (int i = 0; i<maxIterations; i++) {
            colorPalette[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }

        long startTime = System.nanoTime();

        for (int row = 0; row < height; row++) {
            System.out.println("Progress: " + row + "/" + height);
            for (int col = 0; col < width; col++) {
                double cReal = (col - width / 2) * 4.0 / width;
                double cImaginary = (row - height / 2) * 4.0 / width;
                Complex C = new Complex(cReal, cImaginary);
                Complex Z = new Complex(0, 0);
                Complex e = new Complex(Math.E, 0);

                int iterationCounter = 0;
                while (Z.getReal() * Z.getReal() + Z.getImaginary() * Z.getImaginary() < 4 && iterationCounter < maxIterations) {

                    if(Z.getReal() != 0 || Z.getImaginary() != 0){
                        Z = Z.multiply(Z);
                    }
                    Z = Z.add(C);
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
        ImageIO.write(image, "png", new File("mandelbrot2.png"));
    }
}
