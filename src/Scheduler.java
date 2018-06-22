import java.util.LinkedList;

public class Scheduler {
    Scheduler(LinkedList<Section> sections){
        this.sections = sections;
    }

    public synchronized Section pop(){
        return sections.pop();
    }

    public boolean isEmpty(){
        return sections.isEmpty();
    }

    // Member variables //
    private LinkedList<Section> sections;
}
