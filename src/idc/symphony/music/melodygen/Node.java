package idc.symphony.music.melodygen;

import org.jfugue.pattern.Pattern;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {

    HammingWhole hammingWhole;
    ArrayList<Node> forward; // Nodes further from the starting node of the graph
    ArrayList<Node> backward; // Nodes closer to the starting node of the graph
    ArrayList<Node> side; // Nodes with the same distance to the starting node

    public Node(HammingWhole hammingWhole) {
        this.hammingWhole = hammingWhole;
        forward = new ArrayList<>();
        backward = new ArrayList<>();
        side = new ArrayList<>();
    }

    public Node(Node other) {
        this.hammingWhole = other.hammingWhole;
        forward = other.forward;
        backward = other.backward;
        side = other.side;
    }

    @Override
    public boolean equals(Object other) {

        if (other == this) return true;

        if (!(other instanceof Node)) return false;

        Node othr = (Node)other;

        return hammingWhole.equals(othr.hammingWhole);
    }

    public String toString() {
        return hammingWhole.toString();
    }

    public Pattern toPattern() {
        return hammingWhole.toPattern();
    }

    HashMap<Node, Integer> cachedDistances;
    public int distance(Node other) {
        if (cachedDistances == null) {
            cachedDistances = new HashMap<>();
        }
        if (!cachedDistances.containsKey(other)) {
            int distCalc = hammingWhole.distance(other.hammingWhole);
            cachedDistances.put(other, distCalc);
        }
        return cachedDistances.get(other);
    }

    public boolean hasForward() {
        return forward.size() > 0;
    }

    public boolean hasSide() {
        return side.size() > 0;
    }

    public boolean hasBackward() {
        return backward.size() > 0;
    }

}
