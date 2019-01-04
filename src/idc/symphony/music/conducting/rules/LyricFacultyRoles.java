package idc.symphony.music.conducting.rules;

import idc.symphony.music.ConductorState;
import idc.symphony.music.band.BandMember;
import idc.symphony.music.band.BandRole;
import idc.symphony.music.conducting.Command;
import idc.symphony.music.conducting.Recurrence;
import org.jfugue.pattern.Pattern;

import java.util.HashSet;
import java.util.Set;

public class LyricFacultyRoles implements Command {
    Set<Integer> joinedFaculties = new HashSet<>();
    int lastYear = -1;

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        for (int facultyID : state.getFacultyMap().keySet()) {
            BandMember member = state.getBand().bandMembers().get(facultyID);
            BandRole role = state.sequenceContext().facultyRole(facultyID);

            if (role != null) {
                Pattern facultyLyric = new Pattern();

                if (lastYear != state.getCurrentYear() && state.getCurrentYear() >= state.getStructure().getMinYear()) {
                    lastYear = state.getCurrentYear();
                    facultyLyric.add(String.format("'(YEAR:%d)", state.getCurrentYear()));
                }

                if (!joinedFaculties.contains(facultyID)) {
                    joinedFaculties.add(facultyID);
                    facultyLyric.add(String.format("'(JOINED:%d)", facultyID));
                }

                facultyLyric.add(String.format("'(ROLE:%d,%d)", facultyID, role.ordinal()));

                state.getComposition().prepend(
                    member.getTargetTrack(),
                    state.getCurrentSequence(),
                    facultyLyric
                );
            }
        }

        return true;
    }
}