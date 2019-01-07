package idc.symphony.music.conducting.logging;

import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.Command;
import idc.symphony.music.conducting.commands.Recurrence;

import java.util.Arrays;
import java.util.logging.Logger;

public class YearLogger implements Command {
    Logger logger;

    public YearLogger(Logger logger) {
        this.logger = logger;
    }


    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        logger.info(String.format("Year %d parsed", state.getCurrentYear()));
        logger.fine(String.format("Events parsed: %d", state.yearContext().getEventCount()));
        logger.fine(String.format("Events per sequence: %s",
                Arrays.toString(state.getStructure().eventsPerSequence(state.getCurrentYear()))));

        return true;
    }
}
