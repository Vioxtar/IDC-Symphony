package idc.symphony.music;

import idc.symphony.music.sequence.SequenceTransformer;
import org.jfugue.midi.MidiDefaults;
import org.jfugue.pattern.Pattern;
import org.staccato.StaccatoParserListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Composition {
    private ArrayList<Pattern>[] tracks;
    private float[] trackLengths;
    private float[] patternStartTimes;
    private SequenceTransformer patternSequencer;
    private StaccatoParserListener patternBuilder;
    private int tempo;

    public Composition(float[] trackLengths, int tempo) {
        if (trackLengths.length == 0) {
            throw new IllegalArgumentException("Track lengths should be non-empty array");
        }

        this.tempo = tempo;
        this.trackLengths = Arrays.copyOf(trackLengths, trackLengths.length);
        patternSequencer = new SequenceTransformer();
        patternBuilder = new StaccatoParserListener();
        patternSequencer.addParserListener(patternBuilder);
        initStartTimes();
        initTracks();
    }

    private void initTracks() {
        tracks = new ArrayList[MidiDefaults.TRACKS];


        for (int track = 0; track < tracks.length; track++) {
            tracks[track] = new ArrayList<>(trackLengths.length);

            for (int pattern = 0; pattern < trackLengths.length; pattern++) {
                tracks[track].set(pattern, generateRestPattern(track, pattern));
            }
        }
    }

    private void initStartTimes() {
        patternStartTimes = new float[trackLengths.length];
        patternStartTimes[0] = 0;

        for (int i = 1; i < trackLengths.length; i++) {
            if (i > 1) {
                patternStartTimes[i] += patternStartTimes[i - 2];
            }

            patternStartTimes[i] += trackLengths[i - 1];
        }
    }

    private Pattern generateRestPattern(int track, int section) {
        return new Pattern(String.format("V%d @%f R/%f",
                track,
                patternStartTimes[section],
                trackLengths[section]
        ));
    }

    public void put(int track, int section, Pattern pattern) {
        if (track < 0 || track >= (MidiDefaults.TRACKS)) {
            throw new IllegalArgumentException("Track should be between 0 and MidiDefaults.TRACKS");
        }

        patternSequencer.setTime(patternStartTimes[section]);
        patternSequencer.setVoice((byte)track);

        pattern.transform(patternSequencer);
        tracks[track].set(section, patternBuilder.getPattern());
    }

    public float beatsToSeconds(float beats) {
        return (beats / MidiDefaults.DEFAULT_TEMPO_BEATS_PER_WHOLE * tempo);
    }

    public float secondsToBeats(float seconds) {
        return (seconds / tempo * MidiDefaults.DEFAULT_TEMPO_BEATS_PER_WHOLE);
    }

    public Pattern getFinalComposition() {
        Pattern finalPattern = new Pattern();

        for (List<Pattern> track : tracks) {
            track.forEach((sequence) -> finalPattern.add(sequence));
        }

        return finalPattern;
    }
}
