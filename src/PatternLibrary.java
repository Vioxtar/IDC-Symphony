import org.apache.commons.io.FilenameUtils;
import org.jfugue.pattern.Pattern;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class PatternLibrary {
    // A map of usable patterns, whose keys are pattern names and values are pattern instances
    private Map<String, Pattern> patterns;

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
        patterns.put(name, pattern);
    }

    public Pattern getPattern(String name) {
        return patterns.get(name);
    }
}
