package idc.symphony.music.conducting.commands;

import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.shared.ContextExtension;

import java.util.Comparator;

public class PrevalenceCarpet extends AbstractRoleSetter{
    private BandRole[] CARPET_ROLES = {BandRole.Carpet1, BandRole.Carpet2, BandRole.Carpet3};
    private ContextExtension context;

    public PrevalenceCarpet(ContextExtension context) {
        this(context,true);
    }

    public PrevalenceCarpet(ContextExtension context, boolean createPatterns) {
        this.createPatterns = createPatterns;
        this.context = context;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        for (BandRole carpet : CARPET_ROLES) {
            if (!state.sequenceContext().roleExists(carpet)) {
                state.getFacultyMap().values().stream()
                        .filter((faculty)-> !state.sequenceContext().facultyHasRole(faculty.ID))
                        .max(Comparator.comparingInt(faculty -> faculty.events.size()))
                        .filter((faculty) -> faculty.events.size() > 0)
                        .ifPresent((faculty) -> {
                            setRole(state, faculty.ID, carpet);
                            context.facultyPlayed.put(faculty, true);
                        });

                return true;
            }
        }

        return true;
    }
}
