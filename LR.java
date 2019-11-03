import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import java.io.*;
import java.util.*;

public class LR {

    private static grammarTable[] grammar;
    private static List<String> terminate;
    private static List<String> noterminate;
    private static Map<String, List<String>> getFirst ;
    private static Map<String, List<String>> getFollow;
    private static List<grammarTable> augGrammar;
    private static List<state> states;
    private static Map<Integer,Map<String, String>> LR_action;
    private static Map<Integer,Map<String, String>> LR_goto;
    private static Map<Integer, Map<String, Integer>> Go;
    private static List<Map<Integer,Map<Integer,String[]>>> content;
    private static List<String[][]> token_result;
    private static Map<String,List<String>> defineMap;
    private static String  valueDesign;

    public static void main(String[] args){
        //readResult();
        LexicalAnalysis.LexicalAnalysis.run();
        HashMap<String, String> tagsMap = new HashMap<>();
        String tag = readFile("src\\tag.txt");
        String[] tags = tag.split("\n");
        for(int i = 0; i < tags.length ; i++)
            tagsMap.put(tags[i].split("\t")[0],tags[i].split("\t")[1]);
        HashMap<String, String> gMap = new HashMap<>();
        String g = readFile("src\\grammar.txt");
        String[] gs = g.split("\n");
        for(int i = 0; i < gs.length ; i++)
            gMap.put(gs[i].split("->")[0],gs[i].split("->")[1]);
        System.out.print("文法如下：\n");
        grammar = new grammarTable[gs.length];
        for(int i = 0; i< gs.length; i ++){
            grammar[i] = new grammarTable();
            grammar[i].setName(String.valueOf(i));
            grammar[i].setLeft(gs[i].split("->")[0]);
            grammar[i].setRight(gs[i].split("->")[1].split(" "));
            grammar[i].print();
        }
        prepare();
        makeTables();
        token_result = new ArrayList<>();
        defineMap = new HashMap<>();
        valueDesign = "";
        String s = readResult();


        System.out.print(s);
        String[] t = s.split("\n");
        content = new ArrayList<>();
        for(int j = 0 ; j < t.length; j++){
            System.out.print("\n第"+String.valueOf(j)+"行：\n");
            String[] word = t[j].replace("\n","").split(" ");
            for(int i = 0 ; i < word.length; i++){
                System.out.print(word[i]);
                System.out.print(" ");
            }
            analysis(word, j);
        }
        printDefine();
        System.out.print("四元式:\n");
        System.out.print(valueDesign);
        //printAcGo();


    }
    public static void printDefine(){
        System.out.print("填入符号表：\n");
        for(String str : defineMap.keySet()){
            System.out.print(str + " : ");
            List<String> list = defineMap.get(str);
            for(int i = 0 ; i < list.size(); i ++)
                System.out.print(list.get(i) + " ");
            System.out.println();

        }
    }

    public static String readResult(){
        String s = "";
        String origin = readFile("src\\LexicalAnalysis_result.txt");
        String[] ori = origin.split("\n");
        String[][] orig = new String[ori.length][3];
        List<Integer> where = new ArrayList<>();
        for(int i= 0 ; i < ori.length;i++){
            orig[i][0] = ori[i].split("\t")[0];
            String str = ori[i].split("\t")[1].replace("<","").replace(">","").replace(" ","");
            orig[i][1] = str.split(",")[0];
            orig[i][2] = str.split(",")[1];
            //System.out.print(orig[i][2] + "\n");
            if(orig[i][0].equals("#"))
                where.add(i);
        }

        for(int i = 0 ; i < where.size();i ++){
            List<String[][]> line = new ArrayList<>();
            int Final = where.get(i);

            if(i== 0){
                String[][] str = new String[Final][3];
                for(int j = 0; j < Final; j++){

                    str[j][0] = orig[j][0];
                    str[j][1] = orig[j][1];
                    str[j][2] = orig[j][2];

                }
                token_result.add(str);
            }
            else{
                //System.out.print(where.get(i - 1) + 1);
                String[][] str = new String[Final - where.get(i - 1) - 1][3];
                for(int j = where.get(i - 1) + 1; j < Final; j++){

                    str[j - (where.get(i - 1) + 1)][0] = orig[j][0];
                    str[j - (where.get(i - 1) + 1)][1] = orig[j][1];
                    str[j - (where.get(i - 1) + 1)][2] = orig[j][2];
                   // System.out.print("\n" + str[0][0]+"\n");

                }
                token_result.add(str);
            }

        }
        for(int i = 0 ; i < ori.length; i++){
            if(orig[i][2].equals("-")){
                if(orig[i][0].equals("#"))
                    s += orig[i][0] + "\n";
                else s += orig[i][0] + " ";
            }
            else{
                s += orig[i][1] + " ";
            }
        }
        //System.out.print(s);
        return s;
    }

    public static void printAcGo(){
        List<String> list = new ArrayList();
        list.addAll(terminate);
        list.addAll(noterminate);
        list.remove("P");
        for(int i = 0;i < list.size();i++){
            System.out.print(list.get(i));
            for(int j = 0 ; j < 6 - list.get(i).length(); j++)
                System.out.print(" ");

        }
        System.out.println();
        for(int i = 0 ; i < states.size();i++){
            for(int j = 0 ; j < list.size(); j++){
                if(LR_action.containsKey(i) && LR_action.get(i).containsKey(list.get(j))){
                    System.out.print(LR_action.get(i).get(list.get(j)));
                    for(int p = 0 ; p < 6 - LR_action.get(i).get(list.get(j)).length(); p++)
                        System.out.print(" ");
                }
                else if(LR_goto.containsKey(i) && LR_goto.get(i).containsKey(list.get(j))){
                    System.out.print(LR_goto.get(i).get(list.get(j)));
                    for(int p = 0 ; p < 6 - LR_goto.get(i).get(list.get(j)).length(); p++)
                        System.out.print(" ");
                }
                else{
                    System.out.print("      ");
                }

            }
            System.out.println();
        }
    }

    public static void analysis(String[] word, int line){
        try{
            Map<Integer,Map<Integer,String[]>> map = new HashMap<>();
            SStack stateStack = new SStack();stateStack.start();
            VStack valueStack = new VStack();valueStack.start();
            stateStack.Push(states.get(0));
            valueStack.Push("#");
            List<String[]> do_str = new ArrayList<>();
            List<Integer> f = new ArrayList<>();
            int ip = 0;
            while(1 == 1){
                int flag = 0;
                //stateStack.print();
                //valueStack.print();
                if(LR_action.containsKey(stateStack.showTop().getId()) && LR_action.get(stateStack.showTop().getId()).containsKey(word[ip])){
                    String v = LR_action.get(stateStack.showTop().getId()).get(word[ip]);
                    if(v.toCharArray()[0] == 'S'){
                        String str = v.substring(1, v.length());
                        stateStack.Push(states.get(Integer.valueOf(str)));
                        valueStack.Push(word[ip]);
                        ip += 1;
                        flag = 1;
                    }
                }
                if(LR_action.containsKey(stateStack.showTop().getId()) && LR_action.get(stateStack.showTop().getId()).containsKey(word[ip])){
                    String v = LR_action.get(stateStack.showTop().getId()).get(word[ip]);
                    if(v.toCharArray()[0] == 'r'){
                        String str = v.substring(1, v.length());
                        int l = grammar[Integer.valueOf(str)].getRight().length;
                        grammar[Integer.valueOf(str)].print();
                        for(int i = 0 ; i < l; i++){
                            stateStack.Pop();
                            valueStack.Pop();
                        }
                        if(str.equals("1") || str.equals("2")){
                            //声明语句
                            if(defineMap.containsKey(token_result.get(line)[0][0]))
                                defineMap.get(token_result.get(line)[0][0]).add(token_result.get(line)[1][0]);
                            else{
                                List<String> temp = new ArrayList<>();
                                temp.add(token_result.get(line)[1][0]);
                                defineMap.put(token_result.get(line)[0][0], temp);
                            }

                        }
                        else if(str.equals("3")){
                            //赋值语句
                            String temp = "";
                            for(String str_d : defineMap.keySet()){
                                List<String> list_d = defineMap.get(str_d);
                                for(int i = 0 ; i < list_d.size(); i ++){
                                    if(list_d.get(i).equals(token_result.get(line)[ip - 3][0])){
                                        temp += str_d;
                                        break;
                                    }
                                }

                            }
                            System.out.print(ip );

                            if(temp.equals(""))
                                throw new not_defined_wordException();
                            else if(temp.equals("int")){
                                //System.out.print(token_result.get(line)[2][0] +"\n\n\n");
                                for(int t = 0 ; t < token_result.get(line)[ip-1][0].length();t++){
                                    if(token_result.get(line)[ip-1][0].toCharArray()[t] == '.'){
                                        //System.out.print(token_result.get(line)[2][0] +"\n\n\n");
                                        throw new illegalValueException();
                                    }
                                }

                                    valueDesign +=   token_result.get(line)[ip-1][0]+" " + token_result.get(line)[ip-3][0] + "\n";


                            }
                            else{

                                    valueDesign += token_result.get(line)[ip-1][0]+" " + token_result.get(line)[ip-3][0] + "\n";
                            }

                            if(f.size() != 0 && f.get(f.size() - 1) == 0){
                                String[] do_s = new String[4];
                                do_s[0] = token_result.get(line)[2][0];
                                do_s[1] = token_result.get(line)[0][0];
                                do_s[2] = "-";
                                do_str.add(do_s);
                                f.add(1);
                            }
                            else if(f.size() != 0 && f.get(f.size() - 1) == 1){
                                String back = String.valueOf(do_str.size() + 2);
                                String[] do_s = new String[4];
                                do_s[0] = token_result.get(line)[2][0];
                                do_s[1] = token_result.get(line)[0][0];
                                do_s[2] = "-";
                                do_s[3] = back;
                                do_str.add(do_s);
                                f.add(2);
                                do_str.get(do_str.size() - 2)[3] = back;
                            }
                            else if(f.size() != 0 && f.get(f.size() - 1) == 3){
                                String back = String.valueOf(do_str.size() + 2);
                                String[] do_s = new String[4];
                                do_s[0] = token_result.get(line)[2][0];
                                do_s[1] = token_result.get(line)[0][0];
                                do_s[2] = "-";
                                do_s[3] = back;
                                do_str.add(do_s);
                                do_str.get(do_str.size() - 2)[3] = back;
                                f.add(4);
                            }

                        }
                        else if(str.equals("4") || str.equals("5")){
                            String temp = "";
                            for(String str_d : defineMap.keySet()){
                                List<String> list_d = defineMap.get(str_d);
                                for(int i = 0 ; i < list_d.size(); i ++){
                                    if(list_d.get(i).equals(token_result.get(line)[ip - 5][0])){
                                        temp += str_d;
                                        break;
                                    }
                                }

                            }
                            //System.out.print(ip +"\n\n\n");
                            if(temp.equals(""))
                                throw new not_defined_wordException();
                            else{
                                if(!token_result.get(line)[0][0].equals("do"))
                                    valueDesign += token_result.get(line)[ip-2][0]+" " + token_result.get(line)[ip-1][0]
                                        + " "+ token_result.get(line)[ip-3][0] + " " + token_result.get(line)[ip - 5][0] + "\n";
                            }

                            if(f.size() != 0 && f.get(f.size() - 1) == 0){
                                String[] do_s_1 = new String[4];
                                do_s_1[0] = token_result.get(line)[ip-2][0];
                                do_s_1[1] = token_result.get(line)[ip-1][0];
                                do_s_1[2] = token_result.get(line)[ip-3][0];
                                do_s_1[3] = "t1";
                                do_str.add(do_s_1);
                                String[] do_s_2 = new String[4];
                                do_s_2[0] = "t1";
                                do_s_2[1] = token_result.get(line)[ip - 5][0];
                                do_s_2[2] = "-";
                                do_str.add(do_s_2);
                                f.add(3);
                            }
                            else if(f.size() != 0 && f.get(f.size() - 1) == 1){
                                String back = String.valueOf(do_str.size() + 3);
                                String[] do_s_1 = new String[4];
                                do_s_1[0] = token_result.get(line)[ip-2][0];
                                do_s_1[1] = token_result.get(line)[ip-1][0];
                                do_s_1[2] = token_result.get(line)[ip-3][0];
                                do_s_1[3] = "t1";
                                do_str.add(do_s_1);
                                String[] do_s_2 = new String[4];
                                do_s_2[0] = "t1";
                                do_s_2[1] = token_result.get(line)[ip - 5][0];
                                do_s_2[2] = "-";
                                do_s_2[3] = back;
                                do_str.add(do_s_2);
                                do_str.get(do_str.size() - 3)[3] = back;
                                f.add(3);
                            }
                            else if(f.size() != 0 && f.get(f.size() - 1) == 3){
                                String back = String.valueOf(do_str.size() + 3);
                                String[] do_s_1 = new String[4];
                                do_s_1[0] = token_result.get(line)[ip-2][0];
                                do_s_1[1] = token_result.get(line)[ip-1][0];
                                do_s_1[2] = token_result.get(line)[ip-3][0];
                                do_s_1[3] = "t2";
                                do_str.add(do_s_1);
                                String[] do_s_2 = new String[4];
                                do_s_2[0] = "t2";
                                do_s_2[1] = token_result.get(line)[ip - 5][0];
                                do_s_2[2] = "-";
                                do_s_2[3] = back;
                                do_str.add(do_s_2);
                                do_str.get(do_str.size() - 3)[3] = back;
                                f.add(3);
                            }
                        }
                        else if(word[0].equals("do") && str.equals("11") && !word[ip].equals("#")){
                            do_str = new ArrayList<>();
                            f = new ArrayList<>();
                            f.add(0);
                            String[] do_s = new String[4];
                            do_s[0] = token_result.get(line)[ip - 2][0];
                            do_s[1] = token_result.get(line)[ip - 3][0];
                            do_s[2] = token_result.get(line)[ip - 1][0];
                            do_s[3] = "2";
                            do_str.add(do_s);
                            System.out.print(token_result.get(line)[ip - 3][0] +  token_result.get(line)[ip - 2][0]+
                                    token_result.get(line)[ip - 1][0]);
                            System.out.print(word[ip] +"\n\n\n");
                        }
                        else if(word[0].equals("do") && str.equals("11") && word[ip].equals("#")){
                            String[] do_s = new String[4];
                            do_s[0] = token_result.get(line)[ip - 2][0];
                            do_s[1] = token_result.get(line)[ip - 3][0];
                            do_s[2] = token_result.get(line)[ip - 1][0];
                            do_s[3] = "1";
                            do_str.add(do_s);
                            for(int pp = 0 ; pp < do_str.size(); pp ++){
                                valueDesign += (String.valueOf(pp + 1) + "    ");
                                for(int xx = 0 ; xx < 4 ; xx++){
                                    valueDesign += (do_str.get(pp)[xx]);
                                    for(int yy = 0 ; yy < 6 - do_str.get(pp)[xx].length();yy ++)
                                        valueDesign += " ";
                                }
                                valueDesign += "\n";
                            }
                        }

                        state SS = stateStack.showTop();
                        valueStack.Push(grammar[Integer.valueOf(str)].getLeft());
                        int g = Integer.valueOf(LR_goto.get(SS.getId()).get(grammar[Integer.valueOf(str)].getLeft()));
                        stateStack.Push(states.get(g));
                        //System.out.print(grammar[Integer.valueOf(str)].getLeft());
                        //states.get(g).print();
                        flag = 1;
                    }

                }
                if(LR_action.containsKey(stateStack.showTop().getId()) && LR_action.get(stateStack.showTop().getId()).containsKey(word[ip])){
                    String v = LR_action.get(stateStack.showTop().getId()).get(word[ip]);
                    if(v.equals("acc")){
                        System.out.print("acc\n");
                        break;
                    }

                }
                if(flag == 0){
                    System.out.print("error\n");
                    throw new noProducerException();
                }

            }
            content.add(map);
        }catch (illegalValueException e){
            System.out.print("illegalValueException");
            throw  new RuntimeException(e);
        }catch (not_defined_wordException f){
            System.out.print("not_defined_wordException");
            throw  new RuntimeException(f);
        }catch(noProducerException p){
            System.out.print("noProducerException");
            throw  new RuntimeException(p);
        }


    }

    public static Integer findIndex(String[] s, String x){
        for(int i = 0; i < s.length; i ++){
            if(s[i].equals(x))
                return i;
        }
        return -1;
    }

    public static void makeTables(){
        LR_action = new HashMap<>();
        LR_goto = new HashMap<>();
        //int k = 0;
        for(int k = 0; k < states.size(); k ++)
        {

            if(states.get(k).getNext(terminate).size()!=0){
                for(int p = 0 ; p < states.get(k).getNext(terminate).size(); p ++){
                    LRproject lr = states.get(k).getNext(terminate).get(p);
                    int q = findIndex(lr.getGrammar().getRight(),".");
                    String a = lr.getGrammar().getRight()[q + 1];
                    if(Go.containsKey(k) && Go.get(k).containsKey(a) && Go.get(k).get(a)!=-1 ){
                        if(!LR_action.containsKey(k)){
                            Map<String, String> m = new HashMap<>();
                            m.put(a, "S"+String.valueOf(Go.get(k).get(a)));
                            LR_action.put(k, m);
                            System.out.print("action " + String.valueOf(k) + "   " + a + "   S"+String.valueOf(Go.get(k).get(a)) + "\n");
                        }
                        else if(LR_action.containsKey(k) && !LR_action.get(k).containsKey(a)){
                            Map<String, String> m = LR_action.get(k);
                            m.put(a, "S"+String.valueOf(Go.get(k).get(a)));
                            LR_action.put(k, m);
                            System.out.print("action " + String.valueOf(k) + "   " + a + "   S"+String.valueOf(Go.get(k).get(a)) + "\n");
                        }
                    }

                }
            }
            if(states.get(k).getNext(noterminate).size()!=0){
                for(int p = 0 ; p < states.get(k).getNext(noterminate).size(); p ++){
                    LRproject lr = states.get(k).getNext(noterminate).get(p);
                    int q = findIndex(lr.getGrammar().getRight(),".");
                    String B = lr.getGrammar().getRight()[q + 1];
                    if(Go.containsKey(k) && Go.get(k).containsKey(B) && Go.get(k).get(B)!=-1 ){
                        if(!LR_goto.containsKey(k)){
                            Map<String, String> m = new HashMap<>();
                            m.put(B, String.valueOf(Go.get(k).get(B)));
                            LR_goto.put(k, m);
                            System.out.print("goto " + String.valueOf(k) + "   " + B + "   "+String.valueOf(Go.get(k).get(B)) + "\n");
                        }
                        else if(LR_goto.containsKey(k) && !LR_goto.get(k).containsKey(B)){
                            Map<String, String> m = LR_goto.get(k);
                            m.put(B, String.valueOf(Go.get(k).get(B)));
                            LR_goto.put(k, m);
                            System.out.print("goto " + String.valueOf(k) + "   " + B + "   "+String.valueOf(Go.get(k).get(B)) + "\n");
                        }
                    }
                }
            }
            if(states.get(k).getLast().size()!=0){
                if(states.get(k).containAcc()){
                    Map<String,String> m = new HashMap();
                    m.put("#","acc");
                    LR_action.put(k, m);
                    System.out.print("action " + String.valueOf(k) + "   #   acc\n");
                }
                //System.out.print(states.get(k).getLast().size());
                for(int p = 0 ; p < states.get(k).getLast().size(); p ++){

                    if(findGrammar(states.get(k).getLast().get(p))!=null){

                        if(!LR_action.containsKey(k)){
                            Map<String, String> m = new HashMap<>();
                            m.put(states.get(k).getLast().get(p).getLater(),"r" + findGrammar(states.get(k).getLast().get(p)));
                            LR_action.put(k,m);
                             System.out.print("action " + String.valueOf(k) + "   " + states.get(k).getLast().get(p).getLater() +
                                    "    r" + findGrammar(states.get(k).getLast().get(p)) + "\n");
                        }
                        else if(LR_action.containsKey(k) && !LR_action.get(k).containsKey(states.get(k).getLast().get(p).getLater())){
                            Map<String, String> m = LR_action.get(k);
                            m.put(states.get(k).getLast().get(p).getLater(),"r" + findGrammar(states.get(k).getLast().get(p)));
                            LR_action.put(k,m);
                            System.out.print("action " + String.valueOf(k) + "   " + states.get(k).getLast().get(p).getLater() +
                                    "    r" + findGrammar(states.get(k).getLast().get(p)) + "\n");
                        }
                    }

                }

            }


        }
    }

    public static String findGrammar(LRproject lr){
        grammarTable gt = new grammarTable();
        gt.setLeft(lr.getGrammar().getLeft());
        String[] x = new String[lr.getGrammar().getRight().length - 1];
        for(int i = 0 ; i < lr.getGrammar().getRight().length - 1; i++)
            x[i] = lr.getGrammar().getRight()[i];
        gt.setRight(x);
        for(int i = 0 ; i < grammar.length; i++){
            if(grammar[i].equals(gt))
                return grammar[i].getName();
        }
        return null;
    }

    public static void prepare(){

        terminate = getTerminate(grammar);
        terminate.add("#");
        System.out.print("终结符为：");
        for(int i = 0; i< terminate.size(); i++)
            System.out.print(" " + terminate.get(i));
        noterminate = getNoTerminate(grammar);
        System.out.print("\n非终结符为：");
        for(int i = 0; i< noterminate.size(); i++)
            System.out.print(" " + noterminate.get(i));
        System.out.println();
        getFirst = getFirstX(grammar);
        System.out.print("first集如下："+"\n");
        printF(getFirst);
        getFollow = getFollowX(grammar);
        System.out.print("follow集如下："+"\n");
        printF(getFollow);
        augGrammar = getAugGrammar(grammar);
        System.out.print("扩展文法如下："+"\n");
        for(int i = 0 ; i < augGrammar.size(); i++)
            augGrammar.get(i).print();
        System.out.print("DFA状态闭包如下："+"\n");
        states = makeStates();
        for(int i = 0 ; i < states.size(); i ++)
            states.get(i).print();
        makeGo();

        System.out.print(Go.get(0).get("*"));

    }

    public static void makeGo(){
        Go = new HashMap<>();
        List<String> list = new ArrayList();
        list.addAll(terminate);
        list.addAll(noterminate);
        System.out.print("Go函数如下：");
        for(int i = 0 ; i < states.size(); i ++){
            for(int j = 0 ; j < list.size(); j ++){
                if(GO(states.get(i).getInfo(),list.get(j)).size()!=0){

                    int x = findState(GO(states.get(i).getInfo(),list.get(j)));
                    if(x != -1){
                        Map<String, Integer> m = new HashMap();
                        if(Go.containsKey(i))
                            m = Go.get(i);
                        m.put(list.get(j),x);
                        Go.put(i,m);


                        System.out.print("\n"+ String.valueOf(i)+"," + list.get(j) + "->  "+ String.valueOf(x));
                        System.out.print(Go.get(i).get(list.get(j)));
                    }

                }
            }
        }
        System.out.println();
    }

    public static Integer findState(List<LRproject> list){
        for(int i = 0 ; i < states.size(); i ++){
            List<LRproject> origin = states.get(i).getInfo();
            if(origin.size() == list.size()){
                int p = 0;
                for(int j = 0 ; j < list.size(); j ++){
                    if(!findLR(origin, list.get(j))){
                        p = 1;
                    }
                }
                if(p ==0)
                    return i;
            }
        }
        return -1;
    }

    public static List<state> makeStates(){
        List<List<LRproject>> result = new ArrayList<>();
        grammarTable gr = new grammarTable();
        gr.setLeft("P");
        String[] ss = new String[2];
        ss[0] = ".";
        ss[1] = grammar[0].getRight()[0];
        gr.setRight(ss);
        LRproject lr = new LRproject();
        lr.setGrammar(gr);
        lr.setLater("#");
        List<LRproject> I = new ArrayList<>();
        I.add(lr);
        result.add(Closure(I));
        List<String> s = new ArrayList<>();
        s.addAll(noterminate);
        s.addAll(terminate);
        while(1 == 1){
            int flag = 0;
            for(int i = 0; i < result.size();i++){
                List<LRproject> list = result.get(i);
                for(int j = 0; j < s.size();j ++){
                    String w = s.get(j);
                    if(GO(list,w).size()!= 0){
                        if(!findClosure(result,GO(list,w))){
                            result.add(GO(list,w));
                            flag = 1;

                        }

                    }
                }
            }

            if(flag == 0) break;
        }
        List<state> r = new ArrayList<>();
        for(int i = 0 ;  i < result.size(); i++){
            state st = new state();
            st.setId(i);
            st.setInfo(result.get(i));
            r.add(st);
        }
        return r;
    }

    public static boolean findClosure(List<List<LRproject>> C, List<LRproject> x){

        for(int i = 0; i < C.size(); i++){
            List<LRproject> c = C.get(i);
            if(c.size() == x.size()){
                int flag = 0;
                for(int j = 0; j < c.size();j++){
                    for(int k = 0; k < x.size();k++)
                        if( c.get(j).getLater().equals(x.get(k).getLater()) && c.get(j).getGrammar().equals(x.get(k).getGrammar())){
                            flag += 1;
                        }
                }
                if(flag == x.size()) return true;
            }
        }
        return false;
    }

    public static List<LRproject> GO(List<LRproject> I, String X){
        List<LRproject> J = new ArrayList<>();
        for(int i = 0 ; i < I.size(); i++){
            LRproject lr = I.get(i);
            for(int j = 0; j < lr.getGrammar().getRight().length - 1; j++){
                if(lr.getGrammar().getRight()[j + 1].equals(X) && lr.getGrammar().getRight()[j].equals(".")){
                    //X可以是非终结符
                    //lr.print();
                    LRproject lrp = new LRproject();
                    lrp.setLater(lr.getLater());
                    String[] right = new String[lr.getGrammar().getRight().length];
                    right[j] = X;
                    right[j + 1] = ".";
                    for(int kk = 0; kk < lr.getGrammar().getRight().length; kk ++){
                        if(kk != j && kk != j + 1)
                            right[kk] = lr.getGrammar().getRight()[kk];
                    }
                    grammarTable g = new grammarTable();
                    g.setLeft(lr.getGrammar().getLeft());
                    g.setRight(right);
                    lrp.setGrammar(g);
                    //g.print();
                    if(! findLR(J, lr))
                        J.add(lrp);//注意加入的是lrp而不是lr！！
                }
            }
        }
        return Closure(J);
    }

    public static List<LRproject> Closure(List<LRproject> I){
        Map<String, List<String>> getFirst = getFirstX(grammar);
        List<LRproject> C = I;

        while(1 ==1){
            int flag = 0;
            for(int i = 0; i < C.size(); i++){
                LRproject c = C.get(i);
                //c.print();
                for(int j = 0 ; j < c.getGrammar().getRight().length - 1; j++){
                    String B = c.getGrammar().getRight()[j + 1];
                    //System.out.print(c.getGrammar().getRight()[j] + B);
                    if(c.getGrammar().getRight()[j].equals(".") && getNoTerminate(grammar).contains(B)){
                        List<String> s = new ArrayList<>();
                        for(int k = j + 2; k < c.getGrammar().getRight().length; k++)
                            s.add(c.getGrammar().getRight()[k]);
                        s.add(c.getLater());
                        //System.out.print(s + "aaaaaa");
                        //System.out.print(String.valueOf(s.charAt(0)));
                        if(!s.equals("")){
                            List<String> f = getFirst.get(s.get(0));
                            for(int p = 0; p < augGrammar.size(); p++){
                                if(augGrammar.get(p).getLeft().equals(B) && augGrammar.get(p).getRight()[0].equals(".")){
                                    //System.out.print(String.valueOf(s.charAt(0)));
                                    //augGrammar.get(p).print();
                                    for(int q = 0; q < f.size(); q ++){
                                        LRproject lr = new LRproject();
                                        lr.setGrammar(augGrammar.get(p));
                                        lr.setLater(f.get(q));
                                        if(! findLR(C, lr)){
                                            C.add(lr);
                                            //System.out.print("mmmmm");
                                            //lr.getGrammar().print();
                                            flag = 1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(flag == 0) break;
        }

        return C;
    }

    public static boolean findLR(List<LRproject> a, LRproject b){
        for(int i = 0; i < a.size(); i++){
            LRproject lr = a.get(i);
            if(lr.getGrammar().equals(b.getGrammar()) && lr.getLater().equals(b.getLater()))
                return true;
        }
        return false;
    }

    public static List<grammarTable> getAugGrammar(grammarTable[] grammar){
        List<grammarTable> aug = new ArrayList<>();
        int flag = 0;
        for(int i = 0; i < grammar.length;i++){
            int num = grammar[i].getRightLength();
            for(int j = 0 ; j < num + 1; j ++){
                String[] s = new String[num + 1];
                grammarTable g = new grammarTable();
                for(int k = 0; k < num + 1; k ++){
                    if(k == j) {
                        s[k] = ".";
                    }
                    else if(k < j) s[k] = grammar[i].getRight()[k];
                    else s[k] = grammar[i].getRight()[k - 1];
                }
                g.setName(String.valueOf(flag));
                g.setLeft(grammar[i].getLeft());
                g.setRight(s);
                //g.print();
                aug.add(g);
                flag ++;
            }
        }

        return aug;
    }

    public static void printF(Map<String, List<String>> result){
        Set<String> firstS = result.keySet();
        for(int i = 0; i < firstS.size();i++){
            System.out.print(firstS.toArray()[i] + ": ");
            for(int j = 0; j< result.get(firstS.toArray()[i]).size();j++)
                System.out.print(result.get(firstS.toArray()[i]).get(j) + " ");
            System.out.println();
        }
    }

    public static Map<String, List<String>> getFirstX(grammarTable[] grammar){

        List<String> special = new ArrayList<>();
        special.add("#");

        Map<String, List<String>> result = new HashMap<>();
        result.put("#",special);
        for(int i = 0; i < getTerminate(grammar).size(); i++){
            List<String> list = new ArrayList<>();
            list.add(getTerminate(grammar).get(i));
            result.put(getTerminate(grammar).get(i),list);
        }
        for(int i = 0; i < getNoTerminate(grammar).size(); i++) {
            List<String> list = new ArrayList<>();
            for(int j = 0; j < grammar.length; j++){
                if(grammar[j].getLeft().equals(getNoTerminate(grammar).get(i)))
                    if(getTerminate(grammar).contains(grammar[j].getRight()[0]))
                        list.add(grammar[j].getRight()[0]);
            }
            result.put(getNoTerminate(grammar).get(i),list);
        }

        while (1 == 1){
            int flag = 0;
            for(int i = 0; i < getNoTerminate(grammar).size(); i++){
                String x = getNoTerminate(grammar).get(i);
                for(int j = 0; j < grammar.length; j++) {
                    if (grammar[j].getLeft().equals(x)){
                        if(getNoTerminate(grammar).contains(grammar[j].getRight()[0])){
                            String f = grammar[j].getRight()[0];
                            for(int k = 0; k < result.get(f).size(); k++){
                                if(! result.get(x).contains(result.get(f).get(k))){
                                    result.get(x).add(result.get(f).get(k));
                                    flag = 1;
                                }
                            }
                        }
                    }
                }
            }
            if(flag == 0) break;
        }
        return result;
    }

    public static Map<String, List<String>> getFollowX(grammarTable[] grammar){
        Map<String, List<String>> result = new HashMap<>();
        Map<String, List<String>> first = getFirstX(grammar);
        for(int i = 0; i < getNoTerminate(grammar).size(); i++){
            List<String> list = new ArrayList<>();
            result.put(getNoTerminate(grammar).get(i),list);
        }
        result.get("P").add("#");
        while (1 == 1){
            int flag = 0;
            for(int i = 0; i < getNoTerminate(grammar).size(); i++){
                String a = getNoTerminate(grammar).get(i);
                for(int j = 0; j < grammar.length; j++)
                    if (grammar[j].getLeft().equals(a)){
                        for(int k = 0;k < grammar[j].getRight().length - 1; k++ ){
                            String bb = grammar[j].getRight()[k];
                            String bbb = grammar[j].getRight()[k + 1];
                            if(getNoTerminate(grammar).contains(bb)){
                                List<String> list = first.get(bbb);
                                for(int p = 0; p < list.size();p++){
                                    if(!result.get(bb).contains(list.get(p))){
                                        flag = 1;
                                        result.get(bb).add(list.get(p));
                                    }
                                }
                            }
                        }
                        String b = grammar[j].getRight()[grammar[j].getRight().length - 1];
                        if(getNoTerminate(grammar).contains(b)){
                            for(int p = 0; p<result.get(a).size();p++){
                                if(!result.get(b).contains(result.get(a).get(p))){
                                    flag = 1;
                                    result.get(b).add(result.get(a).get(p));
                                }
                            }

                        }

                    }
            }
            if(flag == 0)
                break;
        }
        return result;
    }

    public static List<String> getNoTerminate(grammarTable[] grammar){
        List<String> result = new ArrayList<>();
        for(int i = 0; i < grammar.length; i ++) {
            if (!result.contains(grammar[i].getLeft())) {
                result.add(grammar[i].getLeft());
            }
        }
        return result;
    }

    public static List<String> getTerminate(grammarTable[] grammar){
        List<String> result = new ArrayList<>();
        List<String> left = new ArrayList<>();
        List<String> right = new ArrayList<>();
        for(int i = 0; i < grammar.length; i ++){
            if(!left.contains(grammar[i].getLeft())){
                left.add(grammar[i].getLeft());
            }

            for(int j = 0; j < grammar[i].getRight().length; j++){
                if(!right.contains(grammar[i].getRight()[j]))
                    right.add(grammar[i].getRight()[j]);
            }
        }
        for(int i = 0; i< right.size(); i++){
            if(!left.contains(right.get(i)))
                //if(!tagsMap.containsKey(right.get(i)))
                    result.add(right.get(i));

        }
        return result;
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
