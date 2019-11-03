package LexicalAnalysis;
import java.util.HashMap;
import java.util.Map;

public class ID_Table {
    private String name;
    public Map ids;
    private int length;
    public void start(String Name){
        this.name = Name;
        this.ids = new HashMap();
        this.length = 0;
    }
    public void addNode(ID_Node idnode){
        if(!this.ids.containsKey(idnode.getName())){
            this.ids.put(idnode, this.length);
            this.length += 1;
        }
    }
}
