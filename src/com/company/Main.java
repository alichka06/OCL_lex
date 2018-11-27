package com.company;

import java.io.File;
import java.io.FileWriter;

public class Main {

    public static void main(String[] args) {
		Parse parse = new Parse();
	    parse.getFileName();
	    try {
	        parse.parse();
	        parse.outLexems();
        } catch (Exception ex){
	        System.out.println("Eror");
        }
    }
}
