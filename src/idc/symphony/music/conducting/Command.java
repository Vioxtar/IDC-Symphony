package idc.symphony.music.conducting;

import idc.symphony.music.ConductorState;


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
