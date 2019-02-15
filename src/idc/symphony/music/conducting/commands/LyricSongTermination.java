package idc.symphony.music.conducting.commands;

import idc.symphony.music.band.Band;
import idc.symphony.music.conducting.ConductorState;
import org.jfugue.pattern.Pattern;

/**
 * For visualization infographics - indicates the song is ending.
 */
public class LyricSongTermination implements Command{


    /**
     * Inserts a lyrical event to rhythm's track in the last section.
     * @param state State of conductor executing the command
     * @param recurrence
     * @return
     */
    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        int track = state.getBand().bandMembers().get(Band.RHYTHM_FACULTY).getTargetTrack();
        int section = state.getCurrentSequence() - 1;
        float duration = (state.getComposition().getSectionLength(section) * 4 * 60) / state.getTempo();

        Pattern prepended = new Pattern(String.format("'(TERMINATE:%f)", duration));
        state.getComposition().prepend(track, section, prepended);

        return false;
    }
}
