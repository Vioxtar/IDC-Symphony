package idc.symphony.music.conducting.commands;

import idc.symphony.data.EventData;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.band.BandMember;
import org.jfugue.pattern.Pattern;

/**
 * For visualization infographics - generates timed metadata message containing event data
 */
public class LyricEvents implements Command {

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        EventData toLyric = state.getCurrentEvent();
        if (toLyric != null) {
            BandMember member = state.getBand().bandMembers().get(toLyric.faculty.ID);
            Pattern eventLyric = new Pattern();
            eventLyric.add(String.format("'(EVENT:%d,%d,%s)",
                    toLyric.faculty.ID,
                    toLyric.year,
                    toLyric.description));

            state.getComposition().prepend(
                    member.getTargetTrack(),
                    state.getCurrentSequence(),
                    eventLyric
            );
        }

        return true;
    }
}
