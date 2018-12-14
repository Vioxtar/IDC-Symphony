import com.sun.org.glassfish.gmbal.ParameterNames;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.rhythm.Rhythm;
import org.jfugue.theory.ChordProgression;

import java.util.Map;

public class Composer {

    private Pattern symphony;
    private Map<String, Pattern> usablePatterns;

    private void loadPatterns() {
        putPattern("Aye",
            new ChordProgression("I IV V")
                .distribute("7%6")
                .allChordsAs("$0 $0 $0 $0 $1 $1 $0 $0 $2 $1 $0 $0")
                .eachChordAs("$0ia100 $1ia80 $2ia80 $3ia80 $4ia100 $3ia80 $2ia80 $1ia80")
                .getPattern()
                .setInstrument("Acoustic_Bass"));

        putPattern("Bye",
            new Rhythm()
                .addLayer("O..oO...O..oOO..")
                .addLayer("..S...S...S...S.")
                .addLayer("````````````````")
                .addLayer("...............+")
                .getPattern());

    }

    private void loadPattern(String name, Pattern pattern) {
        patterns.put(name, pattern);
    }

    public Composer() {
        symphony = new Pattern();
        putPatterns();
    }

    // Called at the very end
    public void playSymphony() {
        Player player = new Player();
        player.play(symphony);
    }

    public void addSeq(String seqName, byte instrument, byte voice, float time, short reps) {

        // TODO: Return an ID representing the pattern placement to be used in remSeq
    }

    // TODO: public void remSeq (some ID) {...}



}
