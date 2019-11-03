import java.util.ArrayList;
import java.util.List;

public class state {
    private List<LRproject> info;
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public void setInfo(List<LRproject> info) {
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public List<LRproject> getInfo() {
        return info;
    }

    public List<LRproject> getNext(List<String> terminate){
        List<LRproject> result = new ArrayList<>();
        for(int i = 0 ; i < info.size(); i ++){
            LRproject x = info.get(i);
            String[] right = x.getGrammar().getRight();
            for(int j = 0 ; j < right.length - 1; j++){
                if(right[j].equals(".") && terminate.contains(right[j + 1]))
                    result.add(x);
            }
        }
        return result;
    }
    public List<LRproject> getLast(){
        List<LRproject> result = new ArrayList<>();
        for(int i = 0 ; i < info.size(); i ++){
            LRproject x = info.get(i);
            if(x.getGrammar().getRight()[x.getGrammar().getRight().length - 1].equals("."))
                result.add(x);
        }
        return result;
    }
    public boolean containAcc(){
        for(int i = 0 ; i < info.size(); i ++){
            LRproject x = info.get(i);
            if(x.getGrammar().getLeft().equals("P")&& x.getLater().equals("#"))
                if(x.getGrammar().getRight()[x.getGrammar().getRight().length - 1].equals("."))
                    return true;
        }
        return false;
    }
    public void print(){
        System.out.print(this.id);
        System.out.print(":\n");
        for(int i = 0 ; i < this.info.size(); i ++){
            this.info.get(i).print();
        }
        System.out.println();
    }
    public boolean hasLRp(LRproject LRp){
        for(int i = 0 ; i < info.size(); i++){
            if(info.get(i).getGrammar().getLeft().equals(LRp.getGrammar().getLeft()))
                if(info.get(i).getLater().equals(LRp.getLater())){
                    String[] s1 = info.get(i).getGrammar().getRight();
                    String[] s2 = LRp.getGrammar().getRight();
                    if(s1.length == s2.length){
                        int flag = 0;
                        for(int j = 0 ; j < s1.length; j++){
                            if(!s1[j].equals(s2[j])){
                                flag = 1;
                                return false;
                            }
                        }
                        if(flag == 0)
                            return true;
                    }
                }
        }
        return false;
    }

}
