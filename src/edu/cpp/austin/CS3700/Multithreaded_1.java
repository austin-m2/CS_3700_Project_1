package edu.cpp.austin.CS3700;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.PriorityQueue;
import java.util.concurrent.*;

public class Multithreaded_1 {

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
    private static ArrayList<ArrayList> encode(BufferedReader br, File file) throws IOException {
        long startTime = System.currentTimeMillis();


        int[] frequency = findFrequency(br);
        Node treeRoot = buildTree(frequency);

        //build code table
        String[] codeTable = new String[256];
        buildCodeTable(codeTable, treeRoot, "");

        //reset buffered reader
        br = new BufferedReader(new FileReader(file));


        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        ArrayList<Future<ArrayList<BitSetPlusLength>>> outputList = new ArrayList<>();

        String currentLine;

        while ((currentLine = br.readLine()) != null) {
            Encoder encoder = new Encoder(currentLine, codeTable);
            Future<ArrayList<BitSetPlusLength>> oneLineCodes= executor.submit(encoder);
            outputList.add(oneLineCodes);

        }

        ArrayList<ArrayList> finalList = new ArrayList<>();
        for (Future<ArrayList<BitSetPlusLength>> future : outputList) {
            try {
                finalList.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        long totalTimeMillis = System.currentTimeMillis() - startTime;
        System.out.println(totalTimeMillis + " milliseconds");

        executor.shutdown();

        return finalList;

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

class Encoder implements Callable<ArrayList<BitSetPlusLength>> {
    private String line;
    //private int outputIndex;
    String[] codeTable;


    Encoder(String l, String[] codeTable) {
        line = l;
        this.codeTable = codeTable;
    }

    @Override
    public ArrayList<BitSetPlusLength> call() throws Exception {

        ArrayList<BitSetPlusLength> bitSetPlusLengthList= new ArrayList<>();
        int i, j;
        for (i = 0; i < line.length(); i++) {
            String code = codeTable[line.charAt(i)];
            BitSet bitSetCode = new BitSet();
            for (j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '1') {
                    bitSetCode.set(j);
                }
            }
            BitSetPlusLength list = new BitSetPlusLength();
            list.bitSet = bitSetCode;
            list.numChars = i;
            bitSetPlusLengthList.add(list);
        }

        return bitSetPlusLengthList;
    }
}

class BitSetPlusLength {
    public BitSet bitSet;
    public int numChars;
}