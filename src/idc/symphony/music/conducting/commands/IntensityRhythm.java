package idc.symphony.music.conducting.commands;

import idc.symphony.music.band.Band;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.shared.ContextExtension;

public class IntensityRhythm extends AbstractRoleSetter {
    private ContextExtension context;

    public IntensityRhythm(ContextExtension context) {
        this(context, true);
    }

    public IntensityRhythm(ContextExtension context, boolean createPatterns) {
        this.context = context;
        this.createPatterns = createPatterns;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        if (!state.sequenceContext().facultyHasRole(Band.RHYTHM_FACULTY)) {
            if (context.intensity < 1.2) {
                setRole(state, Band.RHYTHM_FACULTY, BandRole.RhythmRelaxed);
            }
            else if (context.intensity < 1.8) {
                setRole(state, Band.RHYTHM_FACULTY, BandRole.RhythmMedium);
            } else {
                setRole(state, Band.RHYTHM_FACULTY, BandRole.RhythmIntense);
            }
        }

        return true;
    }

}
