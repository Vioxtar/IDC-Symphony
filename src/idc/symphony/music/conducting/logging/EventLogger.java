package idc.symphony.music.conducting.logging;

import idc.symphony.data.EventData;
import idc.symphony.music.ConductorState;
import idc.symphony.music.conducting.Command;
import idc.symphony.music.conducting.Recurrence;

import java.util.logging.Logger;

public class EventLogger implements Command {
    private Logger logger;
    int totalCounter = 0;
    int yearCounter = 0;
    int currentYear = -1;

    public EventLogger(Logger logger) {
        this.logger = logger;
    }


    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        updateCounters(state);

        logger.fine(String.format("\t\tFaculty: %s", state.getCurrentEvent().faculty.name));
        logger.fine(String.format("\t\tType: %s", state.getCurrentEvent().type.name));
        logger.fine(String.format("\t\tTitle: %s", state.getCurrentEvent().description));
        logger.info(String.format("\tEvent #%d (#%d total) parsed", yearCounter, totalCounter));

        checkInvalidState(state);

        return true;
    }

    private void checkInvalidState(ConductorState state) {
        EventData currentEvent = state.getCurrentEvent();
        if (state.getCurrentYear() != currentEvent.year) {
            logger.warning(
                    String.format("Invalid Event Year: %d, expected %d",
                        currentEvent.year,
                        state.getCurrentYear()));
        }
        if (state.getCurrentSequence() != currentEvent.sequence) {
            logger.warning(
                    String.format("Invalid Event Sequence: %d, expected %d",
                            currentEvent.sequence,
                            state.getCurrentSequence()));
        }
    }

    private void updateCounters(ConductorState state) {
        if (state.getCurrentYear() != currentYear) {
            yearCounter = 0;
            currentYear = state.getCurrentYear();
        }

        yearCounter++;
        totalCounter++;
    }
}
