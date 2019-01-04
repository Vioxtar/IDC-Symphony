package idc.symphony.music.conducting.logging;

import idc.symphony.music.ConductorState;
import idc.symphony.music.conducting.Command;
import idc.symphony.music.conducting.Recurrence;

import java.util.Arrays;
import java.util.logging.Logger;

public class YearLogger implements Command {
    Logger logger;

    public YearLogger(Logger logger) {
        this.logger = logger;
    }


    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        logger.info(String.format("\tEvents parsed: %d", state.yearContext().getEventCount()));
        logger.info(String.format("\tEvents per sequence: %s",
                Arrays.toString(state.getStructure().eventsPerSequence(state.getCurrentYear()))));
        logger.info(String.format("Year %d parsed", state.getCurrentYear()));

        return true;
    }
}
