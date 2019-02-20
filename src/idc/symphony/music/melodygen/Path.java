package idc.symphony.music.melodygen;

import org.jfugue.pattern.Pattern;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Path {

    LinkedList<Node> nodes;
    Node startingNode;
    RanGen gen;
    Node currNode;

    public Path() {
        nodes = new LinkedList<>();
    }

    public Path(Path other) {
        nodes = new LinkedList<>();

        // Copy an existing path
        ListIterator it = other.nodes.listIterator();
        while (it.hasNext()) {
            Node n = (Node)it.next();
            Node newNode = new Node(n);
            nodes.addLast(newNode);
        }

        currNode = other.currNode;
    }

    public void setStartingNode(Node startNode) {
        startingNode = startNode;
    }

    public void setCurrNode(Node node) { currNode = node; }

    public void setRanGen(RanGen gen) {
        this.gen = gen;
    }

    public void setSeed(long seed) {
        if (gen == null) {
            gen = new RanGen(seed);
        } else {
            gen.resetGen(seed);
        }
    }

    public void traverseWithStyle(Node startNode, PathStyle style, long seed, int toCollect) {
        setStartingNode(startNode);
        setCurrNode(startNode);
        setSeed(seed);

        style.setParentPath(this);

        int newTargetSize = getSize() + toCollect;
        while (getSize() < newTargetSize) {
            style.apply();
        }
    }

    public int getSize() {
        return nodes.size();
    }

    public Pattern toPattern() {

        Pattern p = new Pattern();

        ListIterator it = nodes.listIterator();
        while (it.hasNext()) {
            Node node = (Node)it.next();
            p.add(node.toPattern());
        }

        return p;
    }

    public boolean hasExistingNode(ArrayList<Node> nodes, Node node) {
        for (Node n : nodes) {
            if (n.equals(node)) return true;
        }
        return false;
    }

    public boolean extendNodeRandomly(Node node) {
        Alternator alt = new Alternator(gen);

        Node newNode = alt.alternate(node);

        int newCurrDist = newNode.distance(node);
        if (newCurrDist == 0) {
            return false; // Extension failed...
        }

        int newStartDist = newNode.distance(startingNode);
        int currStartDist = node.distance(startingNode);

        if (newStartDist > currStartDist) { // Extension's level is bigger
            if (hasExistingNode(node.forward, newNode)) return false;
            node.forward.add(newNode);
            newNode.backward.add(node);
        } else if (newStartDist < currStartDist) { // Extension's level is smaller
            if (hasExistingNode(node.backward, newNode)) return false;
            node.backward.add(newNode);
            newNode.forward.add(node);
        } else { // Extension level is equal
            if (hasExistingNode(node.side, newNode)) return false;
            node.side.add(newNode);
            newNode.side.add(node);
        }

        return true;
    }

    public boolean extendNodeRandomlyForward(Node node) {
        Alternator alt = new Alternator(gen);

        Node newNode = alt.alternate(node);

        int newCurrDist = newNode.distance(node);
        if (newCurrDist == 0) {
            return false; // Extension failed...
        }

        int newStartDist = newNode.distance(startingNode);
        int currStartDist = node.distance(startingNode);

        if (newStartDist > currStartDist) { // Extension's level is bigger
            if (hasExistingNode(node.forward, newNode)) return false;
            node.forward.add(newNode);
            newNode.backward.add(node);
        } else {
            return false;
        }

        return true;
    }

    public boolean extendNodeRandomlyBackward(Node node) {
        Alternator alt = new Alternator(gen);

        Node newNode = alt.alternate(node);

        int newCurrDist = newNode.distance(node);
        if (newCurrDist == 0) {
            return false; // Extension failed...
        }

        int newStartDist = newNode.distance(startingNode);
        int currStartDist = node.distance(startingNode);

        if (newStartDist < currStartDist) { // Extension's level is smaller
            if (hasExistingNode(node.backward, newNode)) return false;
            node.backward.add(newNode);
            newNode.forward.add(node);
        } else {
            return false;
        }

        return true;
    }

    public boolean extendNodeRandomlySide(Node node) {
        Alternator alt = new Alternator(gen);

        Node newNode = alt.alternate(node);

        int newCurrDist = newNode.distance(node);
        if (newCurrDist == 0) {
            return false; // Extension failed...
        }

        int newStartDist = newNode.distance(startingNode);
        int currStartDist = node.distance(startingNode);

        if (newStartDist == currStartDist) { // Extension's level is the same
            if (hasExistingNode(node.side, newNode)) return false;
            node.side.add(newNode);
            newNode.side.add(node);
        } else {
            return false;
        }

        return true;
    }


    public Node getLast() {
        return nodes.getLast();
    }

    public Node getCurrent() {
        return currNode;
    }

    public void collectNode() {
        nodes.addLast(currNode);
    }

    public void goToStartingNode() {
        currNode = startingNode;
    }

    public boolean attemptGoForward() {
//        if (!currNode.hasForward()) {
//            extendNodeRandomlyForward(currNode);
//        }
//        if (!currNode.hasForward()) {
//            return false;
//        }
        extendNodeRandomlyForward(currNode);
        if (!currNode.hasForward()) return false;
        currNode = currNode.forward.get(gen.ranRange(0, currNode.forward.size() - 1));
        return true;
    }

    public boolean attemptGoBackward() {
//        if (!currNode.hasForward()) {
//            extendNodeRandomlyForward(currNode);
//        }
//        if (!currNode.hasForward()) {
//            return false;
//        }
        extendNodeRandomlyBackward(currNode);
        if (!currNode.hasBackward()) return false;
        currNode = currNode.backward.get(gen.ranRange(0, currNode.backward.size() - 1));
        return true;
    }

    public boolean attemptGoSide() {
//        if (!currNode.hasForward()) {
//            extendNodeRandomlyForward(currNode);
//        }
//        if (!currNode.hasForward()) {
//            return false;
//        }
        extendNodeRandomlySide(currNode);
        if (!currNode.hasSide()) return false;
        currNode = currNode.side.get(gen.ranRange(0, currNode.side.size() - 1));
        return true;
    }




}
