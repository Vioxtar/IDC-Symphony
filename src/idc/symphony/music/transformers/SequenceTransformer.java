package idc.symphony.music.transformers;

import org.jfugue.midi.MidiDefaults;
import org.jfugue.midi.MidiDictionary;
import org.jfugue.parser.ChainingParserListenerAdapter;
import org.jfugue.theory.Note;

/**
 * Wraps a given musical pattern by:
 * - Changing the amplitude of each note
 * - Adding a time seek at the beginning of the pattern
 * - Adding a layer declaration at the beginning of the pattern
 * - Changing the instrument at the beginning of the pattern
 *
 * Intended for patterns that don't state the latter three; explicitly or via staccato string.
 */
public class SequenceTransformer extends ChainingParserListenerAdapter {
    double time;
    float   amplitude;
    byte     instrument;
    byte    voice;

    boolean[] layerTimeChanged;

    public SequenceTransformer() {
        this.time = 0;
        this.amplitude = 1;
        this.voice = 0;
        this.instrument = (byte)-1;

        this.layerTimeChanged = new boolean[MidiDefaults.LAYERS];
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setAmp(float amp) {
        this.amplitude = amp;
    }

    public void setInstrument(byte instrument) {
        this.instrument = instrument;
    }

    public boolean setInstrument(String instrument) {
        if (MidiDictionary.INSTRUMENT_STRING_TO_BYTE.containsKey(instrument)) {
            this.instrument = MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get(instrument);
        }

        return false;
    }

    public void setVoice(byte voice) {
        this.voice = voice;
    }

    @Override
    public void beforeParsingStarts() {
        super.beforeParsingStarts();

        // First and foremost - track
        if (voice != -1) {
            fireTrackChanged(voice);
        }

        // Then - change current time within track
        if (time != -1) {
            fireTrackBeatTimeRequested(time);
            layerTimeChanged[0] = true;
        }

        // Then - change instrument for given track
        if (instrument > 0) {
            fireInstrumentParsed(instrument);
        }
    }

    public void onTrackBeatTimeRequested(double time) {
        this.fireTrackBeatTimeRequested(time + this.time);
    }

    /**
     * Each layer has its own beattime bookmark
     * @param layer target layer
     */
    public void onLayerChanged(byte layer) {
        super.onLayerChanged(layer);

        if (!layerTimeChanged[layer]) {
            fireTrackBeatTimeRequested(time);
            layerTimeChanged[layer] = true;
        }
    }

    @Override
    public void onNoteParsed(Note note) {
        if (amplitude != 1 && amplitude != -1) {
            float newVel = amplitude * note.getOnVelocity();
            newVel = Math.min(127f, Math.max(0f, newVel));
            note.setOnVelocity((byte)newVel);
        }

        super.onNoteParsed(note);
    }
}