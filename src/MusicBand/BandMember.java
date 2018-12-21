package MusicBand;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public interface BandMember {

    /**
     * Plays (returns the pattern of) the main melody.
     * @param t the duration of the role, in the smallest (128ths) note duration
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playMainMelody(int t, Key key);

    /**
     * Plays (returns the pattern of) the secondary melody (harmony).
     * @param t the duration of the role, in the smallest (128ths) note duration
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playSecondary(int t, Key key);

    /**
     * Plays (returns the pattern of) the third carpet.
     * @param t the duration of the role, in the smallest (128ths) note duration
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playCarpet3(int t, Key key);

    /**
     * Plays (returns the pattern of) the second carpet.
     * @param t the duration of the role, in the smallest (128ths) note duration
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playCarpet2(int t, Key key);

    /**
     * Plays (returns the pattern of) the first carpet.
     * @param t the duration of the role, in the smallest (128ths) note duration
     * @param key the key of the pattern to be returned
     * @return the resulting pattern
     */
    Pattern playCarpet1(int t, Key key);

}
