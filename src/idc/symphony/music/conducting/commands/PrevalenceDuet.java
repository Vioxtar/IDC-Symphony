package idc.symphony.music.conducting.commands;

import idc.symphony.data.FacultyData;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.shared.ContextExtension;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PrevalenceDuet extends AbstractRoleSetter {
    private float maxCount = 0;
    private ContextExtension context;

    public PrevalenceDuet(ContextExtension context) {
        this(context,true);
    }

    public PrevalenceDuet(ContextExtension context, boolean createPatterns) {
        this.createPatterns = createPatterns;
        this.context = context;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        if (!state.sequenceContext().roleExists(BandRole.Melody)) {
            state.getFacultyMap().values().stream()
                    .max(Comparator.comparingDouble((faculty) -> context.facultyPrevalence.get(faculty)))
                    .ifPresent((faculty) -> maxCount = context.getFactoredPrevalence(faculty));

            if (maxCount > 0) {
                List<FacultyData> maxPair =
                        state.getFacultyMap().values().stream()
                                .filter((faculty)-> !state.sequenceContext().facultyHasRole(faculty.ID)
                                        && Math.abs(context.getFactoredPrevalence(faculty) - maxCount) < 0.1f)
                                .limit(2).collect(Collectors.toList());

                if (maxPair.size() == 2) {
                    setRole(state, maxPair.get(0).ID, BandRole.Melody);
                    context.facultyPlayed.put(maxPair.get(0), true);
                    setRole(state, maxPair.get(1).ID, BandRole.Secondary);
                    context.facultyPlayed.put(maxPair.get(1), true);
                }
            }
        }

        return true;
    }
}
