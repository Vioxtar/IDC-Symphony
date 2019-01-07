package idc.symphony.music.conducting.commands;

import idc.symphony.music.conducting.ConductorState;
import idc.symphony.music.band.BandMember;
import idc.symphony.music.band.BandRole;
import org.jfugue.pattern.Pattern;

import java.util.HashSet;
import java.util.Set;

/**
 * For visualization infographics - generates
 */
public class LyricFacultyRoles implements Command {
    Set<Integer> joinedFaculties = new HashSet<>();
    int lastYear = -1;

    @Override
    public boolean execute(ConductorState state, Recurrence recurrence) {
        boolean yearTagged = false;

        for (int facultyID : state.getFacultyMap().keySet()) {
            BandMember member = state.getBand().bandMembers().get(facultyID);
            BandRole role = state.sequenceContext().facultyRole(facultyID);

            if (role != null) {
                Pattern prepended = new Pattern();
                Pattern appended = new Pattern();

                if (lastYear != state.getCurrentYear() && state.getCurrentYear() >= state.getStructure().getMinYear() &&
                        !yearTagged) {
                    lastYear = state.getCurrentYear();
                    prepended.add(String.format("'(YEAR:%d)", state.getCurrentYear()));
                    yearTagged = true;
                }

                if (!joinedFaculties.contains(facultyID)) {
                    joinedFaculties.add(facultyID);
                    prepended.add(String.format("'(JOINED:%d)", facultyID));
                }

                prepended.add(String.format("'(ROLE:%d,%d)", facultyID, role.ordinal()));
                appended.add(String.format("'(ROLE:%d,%d)", facultyID, role.ordinal()));

                state.getComposition().prepend(
                    member.getTargetTrack(),
                    state.getCurrentSequence(),
                    prepended
                );

                state.getComposition().append(
                    member.getTargetTrack(),
                    state.getCurrentSequence(),
                    appended
                );
            }
        }

        return true;
    }
}
