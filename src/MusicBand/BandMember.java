package MusicBand;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public interface BandMember {

    Pattern playMainMelody(float duration, int tempo, Key key);

    Pattern playSecondary(float duration, int tempo, Key key);

    Pattern playCarpet3(float duration, int tempo, Key key);

    Pattern playCarpet2(float duration, int tempo, Key key);

    Pattern playCarpet1(float duration, int tempo, Key key);

}
