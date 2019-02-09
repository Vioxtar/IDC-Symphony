package idc.symphony.music.transformers;

import org.jfugue.parser.ChainingParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParserListener;

public class BassRhythmTransformer extends ChainingParserListenerAdapter {
    private static BassRhythmTransformer instance = new BassRhythmTransformer();
    private boolean[] rhythm = new boolean[]{true, true, false, true, true, false};

    public static Pattern toBassRhythm(Pattern pattern, boolean[] rhythm) {
        instance.rhythm = rhythm;
        StaccatoParserListener builder = new StaccatoParserListener();
        instance.addParserListener(builder);
        pattern.transform(instance);
        instance.removeParserListener(builder);

        return builder.getPattern();
    }

    @Override
    public void onNoteParsed(Note note) {
        if (note.isFirstNote()) {
            double newDuration = note.getDuration() / rhythm.length;

            for (int i = 0; i < rhythm.length; i++) {
                Note newNote = new Note(note);
                newNote.setDuration(newDuration);
                newNote.setRest(!rhythm[i]);
                newNote.changeValue(-24);
                newNote.setOctaveExplicitlySet(true);
                newNote.setOriginalString(null);

                super.onNoteParsed(newNote);
            }
        }
    }
}
