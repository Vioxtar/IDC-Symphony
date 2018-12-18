package composition;

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
    float   time;
    float   amplitude;
    byte    instrument;
    byte    voice;

    boolean[] layerTimeChanged;

    /**
     * @param sequence the sequence to be transformed
     */
    public SequenceTransformer(Sequence sequence) {
        this.time = sequence.getTime();
        this.amplitude = sequence.getAmplitude();
        this.voice = sequence.getVoice();
        this.instrument = sequence.getInstrument();

        this.layerTimeChanged = new boolean[MidiDefaults.LAYERS];
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
        fireInstrumentParsed(instrument);
    }

    /**
     * Each layer has its own beattime bookmark
     * @param layer
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
