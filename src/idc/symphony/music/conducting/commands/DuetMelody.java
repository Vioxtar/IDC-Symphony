package idc.symphony.music.conducting.commands;

import idc.symphony.data.FacultyData;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.band.BandRole;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DuetMelody extends AbstractRoleSetter {
    int maxCount = 0;

    public DuetMelody() {
        this(true);
    }

    public DuetMelody(boolean createPatterns) {
        this.createPatterns = createPatterns;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        if (!state.sequenceContext().roleExists(BandRole.Melody)) {
            state.getFacultyMap().values().stream()
                    .max(Comparator.comparingInt((faculty) -> state.sequenceContext().getEventCount(faculty.ID)))
                    .ifPresent((faculty) -> maxCount = state.sequenceContext().getEventCount(faculty.ID));

            if (maxCount > 0) {
                List<FacultyData> maxPair =
                        state.getFacultyMap().values().stream()
                                .filter((faculty)-> !state.sequenceContext().facultyHasRole(faculty.ID)
                                        && state.sequenceContext().getEventCount(faculty.ID) == maxCount)
                                .limit(2).collect(Collectors.toList());

                if (maxPair.size() == 2) {
                    setRole(state, maxPair.get(0).ID, BandRole.Melody);
                    setRole(state, maxPair.get(1).ID, BandRole.Secondary);
                }
            }
        }

        return true;
    }
}
