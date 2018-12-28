package idc.symphony.music.band;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

public class CS extends Faculty {

    byte instrument;

    public CS() {
        this.instrument = 70;

        // Set a seed for this generator
        long seed = 15;
        this.gen.setSeed(seed);
    }

    @Override
    public Pattern playMainMelody(int wholes, Key key) {

        Pattern p = new Pattern();
        p.setInstrument(instrument);

        Note lastNote = null;

        int wholesRep = 4;

        float[] durs = divideDuration(wholesRep, 20);
        Note[] nots = new Note[durs.length];
        for (int i = 0; i < durs.length; i++) {
            nots[i] = getRandomKeyNote(key);
            if (ranRange(0d, 1d) < 0.8) {
                nots[i] = Note.REST;
            }
        }

        int wholesPlayed = 0;

        int itsPlayed = 0;

        while (wholesPlayed < wholes) {
            for (int i = 0; i < durs.length; i++) {

                Note n = nots[i];
                float dur = durs[i];

                if (ranRange(1, 8) == 1) { // Randomly randomize a random number of randomly selected notes in a random fashion
                    int notesToChange = ranRange(1, 6);
                    for (int j = 1; j <= notesToChange; j++) {
                        int noteToChange = ranRange(0, nots.length - 1);
                        nots[noteToChange] = getCloseNote(getKeyNotes(key), nots[noteToChange], 2);
                    }
                }

                if (ranRange(1, 6) == 1) { // Perform a single shuffle in the durations

                    int durChanges = ranRange(1,1);

                    for (int j = 1; j <= durChanges; j++) {
                        int indxA = ranRange(0, durs.length - 1);
                        int indxB = ranRange(0, durs.length - 1);
                        float aDur = durs[indxA];
                        durs[indxA] = durs[indxB];
                        durs[indxB] = aDur;
                    }
                }

                lastNote = n;
                n.setDuration(dur);
                p.add(n);
            }


//            if (itsPlayed % 2 == 0) { // Randomly randomize a random number of randomly selected notes in a random fashion
//                int notesToChange = ranRange(4, 7);
//                for (int j = 1; j <= notesToChange; j++) {
//                    int noteToChange = ranRange(0, nots.length - 1);
//                    nots[noteToChange] = getCloseNote(getKeyNotes(key), nots[noteToChange], 5);
//                }
//            }
//
//            if (ranRange(1, 3) == 1) { // Perform a single shuffle in the durations
//                durs = divideDuration(wholesRep, 3);
//            }

            wholesPlayed += wholesRep;
            itsPlayed++;
        }


        return p;
    }

    @Override
    public Pattern playSecondary(int t, Key key) {
        return null;
    }

    @Override
    public Pattern playCarpet3(int t, Key key) {
        return null;
    }

    @Override
    public Pattern playCarpet2(int t, Key key) {
        return null;
    }

    @Override
    public Pattern playCarpet1(int t, Key key) {
        return null;
    }
}
