package LexicalAnalysis;
import java.io.*;
import java.util.HashMap;

public class LexicalAnalysis {

    public static void run(){
        HashMap<String, String> tagsMap = new HashMap<>();
        String tag = readFile("src\\tag.txt");
        String[] tags = tag.split("\n");
        ID_Table table = new ID_Table();
        table.start("table");
        NUM_Table num_table = new NUM_Table();
        num_table.start("num_table");
        String content = "";
        for(int i = 0; i < tags.length ; i++){
            //System.out.print(tags[i].split("\t")[0]);
            tagsMap.put(tags[i].split("\t")[0],tags[i].split("\t")[1]);
            //System.out.print(tags[i].split("\t")[0]);
        }
        String xx = readFile("src\\test.txt");

        try{
            char[] input = xx.toCharArray();
            for(int i = 0; i < input.length; i++){
                //System.out.print(input[i]);
                if(isDigit(input[i])){

                    String word = "";
                    word += input[i];
                    int flag = i;
                    while( i+ 1 < input.length && (isDigit(input[i + 1])|| input[ i+ 1] == '.')){
                        word += input[i+ 1];
                        i ++;
                        if(i - flag > 256)
                            throw new too_longException();
                    }
                    num_table.addNode(word);
                    content += word + "\t" + "<" + "CONST" + " , " + word + ">" + "\n";
                }
                else if(isLetter(input[i]))  {
                    String word = "";
                    word += input[i];
                    int j = i+1;
                    while(j < input.length && isLetter(input[j])){
                        word += input[j];
                        j ++;
                        if(j - i >256)
                            throw new too_longException();
                    }
                    if(tagsMap.containsKey(word))
                        content += word + "\t" + "<" + tagsMap.get(word) + " , " + "-" + ">" + "\n";
                    else{
                        content += word + "\t" + "<" + "IDN" + " , " + word + ">" + "\n";
                        ID_Node id = new ID_Node();
                        id.setID(word);
                        table.addNode(id);
                    }

                    i = j - 1;

                }
                else if(isDelimiter(input[i])){
                    content += input[i] + "\t" + "<" + tagsMap.get(String.valueOf(input[i])) + " , " + "-" + ">" + "\n";
                }
                else if(isOperators(input[i])){
                    if(i + 1 < input.length && !isOperators(input[i+1]))
                        content += input[i] + "\t" + "<" + tagsMap.get(String.valueOf(input[i])) + " , " + "-" + ">" + "\n";
                    else{
                        String word = "";
                        word += input[i];
                        word += input[i + 1];

                        int flag = i;

                        if(input[i] != '/' || input[i+1] != '*'){
                            System.out.print(word + "\n");
                            content += word + "\t" + "<" + tagsMap.get(word) + " , " + "-" + ">" + "\n";
                            i += 1;
                        }
                        else{

                            while(i + 1 < input.length){

                                i++;
                                if(i - flag > 256)
                                    throw new too_longException();
                                if(input[i] == '*' && input[i+1] == '/'){
                                    //System.out.print(i);
                                    i ++;
                                    break;
                                }

                            }
                        }

                    }
                }
                else{
                    if(!shouldDelete(input[i]))
                        throw new illegal_wordException();
                }


            }


        }catch (too_longException e){
            RuntimeException exception  =  new RuntimeException(e);
            throw  exception;
        }catch (illegal_wordException x){
            RuntimeException exception  =  new RuntimeException(x);
            throw  exception;
        }
        System.out.print(content);
        writeFile(content,"src\\LexicalAnalysis_result.txt" );
    }

    public static boolean isLetter(char ch){//isLetter 标识符
        if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) return true;
        else return false;
    }

    public static boolean isDigit(char ch){//isDigit 常数
        if (ch >= '0' && ch <= '9') return true;
        else return false;
    }
    public static boolean isOperators(char ch){// isOperators 运算符
        if (ch == '+' || ch == '*' || ch == '-' || ch == '/' || ch == '=' || ch == ':' || ch == '<' || ch == '>' || ch == '!') return true;
        else return false;
    }
    public static boolean isDelimiter(char ch){// isDelimiter 界符
        if (ch == ',' || ch == ';' || ch == '.' || ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{' || ch == '}' || ch == '#') return true;
        else return false;
    }
    public static boolean shouldDelete(char ch){
        if (ch == ' ' || ch == '\t'|| ch == '\n') return true;
        else return false;
    }

    public static String readFile(String pathname){
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line + "\n";
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFile(String content, String pathname) {
        try {
            File writeName = new File(pathname);
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(content);
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
