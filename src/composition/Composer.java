package composition;

import org.apache.commons.io.FilenameUtils;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A JFugue wrapper for easy composition purposes
 */
public class Composer {

    // A map of usable patterns, whose keys are pattern names and values are pattern instances
    private Map<String, Pattern> usablePatterns;

    // The sequence of patterns to construct our composition from
    private Map<Integer, Sequence> sequences;

    // Keeps track of unique sequence IDs for easy sequence management
    private int seqID = 0;


    public Composer() {
        // Load all usable patterns
        usablePatterns = new HashMap<>();
    }


    /**
     * Loads a set of usable patterns from a file. The file object can also be a directory,
     * in which case all .jfugue pattern files in the directory will be loaded.
     * @return an array of all names whose patterns were successfully loaded
     */
    public String[] loadPatternsFromFile(File f) throws IOException {

        String desiredExtension = ".jfugue";

        String[] loadedNames = null;

        // Treat the given file as a directory
        if (f.isDirectory()) {

            ArrayList<String> goodNamesList = new ArrayList<>();

            File[] filesInDir = f.listFiles();
            if (filesInDir != null) {
                for (File file : filesInDir) {
                    String fileName = file.getName();
                    if (FilenameUtils.getExtension(fileName).equals(desiredExtension)) {
                        Pattern pattern = Pattern.load(file);
                        String name = FilenameUtils.removeExtension(fileName);
                        loadPattern(name, pattern);

                        goodNamesList.add(name);
                    }
                }
            }

            int goodNamesCount = goodNamesList.size();
            loadedNames = new String[goodNamesCount];
            for (int i = 0; i < goodNamesCount; i++) {
                loadedNames[i] = goodNamesList.get(i);
            }

        // Treat the given file as a single file
        } else if (f.isFile()) {

            String fileName = f.getName();
            if (FilenameUtils.getExtension(fileName).equals(desiredExtension)) {
                Pattern pattern = Pattern.load(f);
                String name = FilenameUtils.removeExtension(fileName);
                loadPattern(name, pattern);

                loadedNames = new String[1];
                loadedNames[0] = name;
            }

        }

        return loadedNames;
    }

    /**
     * Loads a usable pattern into the composer. The same pattern may then be re-used in multiple sequences.
     * @param name a unique string representing the name of the pattern
     * @param pattern the pattern object - built in property tokens (e.g. instrument/voice)  will be ignored
     */
    public void loadPattern(String name, Pattern pattern) {
        usablePatterns.put(name, pattern);
    }

    /**
     * Composes all sequences into a final composition, and plays it.
     */
    public void play() {
        // Compose our composition
        Pattern composition = new Pattern();

        // TODO: iterate sequences, build the final pattern, consider time and shit, voice management too!

        // Play the composition
        Player player = new Player();
        player.play(composition);
    }

    /**
     * Adds a sequence to the composition.
     * @param patternName name of the pattern to be used (must be loaded in the composer in advance)
     * @param instrument instrument to be used
     * @param voice target voice
     * @param time sequence start time
     * @param reps repetitions count
     * @return
     */
    public int addSequence(String patternName, byte instrument, byte voice, float time, short reps) {

        seqID++;

        // Add the sequence
        Sequence seq = new Sequence(seqID, patternName, instrument, voice, time, reps);
        sequences.put(seqID, seq);

        // Return the sequence ID
        return seqID;
    }

    /**
     * Removes a sequence related to a given ID from the composition.
     * @param id the id of the sequence
     */
    public void remSequence(int id) {
        sequences.remove(id);
    }

    /**
     * A sequence is a packet of information containing:
     * - the sequence name
     * - the instrument to be used
     * - the target voice
     * - the sequence start time
     * - the sequence repetition count
     */
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
