package idc.symphony.music.transformers;

import org.jfugue.parser.ChainingParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.Token;
import org.jfugue.provider.NoteProviderFactory;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParserListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Tunes a given melody according to a given chord progression
 */
public class ChordMelodyTuner extends ChainingParserListenerAdapter {
    private static final Intervals MELODY_INTERVALS_MAJOR = new Intervals("1 3 4 5 6");
    private static final Intervals MELODY_INTERVALS_MINOR = new Intervals("1 b3 4 5 b6");

    private List<Note>      chordRoots;
    private List<int[]>     chordHalfSteps;
    private Random          seed;

    private double beatTime;
    private double nextChordTime;
    private int    currentChord;
    private Note   lastNote;

    /**
     *  Create a chord melody tuner for a specific chord progression.
     *
     * @param chordRoots The root notes of the chord progression, along with the durations.
     * @param minorMajor True means major, false means minor.
     */
    public ChordMelodyTuner(Pattern chordRoots, List<Boolean> minorMajor, Random seed) {
        this.seed = seed;
        this.chordHalfSteps = new ArrayList<>();
        this.chordRoots = new ArrayList<>();

        List<Token> rootTokens = chordRoots.getTokens();

        if (rootTokens.size() != minorMajor.size()) {
            throw new IllegalArgumentException("Chord root notes and minor major indicators must be of equal size");
        }

        int noteIdx = 0;
        for (Token token : rootTokens) {
            if (token.getType() == Token.TokenType.NOTE) {
                Note chordRoot = NoteProviderFactory.getNoteProvider().createNote(token.toString());
                this.chordRoots.add(chordRoot);

                if (minorMajor.get(noteIdx)) {
                    chordHalfSteps.add(MELODY_INTERVALS_MAJOR.setRoot(chordRoot).toHalfstepArray());
                } else {
                    chordHalfSteps.add(MELODY_INTERVALS_MINOR.setRoot(chordRoot).toHalfstepArray());
                }
            }

            noteIdx++;
        }
    }

    public Pattern tune(Pattern melody) {
        StaccatoParserListener builder = new StaccatoParserListener();
        addParserListener(builder);
        melody.transform(this);
        removeParserListener(builder);

        return builder.getPattern();
    }

    @Override
    public void beforeParsingStarts() {
        super.beforeParsingStarts();
        currentChord = 0;
        beatTime = 0;
        nextChordTime = chordRoots.get(currentChord).getDuration();
        lastNote = null;
    }

    @Override
    public void fireNoteParsed(Note note) {
        if (beatTime >= nextChordTime) advanceChord();

        if (!note.isRest()) {
            note.setOriginalString(null);

            BiPredicate<Integer,Integer> filter = (noteHalfStep, chordHalfStep) -> true;

            if (lastNote != null) {
                if (lastNote.getValue() < note.getValue()) {
                    filter = (noteHalfStep, chordHalfStep) -> noteHalfStep <= chordHalfStep;
                } else if (lastNote.getValue() > note.getValue()) {
                    filter = (noteHalfStep, chordHalfStep) -> noteHalfStep >= chordHalfStep;
                }
            }

            setNearestNote(note, filter);
        }

        beatTime += note.getDuration();
        super.fireNoteParsed(note);
    }

    private void advanceChord() {
        currentChord++;

        if (chordHalfSteps.size() <= currentChord) {
            currentChord = 0;
        }

        nextChordTime += chordRoots.get(currentChord).getDuration();
    }

    /**
     * Given a note, finds the note with the
     * @param note
     * @param filter
     */
    private void setNearestNote(Note note, BiPredicate<Integer, Integer> filter) {
        int octave = note.getOctave();
        int halfstep = note.getPositionInOctave();

        int[] halfsteps = chordHalfSteps.get(currentChord);

        int minDistance = Math.abs(halfsteps[0] - halfstep);
        int minHalfstep = halfsteps[0];

        for (int chordHalfStep : chordHalfSteps.get(currentChord)) {
            if (filter.test(halfstep, chordHalfStep)) {
                int dist = Math.abs(chordHalfStep - halfstep);

                if (dist <= minDistance) {
                    if (dist == minDistance && seed.nextBoolean()) {
                        continue;
                    }

                    minDistance = dist;
                    minHalfstep = chordHalfStep;
                }
            }
        }

        note.setValue((byte)(octave * 12 + minHalfstep));
    }
}
