package idc.symphony.stats;

import java.util.Comparator;

public class FacultyStat {
    public final int facultyID;
    public final boolean isStudyField;
    public int totalOccurences;
    public int yearOccurences;
    public int sequenceOccurences;

    public FacultyStat(int facultyID, boolean isStudyField) {
        this.facultyID = facultyID;
        this.isStudyField = isStudyField;
        this.totalOccurences = 0;
        this.yearOccurences = 0;
        this.sequenceOccurences = 0;
    }

    public static final Comparator<FacultyStat> SEQ_COMPARATOR =
            Comparator.comparingInt(a -> a.sequenceOccurences);
    public static final Comparator<FacultyStat> YEAR_COMPARATOR =
            Comparator.comparingInt(a -> a.yearOccurences);
    public static final Comparator<FacultyStat> TOTAL_COMPARATOR =
            Comparator.comparingInt(a -> a.totalOccurences);

}
