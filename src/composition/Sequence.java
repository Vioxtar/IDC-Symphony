package composition;

import org.jfugue.pattern.Pattern;

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
    Pattern pattern;
    byte instrument;
    byte voice;
    float time;
    float amp;

    public Sequence(int id, Pattern pattern, byte instrument, byte voice, float amp, float time) {
        this.id = id;
        this.pattern = pattern;
        this.instrument = instrument;
        this.voice = voice;
        this.amp = amp;
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    public Pattern getPattern() {
        return pattern;
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
}
