package forms;

import java.util.ArrayList;

public record FormList(ArrayList<Form> elements) implements Form {

    public int size(){
        return elements.size();
    }

    public Form get(int i){
        return elements.get(i);
    }
}