package idc.symphony.visual.scheduling;

public class FacultyRoleChanged implements VisualEvent {
    private double time;
    private int facultyID;
    private int roleID;

    public FacultyRoleChanged(double time, int facultyID, int roleID) {
        this.time = time;
        this.facultyID = facultyID;
        this.roleID = roleID;
    }

    @Override
    public double time() {
        return time;
    }

    public int facultyID() {
        return facultyID;
    }

    public int roleID() {
        return roleID;
    }
}
