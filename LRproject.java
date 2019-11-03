import java.util.List;

public class LRproject {
    private grammarTable grammar;
    private String later;

    public void setGrammar(grammarTable grammar) {
        this.grammar = grammar;
    }

    public grammarTable getGrammar() {
        return grammar;
    }

    public String getLater() {
        return later;
    }

    public void setLater(String later) {
        this.later = later;
    }

    public void print(){
        grammar.print();
        System.out.print("  later: " + later);
        System.out.println();
    }

    public boolean equals(LRproject p) {
        if(this.later.equals(p.getLater())){
            if(this.getGrammar().getLeft().equals(p.getGrammar().getLeft())){
                int flag = 0;
                if(this.getGrammar().getRight().length == p.getGrammar().getRight().length){
                    for(int j = 0 ; j < this.getGrammar().getRight().length; j ++){
                        if(!this.getGrammar().getRight()[j].equals(p.getGrammar().getRight()[j])){
                            flag = 1;
                            return false;
                        }
                    }
                }
                if(flag == 0)
                    return true;
            }

        }
        return false;
    }
}
