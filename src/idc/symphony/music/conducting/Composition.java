package idc.symphony.music.conducting;

import idc.symphony.music.transformers.SequenceTransformer;
import idc.symphony.music.transformers.SequenceTrimmer;
import org.jfugue.midi.MidiDefaults;
import org.jfugue.pattern.Pattern;
import org.staccato.StaccatoParserListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Composition data structure
 *
 * Synchronizes pattern sections according to expected started times.
 */
public class Composition {
    private List<Section> sections;

    /**
     * Ensures start time of pattern is beginning of sequence,
     * allowing patterns to overlap while maintaining song structure.
     */
    private SequenceTransformer patternSequencer;
    private SequenceTrimmer patternTrimmer;

    /**
     * Proxy pattern builder for transformers
     */
    private StaccatoParserListener patternBuilder;

    public Composition(int[] trackLengths) {
        if (trackLengths.length == 0) {
            throw new IllegalArgumentException("Track lengths should be non-empty array");
        }

        patternSequencer = new SequenceTransformer();
        patternTrimmer = new SequenceTrimmer();
        patternBuilder = new StaccatoParserListener();
        patternSequencer.addParserListener(patternBuilder);
        patternTrimmer.addParserListener(patternBuilder);
        sections = new ArrayList<>();

        if (trackLengths != null && trackLengths.length != 0) {
            initializeSections(trackLengths);
        }
    }

    private void initializeSections(int[] trackLengths) {
        for (int i = 0; i < trackLengths.length; i++) {
            sections.add(new Section(trackLengths[i]));
        }
    }

    public void addSection(float length) {
        addSection(sections.size(), length);
    }

    public void addSection(int index, float length) {
        sections.add(index, new Section(length));
    }

    /**
     * Puts given pattern in given track at given section.
     * Overrides pattern if exists.
     */
    public void put(int track, int section, Pattern pattern) {
        if (track < 0 || track >= (MidiDefaults.TRACKS)) {
            throw new IllegalArgumentException("Track index out of range");
        }
        if (section < 0 || section >= sections.size()) {
            throw new IllegalArgumentException("Section index out of range");
        }

        sections.get(section).set(track, pattern);
    }

    /**
     * Prepends given pattern to pattern in given track at given section.
     * Creates new pattern if one does not exist
     */
    public void prepend(int track, int section, Pattern pattern) {
        if (track < 0 || track >= (MidiDefaults.TRACKS)) {
            throw new IllegalArgumentException("Track index out of range");
        }
        if (section < 0 || section >= sections.size()) {
            throw new IllegalArgumentException("Section index out of range");
        }

        Pattern sectionPattern = sections.get(section).get(track);

        if (sectionPattern == null) {
            sections.get(section).set(track, pattern);
        } else {
            sectionPattern.prepend(pattern);
        }
    }

    /**
     * Append given pattern to pattern in given track at given section.
     * Creates new pattern if one does not exist
     */
    public void append(int track, int section, Pattern pattern) {
        if (track < 0 || track >= (MidiDefaults.TRACKS)) {
            throw new IllegalArgumentException("Track index out of range");
        }
        if (section < 0 || section >= sections.size()) {
            throw new IllegalArgumentException("Section index out of range");
        }

        Pattern sectionPattern = sections.get(section).get(track);

        if (sectionPattern == null) {
            sections.get(section).set(track, pattern);
        } else {
            sectionPattern.add(pattern);
        }
    }

    /**
     * @return Pattern in given track at given section
     */
    public Pattern getPattern(int track, int section) {
        if (track < 0 || track >= (MidiDefaults.TRACKS)) {
            throw new IllegalArgumentException("Track index out of range");
        }
        if (section < 0 || section >= sections.size()) {
            throw new IllegalArgumentException("Section index out of range");
        }

        return sections.get(section).get(track);
    }

    public int getNumTracks() {
        return MidiDefaults.TRACKS;
    }

    public int getNumSections() {
        return sections.size();
    }

    /**
     * Creates final composition out of current section state
     * Each section undergoes a sync transformation that makes sure the section pattern starts
     * at the expected time.
     *
     * @param tempo Final pattern tempo
     * @return  Generated pattern.
     */
    public Pattern getFinalComposition(int tempo) {
        Pattern finalPattern = new Pattern();
        float startTime = 0;

        for (int sectionNo = 0; sectionNo < sections.size(); sectionNo++) {
            Section section = sections.get(sectionNo);
            Section nextSection = (sectionNo < sections.size() - 1)
                    ? sections.get(sectionNo + 1)
                    : null;

            for (int track = 0; track < MidiDefaults.TRACKS; track++) {
                Pattern sectionPattern = section.get(track);

                if (sectionPattern != null) {
                    float trimLength = section.length;

                    if (nextSection == null || nextSection.isBreak) {
                        if (nextSection == null) {
                            trimLength += 1;
                        } else {
                            trimLength += nextSection.length;
                        }
                    }

                    finalPattern.add(
                            sequencePattern(trimPattern(sectionPattern, section.length), track, startTime).add("\n"));
                }
            }

            startTime += section.length;
        }

        finalPattern.setTempo(tempo);

        return finalPattern;
    }

    private Pattern trimPattern(Pattern pattern, double duration) {
        patternTrimmer.setMaxDuration(duration);
        pattern.transform(patternTrimmer);

        return patternBuilder.getPattern();
    }

    private Pattern sequencePattern(Pattern pattern, int track, double time) {
        patternSequencer.setVoice((byte) track);
        patternSequencer.setTime(time);
        pattern.transform(patternSequencer);

        return patternBuilder.getPattern();
    }

    private class Section {
        private Pattern[] patterns = new Pattern[MidiDefaults.TRACKS];
        private float     length;
        private boolean   isBreak;

        private Section(float length) {
            this.length = length;
            isBreak = this.length < ConductorState.DEFAULT_WHOLES_PER_SEQUENCE;
        }

        private Pattern get(int track) {
            if (track < 0 || track >= MidiDefaults.TRACKS) {
                throw new IllegalArgumentException("Invalid track range - should be from 0 to 16, received " + track);
            }

            return patterns[track];
        }

        private void set(int track, Pattern pattern) {
            if (track < 0 || track >= MidiDefaults.TRACKS) {
                throw new IllegalArgumentException("Invalid track range - should be from 0 to 16, received " + track);
            }

            patterns[track] = pattern;
        }
    }
}
