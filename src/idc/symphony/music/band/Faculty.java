package idc.symphony.music.band;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

import java.util.List;
import java.util.Random;

public class Faculty implements BandMember {

    // Overrides - to be implemented by each child

    public Pattern playMainMelody(int t, Key key) {
        return null;
    }

    public Pattern playSecondary(int t, Key key) {
        return null;
    }

    public Pattern playCarpet3(int t, Key key) {
        return null;
    }

    public Pattern playCarpet2(int t, Key key) {
        return null;
    }

    public Pattern playCarpet1(int t, Key key) {
        return null;
    }

    public Pattern playMainMelody(int t) {
        return null;
    }

    public Pattern playSecondary(int t) {
        return null;
    }

    public Pattern playCarpet3(int t) {
        return null;
    }

    public Pattern playCarpet2(int t) {
        return null;
    }

    public Pattern playCarpet1(int t) {
        return null;
    }




    // Auxiliary functions
    public static int toWholes(int t) {
        return t / 128;
    }

    public static int toHalves(int t) {
        return t / 64;
    }

    public static int toQuarters(int t) {
        return t / 32;
    }

    public static int toEighths(int t) {
        return t / 16;
    }

    public static int toSixteenths(int t) {
        return t / 8;
    }

    public static int toThirtySeconds(int t) {
        return t / 4;
    }

    public static int toSixtyFourths(int t) {
        return t / 2;
    }

    public static int toOneTwentyEights(int t) {
        return t; // :D
    }

    /**
     * Returns the note-duration of a given note given by a number of 128ths.
     * @param t
     * @return
     */
    public static float toNoteDur(int t) {
        return (float)t / 128;
    }

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

    // Random functionality
    Random gen = new Random();
    public void setRanSeed(long seed) {
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
