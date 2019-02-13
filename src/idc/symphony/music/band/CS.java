package idc.symphony.music.band;

import idc.symphony.music.transformers.AmplitudeTransformer;
import idc.symphony.music.transformers.BassRhythmTransformer;
import idc.symphony.music.transformers.OctaveTransformer;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public class CS extends Faculty {

    byte instrument;

    public CS(byte instrument) {
        this.instrument = instrument;

        // Set a seed for this generator
        long seed = 65536;
        this.gen.setSeed(seed);
    }


    @Override
    public int getTargetTrack() {
        return 3;
    }


    @Override
    public Pattern playMainMelody(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 3, 20, 0.3f, 0.4f, 0.2f);
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(1.28f * AMP_MELODY, p);

        return p;

    }

    @Override
    public Pattern playSecondary(int wholes, Key key) {

        Pattern p = genMusic(key, wholes, 3, 8, 0.4f, 0.2f, 0.1f);
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(1.28f * AMP_SECMELODY, p);

        return OctaveTransformer.OffsetOctave(p, -1);
    }

    @Override
    public Pattern playCarpet3(int wholes, Key key) {
        //Pattern p = getLibrary().getPattern("chords_prog_v1");
        Pattern p = new Pattern("Gw Ew Bw Dw");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(1.44f, p);

        return BassRhythmTransformer.toBassRhythm(p, new boolean[]{true, true, true, false, true, true, true, false});
    }

    @Override
    public Pattern playCarpet2(int wholes, Key key) {
        //Pattern p = getLibrary().getPattern("chords_prog_v1");
        Pattern p = new Pattern("Gw Ew Bw Dw");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(1.44f, p);

        return BassRhythmTransformer.toBassRhythm(p, new boolean[]{true, true, true, true});
    }

    @Override
    public Pattern playCarpet1(int wholes, Key key) {
        //Pattern p = getLibrary().getPattern("chords_prog_v1");
        Pattern p = new Pattern("Gw Ew Bw Dw");
        p.setInstrument(instrument);
        p = AmplitudeTransformer.setRelativeAmp(1.44f, p);

        return BassRhythmTransformer.toBassRhythm(p, new boolean[]{true, false, true, false});
    }
}
