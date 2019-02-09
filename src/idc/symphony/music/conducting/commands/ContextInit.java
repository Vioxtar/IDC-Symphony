package idc.symphony.music.conducting.commands;

import idc.symphony.data.FacultyData;
import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.conducting.commands.shared.ContextExtension;

import java.util.HashMap;

public class ContextInit implements Command {
    private ContextExtension context;

    public ContextInit(ContextExtension context) {
        this.context = context;
    }

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        context.facultyPrevalence = new HashMap<>();
        context.facultyPlayed = new HashMap<>();

        for (FacultyData faculty : state.getFacultyMap().values()) {
            context.facultyPrevalence.put(faculty, 0f);
            context.facultyPlayed.put(faculty, false);
        }

        return false;
    }
}
