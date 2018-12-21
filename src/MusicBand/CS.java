package MusicBand;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

import java.util.List;

public class CS extends Faculty {

    byte instrument;

    public CS() {
        this.instrument = 50;
    }

    @Override
    public Pattern playMainMelody(int t, Key key) {

        int added = 0;

        Pattern p = new Pattern();
        p.setInstrument(instrument);

        Note lastNotePlayed = null;

        while (added < t) {

            // Establish next note duration
            int nextNoteDur = ranRange(1, 4);
            nextNoteDur = Math.min(nextNoteDur * (ranRange(0, 1) == 1 ? 16:32), t - added);
            added += nextNoteDur;

            String nextNote;

            if (lastNotePlayed != null) {
                nextNote = getCloseNote(getKeyNotes(key), lastNotePlayed, ranRange(1,2)).toString();
            } else {
                nextNote = getRandomKeyNote(key).toString();
            }

            // Decide if we're resting
            boolean isRest = ranRange(0d, 1d) < 0.1;

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
