package idc.symphony.music.melodygen;

import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.Token;
import org.jfugue.theory.Note;

import java.util.*;

public class WholeNode {
    // Old implementation
    {
//    Byte octave; // The octave
//    int level; // Level is the hamming distance to the zero WholeNode
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
//    ArrayList<WholeNode> prev; // All WholeNodes with distance 1 whose level is below ours
//    ArrayList<WholeNode> next; // All WholeNodes with distance 1 whose level is above ours
//
//
//    /**
//     * Create a copy of an existing WholeNode.
//     * @param other
//     */
//    public WholeNode(WholeNode other) {
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
//    public int distance(WholeNode other) {
//        int dist = 0;
//
//        WholeNode ourCopy = new WholeNode(this);
//        WholeNode thrCopy = new WholeNode(other);
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

    int slotsInWhole = 16;

    /**
     * Construct a new WholeNode from a JFugue Pattern.
     * @param pattern
     */
    public WholeNode(Pattern pattern, Map<Integer, Note> notesMap) {
        noteValues = new int[slotsInWhole];
        isNewNote = new boolean[slotsInWhole];


        List<Token> tokens = pattern.getTokens();

        double totalDur = 0; int slot = 0;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() != Token.TokenType.NOTE) {
                continue;
            }

            // Obtain our note
            Note newNote = new Note(token.toString());
            Byte notePos = newNote.getPositionInOctave();
            int newNoteKey = -1;
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
                noteValues[slot + s] = newNoteKey;
            }


        }



    }

    /**
     * Construct a new WholeNode by copying another.
     * @param other
     */
    public WholeNode(WholeNode other) {
        // Copy the other
        noteValues = new int[slotsInWhole];
        isNewNote = new boolean[slotsInWhole];

        for (int i = 0; i < slotsInWhole; i++) {
            noteValues[i] = other.noteValues[i];
            isNewNote[i] = (i == 0) || other.isNewNote[i];
        }
    }

    /**
     * Returns the JFugue pattern represented by this node.
     * @return
     */
    Pattern cachedPattern;
    public Pattern toPattern(Map<Integer, Note> notesMap) {
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
                    if (noteVal == -1) {
                        note = Note.createRest(0);
                    } else {
                        note = notesMap.get(i);
                    }
                }

                // Sum the duration
                dur++;
            }

            // Add the last note
            note.setDuration(dur / slotsInWhole);
            cachedPattern.add(note);
        }
        return cachedPattern;
    }

    /**
     * Returns the 'Hamming' distance from this node to another.
     * @param other
     * @return
     */
    public int distance(WholeNode other) {
        int notesValDiffSum = 0;
        int notesCntDiffSum = 0;
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
        return notesValDiffSum + notesCntDiffSum;
    }

    /**
     * Distance helper function.
     * @param index
     * @return
     */
    private boolean isNewNote(int index) {
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
        if (a == -1 || b == -1) { return 1; } // One of the notes is a rest, distance is 1
        return Math.abs(a - b); // Both notes aren't rest, return their difference
    }
}






























