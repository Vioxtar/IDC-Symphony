package idc.symphony.music.conducting.logging;

import idc.symphony.data.FacultyData;
import idc.symphony.music.ConductorState;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.Command;
import idc.symphony.music.conducting.Recurrence;

import java.util.logging.Logger;

public class SequenceLogger implements Command {
    Logger logger;

    public SequenceLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        for (FacultyData faculty : state.getFacultyMap().values()) {
            BandRole facultyRole = state.sequenceContext().facultyRole(faculty.ID);
            if (facultyRole != null) {
                logger.info(String.format(
                        "\tFaculty %s received role %s",
                        faculty.name,
                        facultyRole.name()));
            }
        }

        logger.info(String.format("Sequence #%d parsed.", state.getCurrentSequence()));
        return true;
    }
}
