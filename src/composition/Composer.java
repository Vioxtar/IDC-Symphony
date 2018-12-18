package composition;

import org.apache.commons.io.FilenameUtils;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.staccato.StaccatoParserListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A JFugue wrapper for easy composition purposes
 */
public class Composer {

    private Pattern lastComposition;
    private int tempo;

    // The sequence of patterns to construct our composition from
    private Map<Integer, Sequence> sequences;

    // Keeps track of unique sequence IDs for easy sequence management
    private int seqID = 0;

    public Composer() {
        // Load all usable patterns
        sequences = new HashMap<>();

        this.tempo = 120; // JFuguge's default tempo
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    /**
     * Composes all sequences into a final composition
     */
    public Composer compose() {
        // Compose our composition
        Pattern composition = new Pattern();

        for (Map.Entry<Integer, Sequence> seqEntry : sequences.entrySet()) {
            Sequence seq = seqEntry.getValue();

            StaccatoParserListener patternBuilder = new StaccatoParserListener();
            SequenceTransformer transformer = new SequenceTransformer(seq);

            transformer.addParserListener(patternBuilder);
            Pattern patternToBeTransformed = seq.getPattern();

            patternToBeTransformed.transform(transformer);
            Pattern newPattern = patternBuilder.getPattern();

            composition.add("\n"); // Used to differentiate between different sequences, ignored by JFugue
            composition.add(newPattern);
        }

        composition.setTempo(tempo);

        lastComposition = composition;

        return this;
    }

    public Composer play() {
        // Play the composition
        Player player = new Player();
        player.play(lastComposition);

        return this;
    }

    public Composer print() {

        System.out.println(lastComposition.toString());

        return this;
    }

    /**
     * Adds a sequence to the composition.
     * @param pattern pattern to be used
     * @param instrument instrument to be used
     * @param voice target voice
     * @param time sequence start time
     * @return
     */
    public int addSequence(Pattern pattern, int instrument, int voice, float amp, float time) {

        seqID++;

        // Add the sequence
        Sequence seq = new Sequence(seqID, pattern, (byte)instrument, (byte)voice, amp, time);
        sequences.put(seqID, seq);

        // Return the sequence ID
        return seqID;
    }

    public Sequence getSequence(int seqID) {
        return sequences.getOrDefault(seqID, null);
    }

    /**
     * Removes a sequence related to a given ID from the composition.
     * @param id the id of the sequence
     */
    public void remSequence(int id) {
        sequences.remove(id);
    }
}
