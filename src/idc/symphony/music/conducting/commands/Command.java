package idc.symphony.music.conducting.commands;

import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.Prioritized;

/**
 * Conducting command used to change conductor state as well as affect composition
 */
public interface Command {

    /**
     * Command execution
     * @param state State of conductor executing the command
     * @return whether to keep executing
     */
    boolean execute(ConductorState state, Recurrence recurrence);

    default Prioritized<Command> prioritize(int priority) {
        return new Prioritized<>(priority, this);
    }
}
