package idc.symphony.music.transformers;

import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import org.jfugue.parser.ChainingParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParserListener;

/**
 * Layer-based amplitude transformer.
 * @see AmplitudeTransformer
 */
public class LayerMasterer extends ChainingParserListenerAdapter
{
    private static LayerMasterer instance = new LayerMasterer();

    private float[] fractions;
    private int currentLayer;

    public static Pattern master(Pattern pattern, float... fractions) {
        instance.fractions = fractions;
        StaccatoParserListener builder = new StaccatoParserListener();
        instance.addParserListener(builder);
        pattern.transform(instance);
        instance.removeParserListener(builder);

        return builder.getPattern();
    }

    @Override
    public void onLayerChanged(byte layer) {
        super.onLayerChanged(layer);
        currentLayer = layer;
    }

    @Override
    public void onNoteParsed(Note note) {
        if(!note.isRest() && currentLayer < fractions.length) {
            note.setOnVelocity((byte)Math.round(note.getOnVelocity() * fractions[currentLayer]));
        }

        super.onNoteParsed(note);
    }
}
