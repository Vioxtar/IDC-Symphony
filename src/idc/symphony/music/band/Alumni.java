package idc.symphony.music.band;

import idc.symphony.music.transformers.AmplitudeTransformer;
import idc.symphony.music.transformers.OctaveTransformer;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public class Alumni extends Faculty {

    byte instrument;

    public Alumni(byte instrument) {
        this.instrument = instrument;

        // Set a seed for this generator
        long seed = 1213141;
        this.gen.setSeed(seed);
    }

    @Override
    public int getTargetTrack() {
        return 0;
    }

    @Override
    public Pattern playMainMelody(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 2, 4, 0.3f, 0.4f, 0.2f);
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_MELODY, p);
        return melodyTuner.tune(p);

    }

    @Override
    public Pattern playSecondary(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 3, 4, 0.8f, 0.1f, 0.1f);
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_SECMELODY, p);
        return OctaveTransformer.OffsetOctave(melodyTuner.tune(p), -1);

    }

    @Override
    public Pattern playCarpet3(int wholes, Key key) {

        Pattern p = getLibrary().getPattern("chords_prog_v1");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_CARPET3, p);



        return OctaveTransformer.OffsetOctave(p, -1);

    }

    @Override
    public Pattern playCarpet2(int wholes, Key key) {

        Pattern p = getLibrary().getPattern("chords_prog_v1");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_CARPET2, p);

        return OctaveTransformer.OffsetOctave(p, -1);

    }

    @Override
    public Pattern playCarpet1(int wholes, Key key) {

        Pattern p = getLibrary().getPattern("chords_prog_v1");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(AMP_CARPET1, p);

        return OctaveTransformer.OffsetOctave(p, -1);

    }
}
