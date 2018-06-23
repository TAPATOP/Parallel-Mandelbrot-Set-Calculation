import java.awt.*;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import org.apache.commons.math3.complex.Complex;

public class Fractal {
    // Constructors and initialization //
    private Fractal(int width, int height, int xLimitLow, int xLimitHigh, int yLimitLow, int yLimitHigh,
                   int calculationLimit, int maxIterations, String imageName, boolean isQuiet) {
        this.width = width;
        this.height = height;
        this.xLimitLow = xLimitLow;
        this.xLimitHigh = xLimitHigh;
        this.yLimitLow = yLimitLow;
        this.yLimitHigh = yLimitHigh;
        this.calculationLimit = calculationLimit;
        this.maxIterations = maxIterations;
        this.imageName = imageName;
        this.isQuiet = isQuiet;

        calculateRestOfMembers();
    }

    private Fractal(){
        width = 640;
        height = 480;
        xLimitLow = -2;
        xLimitHigh = 2;
        yLimitLow = -2;
        yLimitHigh = 2;
        calculationLimit = 16;
        maxIterations = 50;
        imageName = "zad18.png";
        isQuiet = false;
    }

    private Fractal(String[] args){
        this();
        setMembersByInputArguments(args);

        calculateRestOfMembers();
    }

    private void setMembersByInputArguments(String[] args){
        for (int i = 0; i < args.length; i++){
            switch (args[i]) {
                case "-q":
                    isQuiet = true;
                    break;
                case "-s":
                    String[] sizes = args[i + 1].split("x");
                    width = Integer.parseInt(sizes[0]);
                    height = Integer.parseInt(sizes[1]);
                    i++;
                    break;
                case "-r":
                    String[] limits = args[i + 1].split(":");
                    xLimitLow = Double.parseDouble(limits[0]);
                    xLimitHigh = Double.parseDouble(limits[1]);
                    yLimitLow = Double.parseDouble(limits[2]);
                    yLimitHigh = Double.parseDouble(limits[3]);
                    i++;
                    break;
                case "-t":
                    dedicatedThreadsCount = Integer.parseInt(args[i + 1]);
                    i++;
                    break;
                case "-o":
                    imageName = args[i + 1];
                    i++;
                    break;
                default:
                    System.out.println("Unknown parameter");
            }
        }

    }

    private void calculateRestOfMembers(){
        resolutionProportion = (double)height / width;
        verticalStepBetweenPixels = ((double)xLimitHigh - xLimitLow) / width;
        horizontalStepBetweenPixels = ((double)yLimitHigh - yLimitLow) / height;
        colorPalette = new int[maxIterations];
        for (int i = 0; i<maxIterations; i++) {
            colorPalette[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    // Actual methods//
    public static void main(String[] args) throws Exception{
        //Fractal a = new Fractal(1920 * 4, 1080 * 4, -2, 2, -2, 2, 16, 50, "mandelbrot2.png", false);
        Fractal a = new Fractal(args);
        LinkedList<Section> sections = a.splitCalculationsBySections();
        Scheduler scheduler = new Scheduler(sections);

        Thread[] threads = new Thread[a.dedicatedThreadsCount];

        long startTime = System.nanoTime();
        for(int i = 0; i < a.dedicatedThreadsCount; i++){
            threads[i] = new Thread(new FractalThread(a, scheduler, "Thread " + i));
            threads[i].start();
        }

        for(int i = 0; i < a.dedicatedThreadsCount; i++){
            threads[i].join();
        }

        System.out.println("Elapsed time: " + ((System.nanoTime() - startTime)/1000000000.0));

        a.exportImage();
    }

    boolean isQuiet(){
        return isQuiet;
    }

    private LinkedList<Section> splitCalculationsBySections(){
        LinkedList<Section> sections = new LinkedList<>();

        int dHeight = 350;
        int currHeight = dHeight;
        int dWidth = 350;
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

    private void exportImage() throws Exception{
        ImageIO.write(image, "png", new File(imageName));
    }

    void calculateSection(Section section){
        calculateByBoundaries(section.startHeight, section.endHeight, section.startWidth, section.endWidth);
    }

    private void calculateByBoundaries(int heightStart, int heightLimit, int widthStart, int widthLimit){
        double cImaginary;
        double cReal;
        long startTime = System.nanoTime();
        for (int row = heightStart; row < heightLimit; row++) {
            //System.out.println("Progress: " + (row + 1) + "/" + height);
            cImaginary = (yLimitLow + row * horizontalStepBetweenPixels) * resolutionProportion;

            for (int col = widthStart; col < widthLimit; col++) {
                cReal = xLimitLow + col * verticalStepBetweenPixels;
                Complex C = new Complex(cReal, cImaginary);
                Complex Z = new Complex(0, 0);

                int iterationCounter = 0;
                while (Z.getReal() * Z.getReal() + Z.getImaginary() * Z.getImaginary() < calculationLimit &&
                        iterationCounter < maxIterations) {
                    Z=C.multiply(Z.cos());
                    iterationCounter++;
                }
                if (iterationCounter < maxIterations){
                    image.setRGB(col, row, colorPalette[iterationCounter]);
                } else {
                    image.setRGB(col, row, 0);
                }
            }
        }
//        System.out.println(Thread.currentThread().getName() + "calculating time: " + ((System.nanoTime() - startTime)/1000000000.0));
    }

    // Member variables //
    private int width;
    private int height;
    private double xLimitLow;
    private double xLimitHigh;
    private double yLimitLow;
    private double yLimitHigh;
    private int calculationLimit;
    private int maxIterations;
    private String imageName;
    private int dedicatedThreadsCount;
    private boolean isQuiet;

    private double resolutionProportion;
    private double verticalStepBetweenPixels;
    private double horizontalStepBetweenPixels;
    private int[] colorPalette;
    private BufferedImage image;
}
