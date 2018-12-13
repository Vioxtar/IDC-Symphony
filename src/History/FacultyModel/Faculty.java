package History.FacultyModel;

public class Faculty {

    Integer id;
    String name;

    public Faculty() {
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