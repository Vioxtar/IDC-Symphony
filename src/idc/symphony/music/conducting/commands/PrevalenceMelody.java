package idc.symphony.music.conducting.commands;

import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.shared.ContextExtension;

import java.util.Comparator;

public class PrevalenceMelody extends AbstractRoleSetter {
    private ContextExtension context;

    public PrevalenceMelody(ContextExtension context) {
        this(context, true);
    }

    public PrevalenceMelody(ContextExtension context, boolean createPatterns) {
        this.createPatterns = createPatterns;
        this.context = context;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        if (!state.sequenceContext().roleExists(BandRole.Melody)) {
            state.getFacultyMap().values().stream()
                    .filter((faculty)-> !state.sequenceContext().facultyHasRole(faculty.ID))
                    .max(Comparator.comparingDouble(faculty -> context.getFactoredPrevalence(faculty)))
                    .filter((faculty) -> context.getFactoredPrevalence(faculty) > 0.3)
                    .ifPresent((faculty) -> {
                        setRole(state, faculty.ID, BandRole.Melody);
                        context.facultyPlayed.put(faculty, true);
                    });
        }

        return true;
    }
}
