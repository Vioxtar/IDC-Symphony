package idc.symphony.stats;

import java.util.Comparator;

public class FacultyStat {
    public final int facultyID;
    public final String facultyName;
    public final boolean isStudyField;
    public int totalOccurences;
    public int yearOccurences;
    public int sequenceOccurences;
    public boolean hasntPlayed;

    public FacultyStat(int facultyID, boolean isStudyField, String facultyName) {
        this.facultyID = facultyID;
        this.facultyName = facultyName;
        this.isStudyField = isStudyField;
        this.hasntPlayed = true;
        this.totalOccurences = 0;
        this.yearOccurences = 0;
        this.sequenceOccurences = 0;
    }

    public static final Comparator<FacultyStat> SEQ_COMPARATOR =
            Comparator.comparingInt(a -> {
                if (a.hasntPlayed) {
                    a.hasntPlayed = false;
                    return -a.sequenceOccurences * 1000;
                }
                return -a.sequenceOccurences;
            });
    public static final Comparator<FacultyStat> YEAR_COMPARATOR =
            Comparator.comparingInt(a -> -a.yearOccurences);
    public static final Comparator<FacultyStat> TOTAL_COMPARATOR =
            Comparator.comparingInt(a -> -a.totalOccurences);

}
