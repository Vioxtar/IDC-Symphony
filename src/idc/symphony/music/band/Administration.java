package idc.symphony.music.band;

import org.jfugue.pattern.Pattern;
import org.jfugue.rhythm.Rhythm;
import org.jfugue.theory.Key;

public class Administration extends Faculty {

    byte instrument;

    public Administration(byte instrument) {
        this.instrument = instrument;

        // Set a seed for this generator
        long seed = 123;
        this.gen.setSeed(seed);
    }

    @Override
    public int getTargetTrack() {
        return 9;
    }

    @Override
    public Pattern playMainMelody(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.O.");
        rhythm.setLength(wholes);
        return rhythm.getPattern();

    }

    @Override
    public Pattern playSecondary(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("o.o.^.^.");
        rhythm.setLength(wholes);
        return rhythm.getPattern();

    }

    @Override
    public Pattern playCarpet3(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("^^^^^^^^");
        rhythm.setLength(wholes);
        return rhythm.getPattern();

    }

    @Override
    public Pattern playCarpet2(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.O.");
        rhythm.addLayer("......oo");
        rhythm.setLength(wholes);
        return rhythm.getPattern();

    }

    @Override
    public Pattern playCarpet1(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("^.^.^.^.");
        rhythm.addLayer("...`...`");
        rhythm.setLength(wholes);
        return rhythm.getPattern();

    }
}
