package idc.symphony.music.conducting.rules;

import idc.symphony.music.ConductorState;
import idc.symphony.music.band.Band;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.Command;
import idc.symphony.music.conducting.Recurrence;

public class EmptyRhythm extends AbstractRoleSetter implements Command {

    public EmptyRhythm() {
        this(true);
    }

    public EmptyRhythm(boolean createPatterns) {
        this.createPatterns = createPatterns;
    }


    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        if (!state.sequenceContext().facultyHasRole(Band.RHYTHM_FACULTY)) {
            setRole(state, Band.RHYTHM_FACULTY, BandRole.Rhythm);
        }
        return true;
    }
}
