import java.util.ArrayList;
import java.util.List;

public class SStack {
    private List<state> list;
    private int size;
    public state showTop(){
        return list.get(size - 1);
    }
    public void start(){
        this.list = new ArrayList<>();
        this.size = 0;
    }
    public void Pop(){
        list.remove(size - 1);
        this.size --;
    }
    public void Push(state s){
        this.size++;
        this.list.add(s);
    }
    public void print(){
        for(int i = 0; i< size; i++){
            System.out.print(list.get(i).getId());
        }
        System.out.println();
    }


}