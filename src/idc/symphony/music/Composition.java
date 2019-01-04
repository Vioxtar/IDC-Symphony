package idc.symphony.music;

import idc.symphony.music.transformers.SequenceTransformer;
import org.jfugue.midi.MidiDefaults;
import org.jfugue.pattern.Pattern;
import org.staccato.StaccatoParserListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Composition data structure
 */
public class Composition {
    public static final int[] MELODIC_TRACKS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15
    };

    public static final int RHYTHMIC_TRACK = 9;

    private List<List<Pattern>> tracks;
    private int[] trackLengths;
    private float[] sectionStartTimes;

    /**
     * Ensures start time of pattern is beginning of sequence,
     * allowing patterns to overlap while maintaining song structure.
     */
    private SequenceTransformer patternSequencer;

    /**
     * Proxy pattern builder for transformers
     */
    private StaccatoParserListener patternBuilder;

    public Composition(int[] trackLengths) {
        if (trackLengths.length == 0) {
            throw new IllegalArgumentException("Track lengths should be non-empty array");
        }

        this.trackLengths = Arrays.copyOf(trackLengths, trackLengths.length);
        patternSequencer = new SequenceTransformer();
        patternBuilder = new StaccatoParserListener();
        patternSequencer.addParserListener(patternBuilder);
        initStartTimes();
        initTracks();
    }

    private void initTracks() {
        tracks = new ArrayList<>(MidiDefaults.TRACKS);

        for (int track = 0; track < MidiDefaults.TRACKS; track++) {
            tracks.add(track, new ArrayList<>(trackLengths.length));

            for (int pattern = 0; pattern < trackLengths.length; pattern++) {
                tracks.get(track).add(pattern, null);
            }
        }
    }

    private void initStartTimes() {
        sectionStartTimes = new float[trackLengths.length];
        sectionStartTimes[0] = 0;

        for (int i = 1; i < trackLengths.length; i++) {
            if (i > 1) {
                sectionStartTimes[i] += sectionStartTimes[i - 1];
            }

            sectionStartTimes[i] += trackLengths[i - 1];
        }
    }

    public void put(int track, int section, Pattern pattern) {
        if (track < 0 || track >= (MidiDefaults.TRACKS)) {
            throw new IllegalArgumentException("Track index out of range");
        }
        if (section < 0 || section >= sectionStartTimes.length) {
            throw new IllegalArgumentException("Section index out of range");
        }

        tracks.get(track).set(section, pattern);
    }

    public void prepend(int track, int section, Pattern pattern) {
        if (track < 0 || track >= (MidiDefaults.TRACKS)) {
            throw new IllegalArgumentException("Track index out of range");
        }
        if (section < 0 || section >= sectionStartTimes.length) {
            throw new IllegalArgumentException("Section index out of range");
        }

        List<Pattern> sections = tracks.get(track);
        Pattern sectionPattern = sections.get(section);

        if (sectionPattern == null) {
            sections.set(section, pattern);
        } else {
            sectionPattern.prepend(pattern);
        }
    }

    public void append(int track, int section, Pattern pattern) {
        if (track < 0 || track >= (MidiDefaults.TRACKS)) {
            throw new IllegalArgumentException("Track index out of range");
        }
        if (section < 0 || section >= sectionStartTimes.length) {
            throw new IllegalArgumentException("Section index out of range");
        }

        List<Pattern> sections = tracks.get(track);
        Pattern sectionPattern = sections.get(section);

        if (sectionPattern == null) {
            sections.set(section, pattern);
        } else {
            sectionPattern.add(pattern);
        }
    }

    public Pattern getPattern(int track, int section) {
        if (track < 0 || track >= (MidiDefaults.TRACKS)) {
            throw new IllegalArgumentException("Track index out of range");
        }
        if (section < 0 || section >= sectionStartTimes.length) {
            throw new IllegalArgumentException("Section index out of range");
        }

        return tracks.get(track).get(section);
    }

    public Pattern getFinalComposition(int tempo) {
        Pattern finalPattern = new Pattern();

        for (int section = 0; section < sectionStartTimes.length; section++) {
            for (int track = 0; track < tracks.size(); track++) {
                List<Pattern> sectionList = tracks.get(track);
                patternSequencer.setVoice((byte)track);
                patternSequencer.setTime(sectionStartTimes[section]);
                Pattern sectionPattern = sectionList.get(section);

                if (sectionPattern != null) {
                    sectionPattern.transform(patternSequencer);
                    finalPattern.add(patternBuilder.getPattern()).add("\n");
                }
            }
        }

        finalPattern.setTempo(tempo);

        return finalPattern;
    }
}
