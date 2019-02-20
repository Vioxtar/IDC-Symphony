package idc.symphony.music.melodygen;

import java.util.ArrayList;

public class Alternator {

    RanGen gen;
    public Alternator(RanGen gen) {
        this.gen = gen; // Used for semi-determinism
    }

    public Node alternate(Node node) {
        HammingWhole newHammingWhole = alternate(node.hammingWhole);
        Node newNode = new Node(newHammingWhole);
        return newNode;
    }


    // TODO: Refactor these into seperate functions, that return true upon success false otherwise!

    public HammingWhole alternate(HammingWhole original) {
        HammingWhole copy = new HammingWhole(original);

        int ran = gen.ranRange(0, 3);

        if (ran == 0) // Octave change

            if (gen.ranRange(1, 2) == 1) {
                copy.addOctave(1);
            } else {
                copy.addOctave(-1);
            }

        else if (ran == 1) { // Restify / unrestify a note

            ArrayList<Integer> nonRestingNotes = new ArrayList<>();
            ArrayList<Integer> restingNotes = new ArrayList<>();
            for (int i = 0; i < copy.slotsInWhole; i++) {
                if (copy.isNewNote(i)) {
                    if (!copy.isRestVal(copy.noteValues[i])) {
                        nonRestingNotes.add(i);
                    } else {
                        restingNotes.add(i);
                    }
                }
            }

            ran = gen.ranRange(1, 2);

            if (ran == 1 && nonRestingNotes.size() > 0) { // Restify a note
                int randomIndex = nonRestingNotes.get(gen.ranRange(0, nonRestingNotes.size() - 1));
                copy.restifyNote(randomIndex);
            } else if (restingNotes.size() > 0) { // Unrestify a note
                int randomIndex = restingNotes.get(gen.ranRange(0, restingNotes.size() - 1));
                copy.unRestifyNote(randomIndex);
            }

        } else if (ran == 2) { // Decrement/increment a note

            ArrayList<Integer> decrementals = new ArrayList<>();
            ArrayList<Integer> incrementals = new ArrayList<>();

            for (int i = 0; i < copy.slotsInWhole; i++) {
                if (copy.isNewNote(i)) {
                    int noteVal = copy.noteValues[i];
                    if (!copy.isRestVal(noteVal)) {
                        if (noteVal < copy.maxNoteValue) {
                            incrementals.add(i);
                        }
                        if (noteVal > 0) {
                            decrementals.add(i);
                        }
                    }
                }
            }

            ran = gen.ranRange(1, 2);

            if (ran == 1 && decrementals.size() > 0) { // Decrement
                int randomIndex = decrementals.get(gen.ranRange(0, decrementals.size() - 1));
                copy.incrementNote(randomIndex, -1);
            } else if (incrementals.size() > 0) { // Increment
                int randomIndex = incrementals.get(gen.ranRange(0, incrementals.size() - 1));
                copy.incrementNote(randomIndex, 1);
            }

        } else { // Split/merge a note

            ArrayList<Integer> mergeables = new ArrayList<>();
            ArrayList<Integer> splittables = new ArrayList<>();

            for (int i = 0; i < copy.slotsInWhole; i++) {
                if (copy.isNewNote(i)) {
                    if (i > 0) {
                        int newNoteVal = copy.noteValues[i];
                        int oldNoteVal = copy.noteValues[i-1];
                        if (newNoteVal == oldNoteVal) {
                            mergeables.add(i);
                        }
                    }
                } else {
                    splittables.add(i);
                }
            }

            ran = gen.ranRange(1, 2);
            if (ran == 1 && mergeables.size() > 0) { // Decrement
                int randomIndex = mergeables.get(gen.ranRange(0, mergeables.size() - 1));
                copy.mergeNotes(randomIndex - 1, randomIndex);
            } else if (splittables.size() > 0) { // Increment
                int randomIndex = splittables.get(gen.ranRange(0, splittables.size() - 1));
                copy.splitNote(randomIndex);
            }
        }

        return copy;
    }

}
