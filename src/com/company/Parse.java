package com.company;
import javafx.util.Pair;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Scanner;

public class Parse {
    public ArrayList<Pair<String,String>> lexems = new ArrayList<Pair<String,String>>();
    static private ArrayList<String> marks = new ArrayList<>(){{
        add("->"); //0
        add(">=");
        add("<=");//2
        add("<>");
        add("::");
        add("=");
        add(">");//6
        add("<");
        add("*");
        add("/");//9
        add("+");
        add("-");
        add(":");//12
        add("(");
        add(")");
        add("{");//15
        add("}");
        add(",");
        add("|");//18
        add("@pre");
        add(".");

    }};
    static private ArrayList<String> reserved = new ArrayList<>(){{
       // add("@pre");
        add("and");
        add("context");
        add("body");
        add("def");
        add("derive");
        add("else");
        add("endif");
        add("endpackage");
        add("if");
        add("in");
        add("init");
        add("inv");
        add("invalid");
        add("false");
        add("let");
        add("not");
        add("null");
        add("or");
        add("package");
        add("post");
        add("pre");
        add("self");
        add("static");
        add("then");
        add("true");
        add("xor");
        add("Bag");
        add("Boolean");
        add("Collection");
        add("Integral");
        add("OclAny");
        add("OclInvalid");
        add("OclMessage");
        add("OclVoid");
        add("OrderedSet");
        add("Real");
        add("Sequence");
        add("Set");
        add("String");
        add("Tuple");
        add("UnlimitedNatural");
        //properties of object of any type
        add("oclIsKindOf");
        add("oclIsTypeOf");
        add("oclAsType");
        add("oclInState");
        //collections
        add("asSequence");
        add("sortBy");
        add("size");
        add("isEmpty");
        add("count");
        add("sum");
        add("select");
        add("reject");
        add("collect");
        add("forAll");
        add("exists");
        add("iterate");
        //set
        add("includes");
        add("union");
        add("intersection");
        add("including");
        add("excluding");
        //sequence
        add("prepend");
        add("append");
        add("at");

    }};
    public boolean isReserved(String str){//, boolean b) { // b true якщо :: перед str
       // if (b && reserved.contains(str) && reserved
        return reserved.contains(str);
    }
    FileReader f;
    public void getFileName(){
        do {
            System.out.print("Enter file name: ");
            Scanner scan = new Scanner(System.in);
            String str = scan.next();
            try{
                f = new FileReader(str);
            }catch(Exception ex){
                System.out.println("File name is not correct");
                f = null;
            };
        }while (f == null);
    }
    BufferedWriter fw;
    public void parse() throws Exception{
        File file = new File("Example.txt");
        file.createNewFile();
        fw = new BufferedWriter( new FileWriter(file));
        Scanner scan = new Scanner(f);
        String s;
        while (scan.hasNext()){
            s = scan.nextLine();
            parse2(s);
            fw.newLine();
        }
        fw.close();
    }
    public void parse2(String str) throws Exception{
        int index = str.indexOf("--");
        if (index != -1){
            String str1 = str.substring(0,index);
            parse3(str1);
            Pair<String, String> k = new Pair<>(str.substring(index), "comment");
            lexems.add(k);
            fw.write("<"+str.substring(index)+", коментар> ");
        }else {
            parse3(str);
        }
    }
    public void parse3(String str) throws Exception{
        //System.out.println(str);
        if (!str.contains("\"")) {
            parse4(str);
        }
        else {
            int begin = str.indexOf("\"");
            int end = -1;
            int i = begin + 1;
            while (i < str.length() && str.charAt(i) != '"')
                ++i;
            end = i;
            parse4(str.substring(0, begin));
            Pair<String, String> k = new Pair<>(str.substring(begin, end + 1), "string");
            lexems.add(k);
            fw.write("<" + str.substring(begin, end + 1) + ", рядкова константа> ");
            parse3(str.substring(end + 1));
        }
    }
    public void parse4(String str) throws Exception{
        for (int i=0; i<marks.size()-1; i++) {
            int begin = str.indexOf(marks.get(i));
            if (begin != -1) {
                parse4(str.substring(0, begin));
                if (marks.get(i).equals("@pre")) {
                    Pair<String, String> k = new Pair<>(str.substring(begin, begin + marks.get(i).length()), "reserved");
                    lexems.add(k);
                    fw.write("< " + str.substring(begin, begin + marks.get(i).length()) + " , зарезервоване слово> ");
                }
                else {
                    Pair<String, String> k = new Pair<>(str.substring(begin, begin + marks.get(i).length()), "mark");
                    lexems.add(k);
                    fw.write("< " + str.substring(begin, begin + marks.get(i).length()) + " , розділовий знак> ");
                }
                parse4(str.substring(begin+marks.get(i).length()));
                return;
            }
        }
        parse5(str);
    }
    public void parse5(String str) throws Exception{
        str = str.trim();
        String[] s = str.split(" ");
        for (String st: s){
            if (!st.equals("")) {
                if (isReserved(st)){
                    Pair<String, String> k = new Pair<>(st, "reserved");
                    lexems.add(k);
                    fw.write("<" + st + ", зарезервоване слово> ");
                }
                else if (isInt(st)){
                    Pair<String, String> k = new Pair<>(st, "int");
                    lexems.add(k);
                    fw.write("<" + st + ", ціле число> ");
                }
                else if (isReal(st)){
                    Pair<String, String> k = new Pair<>(st, "double");
                    lexems.add(k);
                    fw.write("<" + st + ", раціональне число> ");
                }
                else if (isIdent(st)){
                        while (st.contains(".")){
                            int k = st.indexOf('.');
                            String t = st.substring(0,k);
                            if (reserved.contains(t)) {
                                Pair<String, String> pair = new Pair<>(t, "reserved");
                                lexems.add(pair);
                                fw.write("<" + t + ", зарезервоване слово>");
                            }
                            else {
                                Pair<String, String> pair = new Pair<>(t, "reserved");
                                lexems.add(pair);
                                fw.write("<" + t + ", ідентифікатор> ");
                            }
                            Pair<String, String> pair = new Pair<>(".", "mark");
                            lexems.add(pair);
                            fw.write("< . , розділовий знак> ");
                            st = st.substring(k+1);
                        }
                        if (reserved.contains(st)) {
                            Pair<String, String> k = new Pair<>(st, "reserved");
                            lexems.add(k);
                            fw.write("<" + st + ", зарезервоване слово>");
                        }
                        else {
                            Pair<String, String> k = new Pair<>(st, "ident");
                            lexems.add(k);
                            fw.write("<" + st + ", ідентифікатор> ");
                        }
                }
                else {System.out.println( st + "Errror");}
            }
        }

    }
    public boolean isInt(String str){
        String num = "0123456789";
        int i=0;
        while (i<str.length() && num.indexOf(str.charAt(i)) != -1)
            i++;
        return  i == str.length();
    }
    public boolean isReal(String str){
        String num = "0123456789";
        int i=0;
        boolean flag = false;
        while ( i<str.length() && (num.indexOf(str.charAt(i))!=-1 || str.charAt(i)=='.')) {
            if (str.charAt(i) == '.') {
                if (flag) return false;
                flag = true;
            }
            i++;
        }
        return  i == str.length();

    }
    public boolean isIdent(String str){
        if (str.charAt(0)>='a' && str.charAt(0)<='z' || str.charAt(0)>='A' && str.charAt(0)<='Z' || str.charAt(0)=='_')
            return true;
        return false;
    }




    public void outLexems(){
        for (int i=0; i<lexems.size(); i++){
            Pair<String, String> m = lexems.get(i);
            System.out.println(m.getKey()+" "+m.getValue());
        }
    }
}
