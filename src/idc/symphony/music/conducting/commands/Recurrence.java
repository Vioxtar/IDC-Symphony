package idc.symphony.music.conducting.commands;

/**
 * Recurrence levels for conducting commands
 */
public enum Recurrence {
    /**
     * Execute command on every event parsed
     */
    Event,
    /**
     * Execute command on every non-empty sequence parsed
     */
    Sequence,
    /**
     * Execute command on every empty year parsed
     */
    EmptyYear,
    /**
     * Execute command on every year parsed
     */
    Year,
    /**
     * Execute command after iteration on events finished
     */
    End
}
