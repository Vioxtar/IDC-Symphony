package composition;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.rhythm.Rhythm;
import org.jfugue.theory.ChordProgression;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/*
* A JFugue wrapper for easy composition purposes
*/
public class Composer {

    // A map of usable patterns, whose keys are pattern names and values are pattern instances
    private Map<String, Pattern> usablePatterns;

    // The sequence of patterns to construct our composition from
    private Map<Integer, Sequence> sequences;

    // Keeps track of unique sequence IDs for easy sequence management
    private int seqID = 0;

    // Load all usable patterns from the pattern arsenal
    private void loadPatterns() { // TODO: We may not need this, instead just have the external class give the composer its own patterns
        loadPattern("Aye",
            new ChordProgression("I IV V")
                .distribute("7%6")
                .allChordsAs("$0 $0 $0 $0 $1 $1 $0 $0 $2 $1 $0 $0")
                .eachChordAs("$0ia100 $1ia80 $2ia80 $3ia80 $4ia100 $3ia80 $2ia80 $1ia80")
                .getPattern());

        loadPattern("Bye",
            new Rhythm()
                .addLayer("O..oO...O..oOO..")
                .addLayer("..S...S...S...S.")
                .addLayer("````````````````")
                .addLayer("...............+")
                .getPattern());
    }

    public void loadPatternsFromDirectory(File dir) {
        // TODO: Load a bunch of usable patterns from a directory
    }

    // Loads a pattern to be made usable by the composer
    public void loadPattern(String name, Pattern pattern) {
        usablePatterns.put(name, pattern);
    }

    public Composer() {
        // Load all usable patterns
        usablePatterns = new HashMap<>();
        loadPatterns();
    }

    // Called at the very end - plays the final composition
    public void play() {
        // Compose our composition
        Pattern composition = new Pattern();

        // TODO: iterate sequences, build the final pattern, consider time and shit, voice management too!

        // Play the composition
        Player player = new Player();
        player.play(composition);
    }

    // Add a sequence to the composition
    public int addSequence(String patternName, byte instrument, byte voice, float time, short reps) {
        seqID++;

        // Add the sequence
        Sequence seq = new Sequence(seqID, patternName, instrument, voice, time, reps);
        sequences.put(seqID, seq);

        // Return the sequence ID
        return seqID;
    }

    // Remove a sequence from the composition
    public void remSequence(int id) {
        sequences.remove(id);
    }

    // A sequence is simply a packet of information containing the sequence name, the instrument to be used,
    // the voice to be played on, the time the sequence should play, and the repetitions of the sequence.
    private class Sequence{

        int id; // We may not need this
        String patternName;
        byte instrument;
        byte voice;
        float time;
        short reps;

        public Sequence(int id, String patternName, byte instrument, byte voice, float time, short reps) {
            this.id = id;
            this.patternName = patternName;
            this.instrument = instrument;
            this.voice = voice;
            this.time = time;
            this.reps = reps;
        }
    }
}
