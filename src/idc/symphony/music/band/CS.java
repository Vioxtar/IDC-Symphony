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
    public Pattern playMainMelody(int wholes, Key key) {

        Pattern p = new Pattern();

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
