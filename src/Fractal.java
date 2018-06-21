import java.awt.Color;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Fractal {
    public static void main(String[] args) throws Exception {
        int width = 1920, height = 1080, maxIterations = 1000;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] colorPalette = new int[maxIterations];
        for (int i = 0; i<maxIterations; i++) {
            colorPalette[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                double cReal = (col - width / 2) * 4.0 / width;
                double cImaginary = (row - height / 2) * 4.0 / width;
                double x = 0, y = 0;
                double xNew;
                int iterationCounter = 0;

                while (x * x + y * y < 4 && iterationCounter < maxIterations) {
                    xNew = x * x - y * y + cReal;
                    y = 2 * x * y + cImaginary;
                    x = xNew;
                    iterationCounter++;
                }
                if (iterationCounter < maxIterations){
                    image.setRGB(col, row, colorPalette[iterationCounter]);
                } else {
                    image.setRGB(col, row, 0);
                }
            }
        }

        ImageIO.write(image, "png", new File("mandelbrot2.png"));
    }
}
