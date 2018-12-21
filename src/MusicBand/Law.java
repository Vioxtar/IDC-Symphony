package MusicBand;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Key;

public class Law implements BandMember {

    byte instrument;

    public Law() {
        this.instrument = 20;
    }

    public Pattern playMainMelody(float dur, int tempo, Key key) {
        return null;
    }

    public Pattern playSecondary(float dur, int tempo, Key key) {
        return null;
    }

    public Pattern playCarpet3(float dur, int tempo, Key key) {
        return null;
    }

    public Pattern playCarpet2(float dur, int tempo, Key key) {
        return null;
    }

    public Pattern playCarpet1(float dur, int tempo, Key key) {
        return null;
    }
}
