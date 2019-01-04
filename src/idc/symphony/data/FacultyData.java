package idc.symphony.data;

import java.util.ArrayList;
import java.util.List;

public class FacultyData {
    public final int ID;
    public final String name;
    public final boolean isStudyField;
    public final List<EventData> events;
    public final FacultyData parent;

    public FacultyData(int facultyID, boolean isStudyField, String facultyName, FacultyData parent) {
        this.ID = facultyID;
        this.name = facultyName;
        this.isStudyField = isStudyField;
        this.events = new ArrayList<>();
        this.parent = parent;
    }
}
