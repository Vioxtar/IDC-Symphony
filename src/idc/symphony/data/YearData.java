package idc.symphony.data;

public class YearData {
    private int events;
    private int faculties;
    private int types;

    public YearData(int numEvents, int numFaculties, int types) {
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
