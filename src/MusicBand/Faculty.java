package MusicBand;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

import java.util.ArrayList;
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

    public static float toNoteDur(int t) {
        return (float)t / 128;
    }

    public static List<Note> getKeyNotes(Key key) {
        return key.getScale().getIntervals().setRoot(key.getRoot()).getNotes();
    }

    public static Note getRandomKeyNote(List<Note> keyNotes) {
        return keyNotes.get(ranRange(0, keyNotes.size() - 1));
    }

    public static Note getRandomKeyNote(Key key) {
        return getRandomKeyNote(getKeyNotes(key));
    }

    public static Note getCloseNote(List<Note> list, Note currNote, int distFromCurr) {
        int currIndex = list.indexOf(currNote);
        if (currIndex == -1) {
            // We don't have the key, return a random one from the list
            return getRandomKeyNote(list);
        }

        int offSet = ranRange(-distFromCurr, distFromCurr);
        int closeIndex = currIndex + offSet;

        // Clamp so we're within list index range
        closeIndex = closeIndex % (list.size() - 1);
        return list.get(closeIndex);
    }

//    Random gen = new Random();

    public static int ranRange(int a, int b){
        if (a == b) {
            return a;
        }
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        double ranBase = Math.random();
        int diff = max - min + 1;
        int ran = (int)((ranBase * diff) + min);
        return ran;
    }

    public static double ranRange(double a, double b){
        if (a == b) {
            return a;
        }
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        double ranBase = Math.random();
        double diff = max - min;
        double ran = (ranBase * diff) + min;
        return ran;
    }

}
