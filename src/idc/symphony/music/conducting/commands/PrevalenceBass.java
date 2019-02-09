package idc.symphony.music.conducting.commands;

import idc.symphony.data.FacultyData;
import idc.symphony.music.band.Band;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.shared.ContextExtension;

public class PrevalenceBass extends AbstractRoleSetter {
    ContextExtension context;

    public PrevalenceBass(ContextExtension context) {
        this(context, true);
    }

    public PrevalenceBass(ContextExtension context, boolean createPatterns) {
        this.createPatterns = createPatterns;
        this.context = context;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        FacultyData CSFaculty = state.getFacultyMap().get(4);

        if (context.facultyPlayed.get(CSFaculty) || context.facultyPrevalence.get(CSFaculty) > 0) {
            if (context.intensity < 1.2) {
                setRole(state, CSFaculty.ID, BandRole.Carpet1);
            }
            else if (context.intensity < 1.8) {
                setRole(state, CSFaculty.ID, BandRole.Carpet2);
            } else {
                setRole(state, CSFaculty.ID, BandRole.Carpet3);
            }
        }

        return true;
    }
}
