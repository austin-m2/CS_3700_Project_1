package edu.cpp.austin.CS3700;

public class Node implements Comparable<Node>{

    public final char character;
    public final int frequency;
    public final Node left, right;

    Node(char ch, int freq, Node left, Node right) {
        character = ch;
        frequency = freq;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        assert ((left == null) && (right == null)) || ((left != null) && (right != null));
        return (left == null) && (right == null);
    }

    @Override
    public int compareTo(Node otherNode) {
        return this.frequency - otherNode.frequency;
    }
}
