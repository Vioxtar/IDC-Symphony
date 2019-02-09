package idc.symphony.music;

import idc.symphony.music.transformers.BassRhythmTransformer;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.rhythm.Rhythm;
import org.jfugue.theory.Key;
import org.junit.jupiter.api.Test;

public class StupidTest {
    @Test
    public void testSnippet() {
        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.O.");
        rhythm.addLayer("......oo");
        rhythm.addLayer("^.^.^.^.");
        rhythm.addLayer("...`...`");
        rhythm.setLength(4);

        Pattern p = new Pattern("Gw Ew Bw Dw");
        p.setInstrument(32);
        //p = AmplitudeTransformer.setRelativeAmp(AMP_CARPET3, p);

        Player player = new Player();
        player.play(BassRhythmTransformer.toBassRhythm(p, new boolean[]{true, false, true, false}));
    }

    public Pattern playMainMelody(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.O.O.O.O.O.");
        rhythm.addLayer("o.o.o...o.o.o.oo");
        rhythm.addLayer("..S...S...S..S..");
        rhythm.addLayer("...........s..s.");
        rhythm.addLayer("`.^.`.^.^.^.`.^^");
        rhythm.addLayer(".``...`.```...`.");
        rhythm.setLength(wholes / 2);
        return rhythm.getPattern();

    }

    @Test
    public void playSecondaryTest() {
        Player player = new Player();
        player.play(playSecondary(4, Key.DEFAULT_KEY));
    }

    public Pattern playSecondary(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.O.");
        rhythm.addLayer("o.o.o...");
        rhythm.addLayer("`.^.`.^.");
        rhythm.addLayer("..`...`.");
        rhythm.setLength(wholes);
        return rhythm.getPattern();

    }

    public Pattern playCarpet3(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.oo");
        rhythm.addLayer(".`o..``.");
        rhythm.setLength(wholes);

        return rhythm.getPattern();

    }

    public Pattern playCarpet2(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O.O.O.O.");
        rhythm.addLayer("......oo");
        rhythm.setLength(wholes);
        return rhythm.getPattern();

    }

    public Pattern playCarpet1(int wholes, Key key) {

        Rhythm rhythm = new Rhythm();
        rhythm.addLayer("O...O...");
        rhythm.addLayer("^.^.^.^.");
        rhythm.addLayer("...`...`");
        rhythm.setLength(wholes);
        return rhythm.getPattern();

    }
}
