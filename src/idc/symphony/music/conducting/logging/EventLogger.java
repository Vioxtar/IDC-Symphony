package idc.symphony.music.conducting.logging;

import idc.symphony.data.EventData;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.Command;
import idc.symphony.music.conducting.commands.Recurrence;

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

        logger.info(String.format("Event #%d (#%d total) parsed:", yearCounter, totalCounter));
        logger.fine(String.format("Faculty: %s", state.getCurrentEvent().faculty.name));
        logger.fine(String.format("Type: %s", state.getCurrentEvent().type.name));
        logger.fine(String.format("Title: %s", state.getCurrentEvent().description));

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
