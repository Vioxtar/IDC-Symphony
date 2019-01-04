package idc.symphony.music.band;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public interface BandMember {

    /**
     * Plays (returns the pattern of) the main melody.
     * @param wholes the duration of the role in wholes
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playMainMelody(int wholes, Key key);

    /**
     * Plays (returns the pattern of) the secondary melody (harmony).
     * @param wholes the duration of the role in wholes
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playSecondary(int wholes, Key key);

    /**
     * Plays (returns the pattern of) the third carpet.
     * @param wholes the duration of the role in wholes
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playCarpet3(int wholes, Key key);

    /**
     * Plays (returns the pattern of) the second carpet.
     * @param wholes the duration of the role in wholes
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playCarpet2(int wholes, Key key);

    /**
     * Plays (returns the pattern of) the first carpet.
     * @param wholes the duration of the role in wholes
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playCarpet1(int wholes, Key key);

    int getTargetTrack();
}
