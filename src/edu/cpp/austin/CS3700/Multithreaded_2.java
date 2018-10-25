package edu.cpp.austin.CS3700;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.PriorityQueue;

public class Multithreaded_2 {

    public static int fileLengthInChars = 0;
    public static Character[] fileCharArray;

    //44.6 KB
    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("constitution.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));


        encode(br, file);



    }

    private static int[] findFrequency(BufferedReader br) throws IOException {
        //find frequency of each character
        ArrayList<Character> chars = new ArrayList<>();
        int[] frequency = new int[256];
        int currentChar = br.read();
        while (currentChar != -1) {
            chars.add((char) currentChar);
            frequency[currentChar]++;
            currentChar = br.read();
            fileLengthInChars++;
        }

        fileCharArray = new Character[fileLengthInChars];
        fileCharArray = chars.toArray(fileCharArray);

        return frequency;
    }

    private static Node buildTree(int[] frequency) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        //initialize queue
        for (int i = 0; i < frequency.length; i++) {
            if (frequency[i] > 0) {
                queue.add(new Node((char) i, frequency[i], null, null));
            }
        }

        //merge two smallest trees
        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            queue.add(parent);
        }

        return queue.poll();

    }

    //encodes the file and writes it to output.dat
    //returns the total number of characters in the file
    private static void encode(BufferedReader br, File file) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();

        int[] frequency = findFrequency(br);
        Node treeRoot = buildTree(frequency);

        //build code table
        String[] codeTable = new String[256];
        buildCodeTable(codeTable, treeRoot, "");

        //reset buffered reader
        br = new BufferedReader(new FileReader(file));

        //this string array will hold the encoded string for each char in the file
        String[] outputCodeArray = new String[fileLengthInChars];


        EncoderThread encoder1 = new EncoderThread(0, fileLengthInChars / 4, fileCharArray, outputCodeArray, codeTable);
        EncoderThread encoder2 = new EncoderThread(fileLengthInChars / 4, fileLengthInChars * 2 / 4, fileCharArray, outputCodeArray, codeTable);
        EncoderThread encoder3 = new EncoderThread(fileLengthInChars * 2 / 4, fileLengthInChars * 3 / 4, fileCharArray, outputCodeArray, codeTable);
        EncoderThread encoder4 = new EncoderThread(fileLengthInChars * 3 / 4, fileLengthInChars, fileCharArray, outputCodeArray, codeTable);

        encoder1.start();
        encoder2.start();
        encoder3.start();
        encoder4.start();

        encoder1.join();
        encoder2.join();
        encoder3.join();
        encoder4.join();

        long totalTimeMillis = System.currentTimeMillis() - startTime;
        System.out.println(totalTimeMillis + " milliseconds");
        
    }

    private static void buildCodeTable(String[] table, Node node, String code) {
        if (!node.isLeaf()) {
            buildCodeTable(table, node.left, code + '0');
            buildCodeTable(table, node.right, code + '1');
        } else {
            table[node.character] = code;
        }
    }
}

class EncoderThread extends Thread {
    int fromIndex, toIndex;
    Character[] charArray;
    String[] outputArray, codeTable;

    EncoderThread(int fromIndex, int toIndex, Character[] charArray, String[] outputArray, String[] codeTable) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.charArray = charArray;
        this.outputArray = outputArray;
        this.codeTable = codeTable;
    }

    @Override
    public void run() {
        for (int i = fromIndex; i < toIndex; i++) {
            outputArray[i] = codeTable[charArray[i]];
        }
    }
}
