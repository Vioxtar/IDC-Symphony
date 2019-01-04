package idc.symphony.visual.scheduling;

import idc.symphony.data.FacultyData;

public class FacultyJoined implements VisualEvent {
    public final double time;
    public final FacultyData faculty;

    public FacultyJoined(double time, FacultyData faculty){
        this.time = time;
        this.faculty = faculty;
    }

    @Override
    public double time() {
        return time;
    }
}
