package idc.symphony.music.band;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public class SocialCommit extends Faculty {

    byte instrument;

    public SocialCommit(byte instrument) {
        this.instrument = instrument;

        // Set a seed for this generator
        long seed = 123847771;
        this.gen.setSeed(seed);
    }

    @Override
    public Pattern playMainMelody(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 1, 20, 0.5f, 0.2f, 0.2f);
        p.setInstrument(instrument);
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

        Pattern p = genMusic(key, wholes, 4, 6, 0.6f, 0.2f, 0.05f);
        p.setInstrument(instrument);
        return p;

    }

    @Override
    public Pattern playCarpet2(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 8, 15, 0.6f, 0.1f, 0f);
        p.setInstrument(instrument);
        return p;

    }

    @Override
    public Pattern playCarpet1(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 8, 14, 0.7f, 0f, 0f);
        p.setInstrument(instrument);
        return p;

    }
}
