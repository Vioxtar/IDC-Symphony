package idc.symphony.music.conducting.rules;

import idc.symphony.music.ConductorState;
import idc.symphony.music.band.BandMember;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.Command;

public abstract class AbstractRoleSetter implements Command {
    protected boolean createPatterns = true;

    protected void setRole(ConductorState state, int facultyID, BandRole role) {
        BandMember member = state.getBand().bandMembers().get(facultyID);
        BandRole.PatternGetter getter = role.getPattern(member);

        if (createPatterns) {
            state.getComposition().append(
                    member.getTargetTrack(),
                    state.getCurrentSequence(),
                    getter.apply(getDurationWhole(state), state.getSongKey()));
        }

        state.sequenceContext().setHasRole(facultyID, role);
    }

    protected int getDurationWhole(ConductorState state) {
        return (state.getStructure().eventsPerSequence(state.getCurrentYear()).length > 0)
                ? state.getWholesPerSequence()
                : state.getWholesPerEmpty();
    }
}
