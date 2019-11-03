package LexicalAnalysis;
import java.util.HashMap;
import java.util.Map;

public class NUM_Table{
    private String name;
    private Map nums;
    private int length;
    public void start(String Name){
        this.name = Name;
        this.nums = new HashMap();
        this.length = 0;
    }
    public void addNode(String num){
        if(!this.nums.containsKey(num)){
            this.nums.put(num, this.length);
            this.length += 1;
        }
    }
}
