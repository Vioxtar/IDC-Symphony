package idc.symphony.music.conducting.rules;

import idc.symphony.music.ConductorState;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.Recurrence;

import java.util.Comparator;

public class DefaultMelody extends AbstractRoleSetter {
    public DefaultMelody() {
        this(true);
    }

    public DefaultMelody(boolean createPatterns) {
        this.createPatterns = createPatterns;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        if (!state.sequenceContext().roleExists(BandRole.Melody)) {
            state.getFacultyMap().values().stream()
                    .filter((faculty)-> !state.sequenceContext().facultyHasRole(faculty.ID))
                    .max(Comparator.comparingInt(faculty -> state.sequenceContext().getEventCount(faculty.ID)))
                    .filter((faculty) -> state.sequenceContext().getEventCount(faculty.ID) > 0)
                    .ifPresent((faculty) -> setRole(state, faculty.ID, BandRole.Melody));
        }

        return true;
    }
}
