package edu.cpp.austin.CS3700;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.PriorityQueue;

public class Main {

    //44.6 KB
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("constitution.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        PriorityQueue queue = new PriorityQueue();

        //find frequency of each character
        int[] frequency = new int[256];
        
        
        
    }
}
