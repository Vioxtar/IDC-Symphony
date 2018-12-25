package idc.symphony.music.sequence;

import org.jfugue.parser.ChainingParserListenerAdapter;
import org.jfugue.theory.Note;

/**
 * Trims the beginning of MIDI patterns
 */
public class MidiTrimmer extends ChainingParserListenerAdapter {
    boolean firstBeat = true;
    boolean firstNote = true;
    double offset = 0;

    public void beforeParsingStarts() {
        firstBeat = true;
        firstNote = true;
    }

    public void onTrackBeatTimeRequested(double time) {
        if (firstBeat) {
            offset = time;
            firstBeat = false;
        }

        fireTrackBeatTimeRequested(time - offset);
    }

    public void onNoteParsed(Note note) {
        if (firstNote) {
            if (note.isRest()) {
                firstNote = false;
                fireNoteParsed(note);
            }
        } else {
            fireNoteParsed(note);
        }
    }
}
