import java.util.List;

public class grammarTable {

    private String name;
    private String left;
    private String[] right;
    private int rightLength;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String[] getRight() {
        return right;
    }
    public void setRight(String[] value) {
        this.right = value;
        this.rightLength = value.length;
    }

    public String getLeft() {
        return left;
    }
    public void setLeft(String value) {
        this.left = value;
    }
    public int getRightLength(){return rightLength;}
    public void print(){
        System.out.print( this.left+"->");
        String[] s = this.right;
        for(int j = 0; j < s.length; j++)
            System.out.print(s[j]);
        System.out.println();
    }


    public boolean equals(grammarTable obj) {
        boolean x = true;
        if(obj.getLeft().equals(this.left)){
            if(obj.getRight().length == this.getRight().length){
                for(int i = 0; i < this.getRight().length; i++)
                    if(!obj.getRight()[i].equals(this.getRight()[i])){
                        x = false;
                        break;
                    }
            }
            else x = false;
        }
        else x = false;
        return x;
    }
}
