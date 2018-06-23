import java.util.LinkedList;

class Scheduler {
    Scheduler(LinkedList<Section> sections){
        this.sections = sections;
    }

    synchronized Section pop(){
        return sections.pop();
    }

    boolean isEmpty(){
        return sections.isEmpty();
    }

    // Member variables //
    private LinkedList<Section> sections;
}
