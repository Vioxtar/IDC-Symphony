package idc.symphony.music.transformers;

import org.jfugue.parser.ChainingParserListenerAdapter;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParserListener;

/**
 * Multiplies all note amplitudes by a given fraction,
 * effectively controlling pattern volume
 */
public class AmplitudeTransformer extends ChainingParserListenerAdapter {
    private static AmplitudeTransformer instance = new AmplitudeTransformer();

    private float fraction;

    public static Pattern setRelativeAmp(float fraction, Pattern pattern) {
        instance.fraction = fraction;
        StaccatoParserListener builder = new StaccatoParserListener();
        instance.addParserListener(builder);
        pattern.transform(instance);
        instance.removeParserListener(builder);

        return builder.getPattern();
    }

    private AmplitudeTransformer() {
        // Un-instantiable
    }

    @Override
    public void onNoteParsed(Note note) {
        if (!note.isRest()) {
            note.setOnVelocity((byte)Math.round(note.getOnVelocity() * fraction));
        }

        fireNoteParsed(note);
    }
}
