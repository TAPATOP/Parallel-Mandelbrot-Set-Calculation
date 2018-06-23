import java.util.LinkedList;

public class FractalThread implements Runnable {
    FractalThread(Fractal fractal, Scheduler sections, String name){
        this.fractal = fractal;
        this.sections = sections;
        this.name = name;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        while(!sections.isEmpty()){
            Section currentSection = sections.pop();
            if(!fractal.isQuiet()){
                System.out.println(name + " is calculating " + currentSection.startHeight + ":" +
                        currentSection.endHeight + " " + currentSection.startWidth + ":" + currentSection.endWidth);
            }
            fractal.calculateSection(currentSection);
        }
        System.out.println(name + " time of work:" + ((System.nanoTime() - startTime)/1000000000.0));
    }

    // Member variables //
    private Fractal fractal;
    private Scheduler sections;
    private String name = "";
}
