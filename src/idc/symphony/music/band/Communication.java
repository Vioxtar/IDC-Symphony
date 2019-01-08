package idc.symphony.music.band;

import idc.symphony.music.transformers.AmplitudeTransformer;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public class Communication extends Faculty {

    byte instrument;

    public Communication(byte instrument) {
        this.instrument = instrument;

        // Set a seed for this generator
        long seed = 84269;
        this.gen.setSeed(seed);
    }


    @Override
    public int getTargetTrack() {
        return 2;
    }


    @Override
    public Pattern playMainMelody(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 3, 15, 0.5f, 0.2f, 0.2f);
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_MELODY, p);

        return p;

    }

    @Override
    public Pattern playSecondary(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 3, 4, 0.8f, 0.1f, 0.1f);
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_SECMELODY, p);
        return p;

    }

    @Override
    public Pattern playCarpet3(int wholes, Key key) {

        Pattern p = getLibrary().getPattern("chords_prog_v1");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_CARPET3, p);

        return p;

    }

    @Override
    public Pattern playCarpet2(int wholes, Key key) {

        Pattern p = getLibrary().getPattern("chords_prog_v1");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_CARPET2, p);

        return p;

    }

    @Override
    public Pattern playCarpet1(int wholes, Key key) {

        Pattern p = getLibrary().getPattern("chords_prog_v1");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_CARPET1, p);

        return p;

    }
}
