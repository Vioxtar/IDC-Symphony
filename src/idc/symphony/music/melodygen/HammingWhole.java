package idc.symphony.music.melodygen;

import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.Token;
import org.jfugue.theory.Note;

import java.util.*;

public class HammingWhole {
    // Old implementation
    {
//    Byte octave; // The octave
//    int level; // Level is the hamming distance to the zero HammingWhole
//    int notesCount; // The number of notes in this whole
//    float soundDensity; // The sum of durations in this whole
//
//
//
//    /**
//     * Every note n is an integer from 1 to m, where m is the number of usable notes (relevant to the
//     * graph builder alone). Every note n must be in [0, m], where n = 0 is the rest note.
//     * For every duration d, its corresponding note n will have a final duration of 1/d wholes.
//     * It thus must be that if we were to iterate on all of d's, their sum of 1/d would equal 1 (whole).
//     */
//    LinkedList<MelNote> notes; // The notes
//
//
//    ArrayList<HammingWhole> prev; // All WholeNodes with distance 1 whose level is below ours
//    ArrayList<HammingWhole> next; // All WholeNodes with distance 1 whose level is above ours
//
//
//    /**
//     * Create a copy of an existing HammingWhole.
//     * @param other
//     */
//    public HammingWhole(HammingWhole other) {
//        this.octave = other.octave;
//        this.level = other.level;
//        this.notesCount = other.notesCount;
//        this.soundDensity = other.soundDensity;
//
//        this.notes = new LinkedList<>();
//        ListIterator<MelNote> it = other.notes.listIterator();
//        while (it.hasNext()) {
//            MelNote melNote = it.next();
//            MelNote newMelNote = new MelNote(melNote);
//            this.notes.addLast(newMelNote);
//        }
//    }
//
//    public int distance(HammingWhole other) {
//        int dist = 0;
//
//        HammingWhole ourCopy = new HammingWhole(this);
//        HammingWhole thrCopy = new HammingWhole(other);
//
//        ListIterator<MelNote> ourIt = ourCopy.notes.listIterator();
//        ListIterator<MelNote> thrIt = thrCopy.notes.listIterator();
//
//        while (ourIt.hasNext() || thrIt.hasNext()) {
//            // We assume these notes have the same starting point
//            MelNote ourMelNote = ourIt.next();
//            MelNote thrMelNote = thrIt.next();
//
//            // Do they have equal durations?
//            Byte ourDur = ourMelNote.duration;
//            Byte thrDur = thrMelNote.duration;
//
//            if (ourDur.equals(thrDur)) {
//                // The notes have an equal duration, compare their notes
//                dist += noteDistance(ourMelNote.note, thrMelNote.note);
//
//            } else if (ourDur > thrDur) {
//                // Call the private 'overlaying' function with ourDur as the over-rider (pass the two its)
//            } else { // ourDur < thrDur
//                // Same here, but thrDur being the over-rider
//            }
//        }
//
//        return dist;
//    }
//
//    private int overlayDifference(MelNote top, ListIterator<MelNote> bottomIt) {
//        int cost = 0;
//
//        // We assume the bottom iterator's next already pointing at the first note whose starting point equals the top's
//        int durToBePassed = top.duration;
//        int durPassed = 0;
//
//        // Move the iterator one step backwards so we get the first bottom node that caused the call
//        bottomIt.previous();
//
//        while(durPassed < durToBePassed) {
//
//        }
//
//        if (durPassed > durToBePassed) {
//            // We're essentially cutting up the next bottom node
//        }
//
//        return cost;
//    }
//
//    private int noteDistance(Byte A, Byte B) {
//        if (A.equals(B)) { return 0; } // The notes are equal, distance is 0
//        if (A == -1 || B == -1) { return 1; } // One of the notes is a rest, distance is 1
//        return Math.abs(A - B); // Both notes aren't rest, return their difference
//    }
}

    // New implementation
    int[] noteValues;
    boolean[] isNewNote;
    private int octave;

    Map<Integer, Note> notesMap;
    int slotsInWhole = 32;
    int maxNoteValue;
    int minNoteValue;


    /**
     * Construct a new HammingWhole from a JFugue Pattern.
     * @param pattern
     */
    public HammingWhole(int complexity, Pattern pattern, Map<Integer, Note> notesMap, int octave) {

        this.slotsInWhole = complexity;
        this.notesMap = notesMap;
        this.octave = octave;

        noteValues = new int[slotsInWhole];
        isNewNote = new boolean[slotsInWhole];

        List<Token> tokens = pattern.getTokens();

        int slot = 0;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() != Token.TokenType.NOTE) {
                continue;
            }

            // Obtain our note
            Note newNote = new Note(token.toString());
            Byte notePos = newNote.getPositionInOctave();
            int newNoteKey = 0;
            if (!newNote.isRest()) {
                // Look for its corresponding value
                for (Map.Entry<Integer, Note> entry : notesMap.entrySet()) {
                    Integer potentialKey = entry.getKey();
                    Note mapNote = entry.getValue();
                    if (notePos.equals(mapNote.getPositionInOctave())) {
                        newNoteKey = potentialKey;
                        newNote.setValue(mapNote.getValue()); // Reset octave, we'll use our own
                    }
                }
            }

            double newNoteDur = newNote.getDuration();
            int slotsToFill = (int)Math.round(newNoteDur * slotsInWhole);

            for (int s = 0; s < slotsToFill; s++) {
                if (slot + s >= slotsInWhole) break;
                noteValues[slot + s] = newNoteKey;
            }

            slot += slotsToFill;

            if (slot < slotsInWhole) {
                isNewNote[slot] = true;
            }

        }

        // Find the maximum note value (to be used as a clamper)
        maxNoteValue = 0;
        for (Integer noteVal : notesMap.keySet()) {
            if (noteVal > maxNoteValue) {
                maxNoteValue = noteVal;
            }
        }

        // MinNoteValue is always the negative of the max note value, to signify rests
        minNoteValue = -maxNoteValue;

    }

    /**
     * Construct a new HammingWhole by copying another.
     * @param other
     */
    public HammingWhole(HammingWhole other) {
        // Copy the other
        slotsInWhole = other.slotsInWhole;

        noteValues = new int[slotsInWhole];
        isNewNote = new boolean[slotsInWhole];

        for (int i = 0; i < slotsInWhole; i++) {
            noteValues[i] = other.noteValues[i];
            isNewNote[i] = (i == 0) || other.isNewNote[i];
        }

        octave = other.octave;

        maxNoteValue = other.maxNoteValue;
        minNoteValue = other.minNoteValue;
        notesMap = other.notesMap;
    }

    /**
     * Returns the JFugue pattern represented by this node.
     * @return
     */
    Pattern cachedPattern;
    public Pattern toPattern() {

        if (cachedPattern == null) {
            cachedPattern = new Pattern();
            Note note = null;
            double dur = 0;
            for (int i = 0; i < slotsInWhole; i++) {
                if (isNewNote(i)) {
                    if (note != null) {
                        note.setDuration(dur / slotsInWhole);
                        cachedPattern.add(note);
                    }

                    dur = 0;

                    int noteVal = noteValues[i];
                    if (isRestVal(noteVal)) {
                        note = Note.createRest(0);
                    } else {
                        // Set the octave and create a new note
                        byte newVal = notesMap.get(noteVal).getPositionInOctave();
                        note = new Note((byte)(octave * 12 + newVal));
                    }
                }

                // Sum the duration
                dur++;
            }

            // Add the last note
            if (note != null) {
                note.setDuration(dur / slotsInWhole);
                cachedPattern.add(note);
            }
        }
        return cachedPattern;
    }

    /**
     * Returns the 'Hamming' distance from this node to another.
     * @param other
     * @return
     */
    public int distance(HammingWhole other) {
        int notesValDiffSum = 0;
        int notesCntDiffSum = 0;
        int octaveDiff = Math.abs(octave - other.octave); // TODO: Should we zerofy this if we're all rest?
        for (int i = 0; i < slotsInWhole; i++) {
            int valDiff = noteValDifference(noteValues[i], other.noteValues[i]);
            boolean ourNewNote = isNewNote(i);
            boolean thrNewNote = other.isNewNote(i);

            if (ourNewNote || thrNewNote) {
                notesValDiffSum += valDiff;
            }

            if (ourNewNote != thrNewNote) {
                notesCntDiffSum++;
            }
        }
        return notesValDiffSum + notesCntDiffSum + octaveDiff;
    }

    public boolean isRestVal(int noteVal) {
        return noteVal < 0;
    }

    /**
     * Distance helper function.
     * @param index
     * @return
     */
    public boolean isNewNote(int index) {
        if (index == 0) return true;
        if (isNewNote[index]) return true;
        if (index > 0) {
            if (noteValues[index] != noteValues[index - 1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Distance helper function.
     * @param a
     * @param b
     * @return
     */
    private int noteValDifference(int a, int b) {
        if (a == b) { return 0; } // The notes are equal, distance is 0
        if (isRestVal(a) || isRestVal(b)) { return 1; } // One of the notes is a rest, distance is 1
        return Math.abs(a - b); // Both notes aren't rest, return their difference
    }

    private int getLeftIndexOfNote(int middleIndex) {
        int leftIndex = middleIndex;
        // Stretch the left index...
        while (!isNewNote(leftIndex)) {
            leftIndex--;
        }
        return leftIndex;
    }

    private int getRightIndexOfNote(int middleIndex) {
        int rightIndex = middleIndex;
        // Stretch the right index...
        while (rightIndex + 1 < slotsInWhole && !isNewNote(rightIndex + 1)) {
            rightIndex++;
        }
        return rightIndex;
    }

    public void restifyNote(int indexInNote) {
        int leftIndex = getLeftIndexOfNote(indexInNote);
        int rightIndex = getRightIndexOfNote(indexInNote);
        restifyRange(leftIndex, rightIndex);
    }

    public void unRestifyNote(int indexInNote) {
        int leftIndex = getLeftIndexOfNote(indexInNote);
        int rightIndex = getRightIndexOfNote(indexInNote);
        unRestifyRange(leftIndex, rightIndex);
    }

    public void splitNote(int index) {
        isNewNote[index] = true;
    }

    public void mergeNotes(int indexInLeftNode, int indexInRightNode) {
        int leftToRight = getRightIndexOfNote(indexInLeftNode);
        int rightToLeft = getLeftIndexOfNote(indexInRightNode);

        // Only merge if we're adjacent...
        if (rightToLeft - 1 != leftToRight) {
            return;
        }

        // Only merge if we're the same value
        if (noteValues[rightToLeft] != noteValues[leftToRight]) {
            return;
        }

        isNewNote[rightToLeft] = false;
    }

    public void incrementNote(int indexInNote, int add) {
        int leftIndex = getLeftIndexOfNote(indexInNote);
        int rightIndex = getRightIndexOfNote(indexInNote);

        for (int i = leftIndex; i <= rightIndex; i++) {

            int noteVal = noteValues[i];
            boolean wasRest = isRestVal(noteVal);

            if (wasRest) {
                noteVal = unRestifyVal(noteVal);
            }

            noteVal = Utils.clamp(noteVal + add, 0, maxNoteValue);

            if (wasRest) {
                noteVal = restifyVal(noteVal);
            }

            noteValues[i] = noteVal;

        }
    }

    private void restifyRange(int startIndex, int endIndex) {

        int left = startIndex;
        int right = endIndex;

        if (startIndex > endIndex) {
            left = endIndex;
            right = startIndex;
        }

        for (int i = left; i <= right; i++) {
            if (0 > i || i >= slotsInWhole) {
                continue;
            }
            noteValues[i] = restifyVal(noteValues[i]);
        }
    }

    private void unRestifyRange(int startIndex, int endIndex) {

        int left = startIndex;
        int right = endIndex;

        if (startIndex > endIndex) {
            left = endIndex;
            right = startIndex;
        }

        for (int i = left; i <= right; i++) {
            if (0 > i || i >= slotsInWhole) {
                continue;
            }
            noteValues[i] = unRestifyVal(noteValues[i]);
        }
    }

    private int restifyVal(int val) {
        if (isRestVal(val)) { return val; }
        return val * -1;
    }

    private int unRestifyVal(int val) {
        if (!isRestVal(val)) { return val; }
        return val * -1;
    }

    int maxOctave = 7;
    int minOctave = 3;
    public void addOctave(int add) {
        octave = Utils.clamp(octave + add, minOctave, maxOctave);
    }

    @Override
    public boolean equals(Object other) {

        if (other == this) return true;

        if (!(other instanceof HammingWhole)) return false;

        HammingWhole othr = (HammingWhole)other;

        if (slotsInWhole != othr.slotsInWhole) return false;
        for (int i = 0; i < slotsInWhole; i++) {
            if (noteValues[i] != othr.noteValues[i]) return false;
            if (isNewNote(i) != othr.isNewNote(i)) return false;
        }

        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < slotsInWhole; i++) {
            if (i > 0 && isNewNote(i)) {
                sb.append(" ");
            }
            sb.append(noteValues[i]);
        }
        return sb.toString();
    }
}






























