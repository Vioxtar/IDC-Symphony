package idc.symphony.music.conducting.commands;

import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.band.Band;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.commands.shared.ContextExtension;

public class EmptyRhythm extends AbstractRoleSetter implements Command {
    public ContextExtension context;

    public EmptyRhythm() {
        this(true);
    }

    public EmptyRhythm(boolean createPatterns) {
        this(null, true);
    }

    public EmptyRhythm(ContextExtension context) {
        this(context, true);
    }

    public EmptyRhythm(ContextExtension context, boolean createPatterns) {
        this.context = context;
        this.createPatterns = createPatterns;
    }


    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        if (!state.sequenceContext().facultyHasRole(Band.RHYTHM_FACULTY)) {
            if (context != null &&
                    state.getCurrentYear() >= state.getStructure().getMinYear() &&
                    state.getCurrentYear() <= state.getStructure().getMinYear()) {
                if (context.intensity < 1.2) {
                    setRole(state, Band.RHYTHM_FACULTY, BandRole.RhythmRelaxed);
                }
                else if (context.intensity < 1.8) {
                    setRole(state, Band.RHYTHM_FACULTY, BandRole.RhythmMedium);
                } else {
                    setRole(state, Band.RHYTHM_FACULTY, BandRole.RhythmIntense);
                }
            } else {
                setRole(state, Band.RHYTHM_FACULTY, BandRole.RhythmRelaxed);
            }
        }
        return true;
    }
}
