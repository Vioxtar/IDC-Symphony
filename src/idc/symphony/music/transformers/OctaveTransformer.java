package idc.symphony.music.transformers;

import org.jfugue.parser.ChainingParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParserListener;

public class OctaveTransformer extends ChainingParserListenerAdapter {
    private static OctaveTransformer instance = new OctaveTransformer();
    private int offset;

    public static Pattern OffsetOctave(Pattern pattern, int offset) {
        instance.offset = offset;
        StaccatoParserListener builder = new StaccatoParserListener();
        instance.addParserListener(builder);
        pattern.transform(instance);
        instance.removeParserListener(builder);

        return builder.getPattern();
    }

    @Override
    public void onNoteParsed(Note note) {
        note.changeValue(offset * Note.OCTAVE);
        note.setOriginalString(null);
        super.onNoteParsed(note);
    }
}
