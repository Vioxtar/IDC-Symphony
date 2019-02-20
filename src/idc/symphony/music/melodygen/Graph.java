package idc.symphony.music.melodygen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Graph {
//
//    HashSet<Node> allNodes;
//    Node startingNode;
//
//    public Graph(Node startingNode, long seed, int numOfSplits, double splitFallOff) {
//        // Store the starting node
//        this.startingNode = startingNode;
//
//        // Initiate our set
//        allNodes = new HashSet<>();
//
//        // Create a new random generator
//        resetGen(seed);
//
//        // Construct our alternator, have it know who its master is for semi-determinism purposes
//        Alternator alt = new Alternator(this);
//
//        // Build the graph recursively
//        recursiveBuild(startingNode, alt, numOfSplits, splitFallOff);
//
//    }
//
//
//    /**
//     * Build the graph recursively
//     * @param alt the alternator to be used
//     */
//    public void recursiveBuild(Node currNode, Alternator alt, double numOfSplits, double splitFallOff) {
//        int splits = (int)Math.ceil(numOfSplits);
//
//        // Exit if we're done
//        if (splits <= 0) return;
//
//        ArrayList<Node> newNodes = new ArrayList<>();
//
//        for (int i = 1; i <= splits; i++) {
//            Node newNode = alt.alternate(currNode);
//
//            int newCurrDist = newNode.distance(currNode);
//            if (newCurrDist == 0) {
//                // We haven't made any changes... bail
//                i--;
//                continue;
//            }
//
//            // Make sure we aren't creating the same node again :S
//            for (Node madeNode : newNodes) {
//                if (newNode.equals(madeNode)) {
//                    // We already created this split, bail
//                    i--;
//                    continue;
//                }
//            }
//
//            int newStartDist = newNode.distance(startingNode);
//            int currStartDist = currNode.distance(startingNode);
//
//            if (newStartDist > currStartDist) { // Split's level is bigger
//                currNode.right.add(newNode);
//                newNode.left.add(currNode);
//            } else if (newStartDist < currStartDist) { // Split's level is smaller
//                currNode.left.add(newNode);
//                newNode.right.add(currNode);
//            } else {
//                // This happens when the split we created is distant from its parent,
//                // but still has the same distance to the starting node (essentially
//                // a up/down neighbor)
//                i--;
//                continue;
//            }
//
//            // Register the new node (to be recursed later)
//            newNodes.add(newNode);
//        }
//
//        // Recurse
//        for (Node madeNode : newNodes) {
//            recursiveBuild(madeNode, alt, numOfSplits - splitFallOff, splitFallOff);
//
//            // Add all notes
//            allNodes.add(madeNode);
//        }
//    }

}
