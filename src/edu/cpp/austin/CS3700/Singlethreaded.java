package edu.cpp.austin.CS3700;

import java.io.*;
import java.util.BitSet;
import java.util.PriorityQueue;

public class Singlethreaded {

    //44.6 KB
    public static void main(String[] args) throws IOException {
        File file = new File("constitution.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));


        encode(br, file);

        
        
    }

    private static int[] findFrequency(BufferedReader br) throws IOException {
        //find frequency of each character
        int[] frequency = new int[256];
        int currentChar = br.read();
        while (currentChar != -1) {
            frequency[currentChar]++;
            currentChar = br.read();
        }
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
    private static int encode(BufferedReader br, File file) throws IOException {
        int[] frequency = findFrequency(br);
        Node treeRoot = buildTree(frequency);

        //build code table
        String[] codeTable = new String[256];
        buildCodeTable(codeTable, treeRoot, "");
        
        //reset buffered reader
        br = new BufferedReader(new FileReader(file));

        //encode file
        BitSet outputBitSet = new BitSet();
        int bitSetIndex = 0;
        int currentChar = br.read();
        while (currentChar != -1) {
            String code = codeTable[currentChar];
            for (int i = 0; i < code.length(); i++) {
                if (code.charAt(i) == '1') {
                    outputBitSet.set(bitSetIndex);
                }
                bitSetIndex++;
            }
            currentChar = br.read();
        }

       

        byte[] outputByteArray = outputBitSet.toByteArray();
        FileOutputStream stream = new FileOutputStream("output.dat");
        stream.write(outputByteArray);
        return bitSetIndex;
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
