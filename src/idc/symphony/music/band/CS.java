package idc.symphony.music.band;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

public class CS extends Faculty {

    byte instrument;

    public CS() {
        this.instrument = 50;

        // Set a seed for this generator
        long seed = 31;
        this.gen.setSeed(seed);
    }

    @Override
    public Pattern playMainMelody(int t, Key key) {

        int added = 0;

        Pattern p = new Pattern();
        p.setInstrument(instrument);

        Note lastNotePlayed = null;

        while (added < t) {

            // Establish next note duration
            int nextNoteDur = ranRange(1, 3);
            nextNoteDur = Math.min(nextNoteDur * ((int)Math.pow(2, ranRange(1, 6))), t - added);
            added += nextNoteDur;

            Note nextNote;

            if (lastNotePlayed != null) {
                nextNote = getCloseNote(getKeyNotes(key), lastNotePlayed, 2);
            } else {
                nextNote = getRandomKeyNote(key);
            }

            lastNotePlayed = nextNote;

            // Decide if we're resting
            boolean isRest = ranRange(0d, 1d) < 0;

            if (isRest) {
                p.add("R/" + toNoteDur(nextNoteDur));
            } else {
                p.add("" + nextNote + '/' + toNoteDur(nextNoteDur));
            }
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
