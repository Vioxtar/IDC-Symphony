package Stats;

public class YearStat {
    int events;
    int faculties;
    int types;

    public YearStat(int numEvents, int numFaculties, int types) {
        this.events = numEvents;
        this.faculties = numFaculties;
        this.types = types;
    }

    public int events() {
        return events;
    }

    public int faculties() {
        return faculties;
    }

    public int types() { return types; }
}
