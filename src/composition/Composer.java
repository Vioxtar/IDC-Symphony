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

    // A map of usable patterns, whose keys are pattern names and values are pattern instances
    private Map<String, Pattern> usablePatterns;

    // The sequence of patterns to construct our composition from
    private Map<Integer, Sequence> sequences;

    // Keeps track of unique sequence IDs for easy sequence management
    private int seqID = 0;

    public Composer() {
        // Load all usable patterns
        usablePatterns = new HashMap<>();
        sequences = new HashMap<>();
    }


    /**
     * Loads a set of usable patterns from a file. The file object can also be a directory,
     * in which case all .jfugue pattern files in the directory will be loaded.
     * @return an array of all names whose patterns were successfully loaded
     */
    public String[] loadPatternsFromFile(File f) {

        String desiredExtension = "jfugue";

        String[] loadedNames = null;

        try {

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

            } else if (f.isFile()) { // Treat the given file as a single file

                String fileName = f.getName();
                if (FilenameUtils.getExtension(fileName).equals(desiredExtension)) {
                    Pattern pattern = Pattern.load(f);
                    String name = FilenameUtils.removeExtension(fileName);
                    loadPattern(name, pattern);

                    loadedNames = new String[1];
                    loadedNames[0] = name;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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

        for (Map.Entry<Integer, Sequence> seqEntry : sequences.entrySet()) {
            Sequence seq = seqEntry.getValue();

            StaccatoParserListener patternBuilder = new StaccatoParserListener();
            SequenceTransformer transformer = new SequenceTransformer(seq);

            transformer.addParserListener(patternBuilder);
            Pattern patternToBeTransformed = usablePatterns.get(seq.getPatternName());

            patternToBeTransformed.transform(transformer);
            Pattern newPattern = patternBuilder.getPattern();

            composition.add(newPattern);
        }

//        System.out.println("Final composition looks like this:");
//        System.out.println(composition);

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
     * @return
     */
    public int addSequence(String patternName, int instrument, int voice, float amp, float time) {

        seqID++;

        // Add the sequence
        Sequence seq = new Sequence(seqID, patternName, (byte)instrument, (byte)voice, amp, time);
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
}
