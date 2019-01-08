package idc.symphony.music.band;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public class Psychology extends Faculty {

    byte instrument;

    public Psychology(byte instrument) {
        this.instrument = instrument;

        // Set a seed for this generator
        long seed = 21;
        this.gen.setSeed(seed);
    }


    @Override
    public int getTargetTrack() {
        return 9;
    }


    @Override
    public Pattern playMainMelody(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 1, 7, 0.5f, 0.2f, 0.2f);
        p.setInstrument(instrument);
        p.addToEachNoteToken("a45");
        return p;

    }

    @Override
    public Pattern playSecondary(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 3, 4, 0.8f, 0.1f, 0.1f);
        p.setInstrument(instrument);
        return p;

    }

    @Override
    public Pattern playCarpet3(int wholes, Key key) {

        Pattern p = getLibrary().getPattern("chords_prog_v1");
        p.setInstrument(instrument);
        return p;

    }

    @Override
    public Pattern playCarpet2(int wholes, Key key) {

        Pattern p = getLibrary().getPattern("chords_prog_v1");
        p.setInstrument(instrument);
        return p;

    }

    @Override
    public Pattern playCarpet1(int wholes, Key key) {

        Pattern p = getLibrary().getPattern("chords_prog_v1");
        p.setInstrument(instrument);
        return p;

    }
}
