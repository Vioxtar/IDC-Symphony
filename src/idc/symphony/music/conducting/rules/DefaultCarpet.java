package idc.symphony.music.conducting.rules;

import idc.symphony.music.ConductorState;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.Recurrence;

import java.util.Comparator;

public class DefaultCarpet extends AbstractRoleSetter {
    BandRole[] CARPET_ROLES = {BandRole.Carpet1, BandRole.Carpet2, BandRole.Carpet3};

    public DefaultCarpet() {
        this(true);
    }

    public DefaultCarpet(boolean createPatterns) {
        this.createPatterns = createPatterns;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        for (BandRole carpet : CARPET_ROLES) {
            if (!state.sequenceContext().roleExists(carpet)) {
                state.getFacultyMap().values().stream()
                        .filter((faculty)-> !state.sequenceContext().facultyHasRole(faculty.ID))
                        .max(Comparator.comparingInt(faculty -> faculty.events.size()))
                        .filter((faculty) -> faculty.events.size() > 0)
                        .ifPresent((faculty) -> setRole(state, faculty.ID, carpet));

                return true;
            }
        }

        return true;
    }
}
