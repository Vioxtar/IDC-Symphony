package composition;

/**
 * A sequence is a packet of information containing:
 * - the sequence name
 * - the instrument to be used
 * - the target voice
 * - the sequence start time
 * - the sequence repetition count
 */
public class Sequence{

    int id; // We may not need this
    String patternName;
    byte instrument;
    byte voice;
    float time;
    float amp;
    short reps;

    public Sequence(int id, String patternName, byte instrument, byte voice, float amp, float time, short reps) {
        this.id = id;
        this.patternName = patternName;
        this.instrument = instrument;
        this.voice = voice;
        this.amp = amp;
        this.time = time;
        this.reps = reps;
    }

    public float getTime() {
        return time;
    }

    public String getPatternName() {
        return patternName;
    }

    public byte getInstrument() {
        return instrument;
    }

    public byte getVoice() {
        return voice;
    }

    public float getAmplitude() {
        return amp;
    }

    public short getReps() {
        return reps;
    }
}
