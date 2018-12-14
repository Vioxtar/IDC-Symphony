import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.rhythm.Rhythm;
import org.jfugue.theory.ChordProgression;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/*
* A JFugue wrapper for easy composition purposes
*/
public class Composer {

    // A map of usable patterns, whose keys are pattern names and values are pattern instances
    private Map<String, Pattern> loadedPatterns;

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
                .getPattern()
                .setInstrument("Acoustic_Bass"));

        loadPattern("Bye",
            new Rhythm()
                .addLayer("O..oO...O..oOO..")
                .addLayer("..S...S...S...S.")
                .addLayer("````````````````")
                .addLayer("...............+")
                .getPattern());
    }

    public void loadPatternsFromDirectory(File dir) {
        // TODO: Implement :DDD
    }

    // Loads a pattern to be made usable by the composer
    public void loadPattern(String name, Pattern pattern) {
        loadedPatterns.put(name, pattern);
    }

    public Composer() {
        // Load all usable patterns
        loadPatterns();
    }

    // Called at the very end - plays the final composition
    public void play() {
        // Compose our composition
        Pattern composition = new Pattern();

        // Play the composition
        Player player = new Player();
        player.play(composition);
    }

    // Add a sequence to the composition
    public int addSequence(String seqName, byte instrument, byte voice, float time, short reps) {

        seqID++;

        // Add the sequence
        Sequence seq = new Sequence(seqName, instrument, voice, time, reps);
        sequences.put(seqID, seq);

        // Return the sequence ID

        return seqID;

        // TODO: Return an ID representing the pattern placement to be used in remSeq
    }

    // Remove a sequence from the composition
    public void remSequence(int id) {
        sequences.remove(id);
    }

    // A sequence is simply a packet of information containing the sequence name, the instrument to be used,
    // the voice to be played on, the time the sequence should play, and the repetitions of the sequence.
    private class Sequence{

        String seqName;
        byte instrument;
        byte voice;
        float time;
        short reps;

        public Sequence(String seqName, byte instrument, byte voice, float time, short reps) {
            this.seqName = seqName;
            this.instrument = instrument;
            this.voice = voice;
            this.time = time;
            this.reps = reps;
        }
    }
}
