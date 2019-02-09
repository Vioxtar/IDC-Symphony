package idc.symphony.music.transformers;

import org.jfugue.parser.ChainingParserListenerAdapter;
import org.jfugue.theory.Note;

import java.util.Arrays;

/**
 * Trims a one-track sequence to a requested duration in wholes.
 * Allows notes to hang beyond the requested duration per trimming leniency, but does not allow notes
 * to start past the duration.
 */
public class SequenceTrimmer extends ChainingParserListenerAdapter {
    public static final double TRIMMING_LENIENCY = 0.25;

    private double maxDuration = 0;
    private double[] trackBeatTime = new double[16];
    private int currentLayer = 0;

    public void setMaxDuration(double maxDuration) {
        this.maxDuration = maxDuration;
    }

    @Override
    public void beforeParsingStarts() {
        super.beforeParsingStarts();

        Arrays.fill(trackBeatTime, 0);
    }

    public void onLayerChanged(byte layer) {
        super.onLayerChanged(layer);
        currentLayer = layer;
    }

    public void onTrackBeatTimeRequested(double time) {
        super.onTrackBeatTimeRequested(time);
        trackBeatTime[currentLayer] = time;
    }

    public void onNoteParsed(Note note) {
        if (trackBeatTime[currentLayer] < maxDuration) {
            if (noteExceedsMaxDuration(note)) {
                note.setDuration(maxDuration + TRIMMING_LENIENCY - trackBeatTime[currentLayer]);
            }

            super.onNoteParsed(note);
        }

        trackBeatTime[currentLayer] += note.getDuration();
    }

    private boolean noteExceedsMaxDuration(Note note) {
        return trackBeatTime[currentLayer] + note.getDuration() > maxDuration + TRIMMING_LENIENCY;
    }
}
