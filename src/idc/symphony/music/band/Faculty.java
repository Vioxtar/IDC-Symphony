package idc.symphony.music.band;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

import java.util.*;

public class Faculty implements BandMember {

    /*************************************************************************************************
     * Overrides - to be re-implemented by each child
     *************************************************************************************************/

    public Pattern playMainMelody(int wholes, Key key) {
        return null;
    }

    public Pattern playSecondary(int wholes, Key key) {
        return null;
    }

    public Pattern playCarpet3(int wholes, Key key) {
        return null;
    }

    public Pattern playCarpet2(int wholes, Key key) {
        return null;
    }

    public Pattern playCarpet1(int wholes, Key key) {
        return null;
    }

    /*************************************************************************************************
     * Durations functionality
     *************************************************************************************************/

    /**
     * Randomly divides a duration of x wholes to sequence of smaller durations.
     * @param wholes
     * @param numOfNotes
     * @return an array of floats containing note durations
     */
    public float[] divideDuration(int wholes, int numOfNotes) {
        if (wholes <= 0 || numOfNotes < 1) {
            return null;
        }

        ArrayList<Character> durs = new ArrayList<>();
        for (int i = 1; i <= wholes; i++) {
            durs.add('w');
        }

        ArrayList<Character> dividedDurs = divDur(durs, numOfNotes);
        Collections.shuffle(dividedDurs, gen);

        // Add room to add seperators
        ArrayList<Integer> seps = new ArrayList<>();
        HashMap<Integer, Character> expanded = new HashMap<>();

        for (int i = 0; i < dividedDurs.size(); i++) {
            char c = dividedDurs.get(i);
            expanded.put(i * 2, c);
            if (i > 0) { seps.add(i * 2 - 1); }
        }

        // Add seperators
        int sepsLeft = Math.min(dividedDurs.size(), numOfNotes) - 1;
        while (sepsLeft > 0) {
            int sepToUse = ranRange(0, seps.size() - 1);
            int sepIndex = seps.get(sepToUse);
            seps.remove(sepToUse);
            expanded.put(sepIndex, ',');
            sepsLeft--;
        }

        // Finally, parse the output and return it as an array
        float[] output = new float[numOfNotes];
        int i = 0;
        for (Character c : expanded.values()) {
            double t = 1f;
            switch (c) {
                case 'w': t = 1f; break;
                case 'h': t /= 2; break;
                case 'q': t /= 4; break;
                case 'i': t /= 8; break;
                case ',': i++; break;
            }
            output[i] += t;
            if (i >= output.length) { break; }
        }

        return output;

    }

    private ArrayList<Character> divDur(ArrayList<Character> durs, int numNotes) {
        // We did our dividing, stop the recursion and return our results
        if (numNotes <= 1) {
            return durs;
        }

        // Which indexes can we divide?
        ArrayList<Integer> divisibleIndexes = new ArrayList<>();
        boolean canDivide = false;
        for (int i = 0; i < durs.size(); i++) {
            char c = durs.get(i);
            // We wish to divide each duration uniformly, add a simplified weight system
            // for the randomized divisible index to be divided by adding more instances
            // of each divisible index per its duration
            switch (c) {
                case 'w':
                    for (int j = 1; j <= 4; j++) { divisibleIndexes.add(i); }
                    canDivide = true;
                    break;
                case 'h':
                    for (int j = 1; j <= 2; j++) { divisibleIndexes.add(i); }
                    canDivide = true;
                    break;
                case 'q':
                    for (int j = 1; j <= 1; j++) { divisibleIndexes.add(i); }
                    canDivide = true;
                    break;
            }
        }

        if (canDivide) { // Divide a random divisible character
            int divIndx = divisibleIndexes.get(ranRange(1, divisibleIndexes.size()) - 1);
            char c = durs.get(divIndx);
            durs.remove(divIndx);
            switch (c) {
                case 'w':
                    durs.add('h'); durs.add('h'); break;
                case 'h':
                    durs.add('q'); durs.add('q'); break;
                case 'q':
                    durs.add('i'); durs.add('i'); break;
            }
            return divDur(durs, numNotes - 1);
        } else { // We can't divide anymore
            return divDur(durs, -1); // Rely on our single stop condition
        }
    }

    /*************************************************************************************************
     * Notes functionality
     *************************************************************************************************/

    /**
     * Returns a list of notes from a given key.
     * @param key
     * @return
     */
    public static List<Note> getKeyNotes(Key key) {
        return key.getScale().getIntervals().setRoot(key.getRoot()).getNotes();
    }

    /**
     * Returns a random note from a given list of key notes.
     * @param keyNotes
     * @return
     */
    public Note getRandomKeyNote(List<Note> keyNotes) {
        return keyNotes.get(ranRange(0, keyNotes.size() - 1));
    }

    /**
     * Returns a random note from a given key.
     * @param key
     * @return
     */
    public Note getRandomKeyNote(Key key) {
        return getRandomKeyNote(getKeyNotes(key));
    }

    /**
     * Given a list of notes (assumed to be sorted) and a current note,
     * returns a neighboring note within a certain distance.
     * @param list
     * @param currNote
     * @param distFromCurr
     * @return
     */
    public Note getCloseNote(List<Note> list, Note currNote, int distFromCurr) {
        int currIndex = list.indexOf(currNote);
        if (currIndex == -1) {
            // We don't have the key, return a random one from the list
            return getRandomKeyNote(list);
        }

        int offSet = ranRange(-distFromCurr, distFromCurr);
        int closeIndex = currIndex + offSet;

        // Clamp so we're within list index range
        closeIndex = Math.floorMod(closeIndex, list.size());

        return list.get(closeIndex);
    }


    /*************************************************************************************************
     * Random functionality - every faculty instance has its own random seed generator to allow for
     * semi-deterministic music composition. For semi-deterministic outputs, always use ranRange(a,b).
     *************************************************************************************************/

    Random gen = new Random();
    public void setRanSeed(long seed) {
        gen.setSeed(seed);
    }
    public void resetGen(long seed) {
        gen = new Random();
        gen.setSeed(seed);
    }

    /**
     * Returns a uniform-random integer between any two numbers.
     * @param a
     * @param b
     * @return
     */
    public int ranRange(int a, int b){
        if (a == b) {
            return a;
        }
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        double ranBase = gen.nextDouble();
        int diff = max - min + 1;
        int ran = (int)((ranBase * diff) + min);
        return ran;
    }

    /**
     * Returns a uniform-random double between any two numbers.
     * @param a
     * @param b
     * @return
     */
    public double ranRange(double a, double b){
        if (a == b) {
            return a;
        }
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        double ranBase = gen.nextDouble();
        double diff = max - min;
        double ran = (ranBase * diff) + min;
        return ran;
    }

}
