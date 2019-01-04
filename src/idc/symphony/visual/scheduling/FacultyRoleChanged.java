package idc.symphony.visual.scheduling;

import idc.symphony.data.FacultyData;
import idc.symphony.music.band.BandRole;

public class FacultyRoleChanged implements VisualEvent {
    public final double time;
    public final FacultyData faculty;
    public final BandRole    role;

    public FacultyRoleChanged(double time, FacultyData faculty, BandRole role) {
        this.time = time;
        this.faculty = faculty;
        this.role = role;
    }

    @Override
    public double time() {
        return time;
    }
}
