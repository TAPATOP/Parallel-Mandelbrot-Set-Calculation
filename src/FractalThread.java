import java.util.LinkedList;

public class FractalThread implements Runnable {
    public FractalThread(Fractal fractal, Scheduler sections, String name){
        this.fractal = fractal;
        this.sections = sections;
        this.name = name;
    }

    @Override
    public void run() {
        while(!sections.isEmpty()){
            Section currentSection = sections.pop();
            System.out.println(name + " is calculating " + currentSection.startHeight + ":" + currentSection.endHeight +
            " " + currentSection.startWidth + ":" + currentSection.endWidth);
            fractal.calculateSection(currentSection);
        }
    }

    // Member variables //
    private Fractal fractal;
    private Scheduler sections;
    private String name = "";
}
