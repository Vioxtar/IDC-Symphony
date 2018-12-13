package History.EventTypeModel;

public class EventType {

    Integer id;
    String name;

    public EventType() {
    }

    Integer getID() {
        return id;
    }

    String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }
}
