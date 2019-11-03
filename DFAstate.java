import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DFAstate {
    private List<grammarTable> closure;
    private int id;
    private grammarTable origin;

    public void setClosure(List<grammarTable> closure) {
        this.closure = closure;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrigin(grammarTable origin) {
        this.origin = origin;
    }

    public grammarTable getOrigin() {
        return this.origin;
    }

    public int getId() {
        return this.id;
    }

    public List<grammarTable> getClosure() {
        return this.closure;
    }
    public void print(){
        System.out.print(this.id);
        System.out.print(". " );
        this.origin.print();
        //System.out.println();
        for(int i = 0 ; i < this.closure.size(); i ++){
            grammarTable t = this.closure.get(i);
            t.print();
        }
    }

}
