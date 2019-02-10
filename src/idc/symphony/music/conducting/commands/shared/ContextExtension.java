package idc.symphony.music.conducting.commands.shared;

import idc.symphony.data.FacultyData;
import java.util.Map;

public class ContextExtension {
    public Map<FacultyData, Float> facultyPrevalence;
    public Map<FacultyData, Boolean> facultyPlayed;
    public float intensity = 0;

    public ContextExtension() {
    }

    public float getFactoredPrevalence(FacultyData faculty) {
        float result = facultyPrevalence.get(faculty);

        /*if (!facultyPlayed.get(faculty)) {
            result *= 2f;
        }*/

        return result;
    }
}
