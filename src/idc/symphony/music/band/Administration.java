package idc.symphony.music.band;

import idc.symphony.music.transformers.LayerMasterer;
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
        rhythm.addLayer("O.O.O.O.O.O.O.O.");
        rhythm.addLayer("o.o.o...o.o.o.oo");
        rhythm.addLayer("..S...S...S..S..");
        rhythm.addLayer("...........s..s.");
        rhythm.addLayer("`.^.`.^.^.^.`.^^");
        rhythm.addLayer(".``...`.```...`.");
        rhythm.setLength(wholes / 2);
        return LayerMasterer.master(rhythm.getPattern(), 1.1f, 1.1f, 0.8f, 0.8f, 0.4f, 0.4f);

    }

    @Override
    public Pattern playSecondary(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.O.");
        rhythm.addLayer("o.o.o...");
        rhythm.addLayer("`.^.`.^.");
        rhythm.addLayer(".``...`.");
        rhythm.setLength(wholes);
        return LayerMasterer.master(rhythm.getPattern(), 1.1f, 1.1f, 0.6f, 0.6f);

    }

    @Override
    public Pattern playCarpet3(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.oo");
        rhythm.addLayer("..o.....");
        rhythm.addLayer(".`...``.");
        rhythm.setLength(wholes);

        return LayerMasterer.master(rhythm.getPattern(), 1.1f, 1.1f, 0.6f);

    }

    @Override
    public Pattern playCarpet2(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.O.");
        rhythm.addLayer("......oo");
        rhythm.setLength(wholes);
        return LayerMasterer.master(rhythm.getPattern(),1.1f, 1.1f);

    }

    @Override
    public Pattern playCarpet1(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O...O...");
        rhythm.addLayer("^.^.^.^.");
        rhythm.addLayer("...`...`");
        rhythm.setLength(wholes);
        return LayerMasterer.master(rhythm.getPattern(), 1.1f, 0.6f, 0.6f);

    }
}
