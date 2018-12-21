package idc.symphony.music;

import org.jfugue.midi.MidiDefaults;
import org.jfugue.pattern.Pattern;

import java.util.ArrayList;

public class Composition {
    private ArrayList<Pattern>[] tracks;
    private int tempo;

    public Composition(float[] trackLengths, int tempo) {
        initTracks(trackLengths, tempo);
    }

    private void initTracks(float[] trackLengths, int tempo) {
        tracks = new ArrayList[MidiDefaults.TRACKS];

        for (int track = 0; track < tracks.length; track++) {
            tracks[track] = new ArrayList<>(trackLengths.length);

            for (int pattern = 0; pattern < trackLengths.length; pattern++) {
                tracks[track].set(pattern, generateRestPattern(trackLengths[pattern]));
            }
        }
    }

    private Pattern generateRestPattern(float duration) {
        return new Pattern("R/" + duration);
    }

    public float beatsToSeconds(float beats) {
        return (beats / MidiDefaults.DEFAULT_TEMPO_BEATS_PER_WHOLE * tempo);
    }

    public float secondsToBeats(float seconds) {
        return (seconds / tempo * MidiDefaults.DEFAULT_TEMPO_BEATS_PER_WHOLE);
    }
}
