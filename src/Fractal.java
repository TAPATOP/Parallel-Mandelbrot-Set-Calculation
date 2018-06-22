import java.awt.*;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import org.apache.commons.math3.complex.Complex;

public class Fractal {
    public Fractal(int width, int height, int xLimitLow, int xLimitHigh, int yLimitLow, int yLimitHigh,
                   int calculationLimit, int maxIterations, String imageName) {
        this.width = width;
        this.height = height;
        this.xLimitLow = xLimitLow;
        this.xLimitHigh = xLimitHigh;
        this.yLimitLow = yLimitLow;
        this.yLimitHigh = yLimitHigh;
        this.calculationLimit = calculationLimit;
        this.maxIterations = maxIterations;
        this.imageName = imageName;

        this.resolutionProportion = (double)height / width;
        this.verticalStepBetweenPixels = ((double)xLimitHigh - xLimitLow) / width;
        this.horizontalStepBetweenPixels = ((double)yLimitHigh - yLimitLow) / height;
        this.colorPalette = new int[maxIterations];
        for (int i = 0; i<maxIterations; i++) {
            colorPalette[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public static void main(String[] args) throws Exception{
        Fractal a = new Fractal(1920, 1080, -2, 2, -2, 2, 4, 20, "mandelbrot2.png");
        LinkedList<Section> sections = a.splitCalculationsBySections();
        int counter = 0;
        while(!sections.isEmpty()){
            counter++;
            Section currentSection = sections.pop();
            if(counter % 2 == 0) {
                a.generate(currentSection.startHeight, currentSection.endHeight, currentSection.startWidth, currentSection.endWidth);
            }
        }

        a.exportImage();
    }

    private LinkedList<Section> splitCalculationsBySections(){
        LinkedList<Section> sections = new LinkedList<>();

        int dHeight = 150;
        int currHeight = dHeight;
        int dWidth = 150;
        int currWidth;
        for(; currHeight <= height; currHeight += dHeight){
            for(currWidth = dWidth; currWidth <= width; currWidth += dWidth){
                sections.add(new Section((currHeight - dHeight), currHeight, (currWidth - dWidth), currWidth));
            }
            if(currWidth > width){
               sections.add(new Section((currHeight - dHeight), currHeight, (width - (dWidth -(currWidth - width))), width));
            }
        }
        if(currHeight > height){
            sections.add(new Section(height - (dHeight - (currHeight - height)), height,0, width));
        }

        return sections;
    }

    public void exportImage() throws Exception{
        ImageIO.write(image, "png", new File(imageName));
    }

    private void generate(int heightStart, int heightLimit, int widthStart, int widthLimit) throws Exception {
        long startTime = System.nanoTime();

        drawFractalByBoundaries(heightStart, heightLimit, widthStart, widthLimit);

        System.out.println("Elapsed time: " + ((System.nanoTime() - startTime)/1000000000.0));
    }

    private void drawFractalByBoundaries(int heightStart, int heightLimit, int widthStart, int widthLimit){
        double cImaginary;
        double cReal;
        for (int row = heightStart; row < heightLimit; row++) {
            System.out.println("Progress: " + (row + 1) + "/" + height);
            cImaginary = (yLimitLow + row * horizontalStepBetweenPixels) * resolutionProportion;

            for (int col = widthStart; col < widthLimit; col++) {
                cReal = xLimitLow + col * verticalStepBetweenPixels;
                Complex C = new Complex(cReal, cImaginary);
                Complex Z = new Complex(0, 0);

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
    }

    // Member variables //
    private int width;
    private int height;
    private int xLimitLow;
    private int xLimitHigh;
    private int yLimitLow;
    private int yLimitHigh;
    private int calculationLimit;
    private int maxIterations;
    private String imageName;

    private double resolutionProportion;
    private double verticalStepBetweenPixels;
    private double horizontalStepBetweenPixels;
    private int[] colorPalette;
    private BufferedImage image;
}
