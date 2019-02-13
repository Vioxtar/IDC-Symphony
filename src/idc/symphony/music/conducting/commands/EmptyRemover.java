package idc.symphony.music.conducting.commands;

import idc.symphony.music.conducting.ConductorState;

/**
 * Removes empty year sequences if there was not enough of a gap between two empty year sequences, helping preserve
 * momentum.
 */
public class EmptyRemover implements Command {
    private int lastYear = -1;
    private int minGap;

    public EmptyRemover(int minGap) {
        this.minGap = minGap;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        if (state.getCurrentYear() >= state.getStructure().getMinYear() &&
            state.getCurrentYear() <= state.getStructure().getMaxYear()) {

            if (state.getCurrentYear() - lastYear < minGap)
            {
                state.getComposition().removeSection(state.getCurrentSequence());
                state.moveCurrentSequence(-1);
            }

            lastYear = state.getCurrentYear();
        }

        return true;
    }
}
