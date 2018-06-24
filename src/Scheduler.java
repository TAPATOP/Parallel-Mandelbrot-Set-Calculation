import java.util.LinkedList;

class Scheduler {
    Scheduler(LinkedList<Section> sections){
        this.sections = sections;
    }

    synchronized Section pop(){
        if(isEmpty()) return null;
        return sections.pop();
    }

    boolean isEmpty(){
        return sections.isEmpty();
    }

    // Member variables //
    private LinkedList<Section> sections;
}
