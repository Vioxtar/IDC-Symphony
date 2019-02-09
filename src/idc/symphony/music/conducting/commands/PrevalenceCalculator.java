package idc.symphony.music.conducting.commands;

import idc.symphony.data.FacultyData;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.shared.ContextExtension;

/**
 * Calculates exponential roll off prevalence value for faculties, based on number of events in sequence.
 */
public class PrevalenceCalculator implements Command {
    ContextExtension context;
    float rolloff;

    /**
     * Calculates exponential roll off prevalence value for faculties, based on number of events in sequence.
     * @param context Shared command context containing faculty prevalence map
     * @param rolloff Rolloff coefficient (0.0 to 1.0)
     */
    public PrevalenceCalculator(ContextExtension context, float rolloff) {
        this.context = context;
        this.rolloff = rolloff;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        float newIntensity = 0;

        for (FacultyData faculty : context.facultyPrevalence.keySet()) {
            float previous = Math.min(context.facultyPrevalence.get(faculty), 2);
            float current = state.sequenceContext().getEventCount(faculty.ID);
            float prevalence = (1 - rolloff) * previous + (rolloff) * current;

            newIntensity += prevalence;
            context.facultyPrevalence.put(faculty, prevalence);
        }

        context.intensity = newIntensity;
        System.out.println(String.format("Seq %d, Intensity %f", state.getCurrentSequence(), context.intensity));
        return true;
    }
}
