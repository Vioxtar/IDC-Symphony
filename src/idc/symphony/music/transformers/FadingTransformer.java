package idc.symphony.music.transformers;

import org.jfugue.parser.ChainingParserListenerAdapter;
import org.jfugue.theory.Note;

/**
 * Fades a pattern via amplitude, cutting off notes that play past a certain point and ensuring the fading portion
 * isn't busy.
 */
public class FadingTransformer extends ChainingParserListenerAdapter {
    private double fadingStart;
    private double fadingEnd;
    private double cutOffPoint;

    private Note    lastNote;
    private int     notesSinceLastRest;
    private double  beatTime;

    public FadingTransformer(double fadingStart, double fadingEnd, double cutOffPoint) {
        this.fadingStart = fadingStart;
        this.fadingEnd = fadingEnd;
        this.cutOffPoint = cutOffPoint;
    }

    @Override
    public void beforeParsingStarts() {
        super.beforeParsingStarts();
        beatTime = 0;
    }

    @Override
    public void onNoteParsed(Note note) {
        if (beatTime >= fadingStart) {
            float coeff = (float)(beatTime - fadingStart);
            coeff /= (float)(fadingEnd - fadingStart);
            coeff = 1 - coeff;

            note.setOnVelocity((byte)(note.getOnVelocity() * coeff));
        }

        if (lastNote == null) {
            lastNote = note;
        } else {
            if (!note.isRest() && !lastNote.isRest()) {
                lastNote.setDuration(lastNote.getDuration() + note.getDuration());
                notesSinceLastRest++;

                if (notesSinceLastRest > 3) {
                    turnIntoRest(lastNote);
                }

                fireNote(lastNote);
            } else {
                fireNote(lastNote);
                fireNote(note);

                notesSinceLastRest = note.isRest() ? 0 : 1;
            }

            lastNote = null;
        }

        beatTime += note.getDuration();

    }

    private void mergeNotes() {

    }

    /**
     * turns a given note into a rest note
     * @param note
     */
    private void turnIntoRest(Note note) {
        note.setRest(true);
        note.setOriginalString(null);
        notesSinceLastRest = 0;
    }

    private void fireNote(Note note) {
        if (beatTime <= cutOffPoint) {
            super.onNoteParsed(note);
        }
    }
}
