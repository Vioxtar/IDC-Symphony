package FacultyModel;

import EventModel.Event;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FacultiesDataParser {

    // In charge of parsing faculty data

    File file;

    public FacultiesDataParser(File file) {
        this.file = file;
    }

    public Map<String, Faculty> createFacultiesMap() {
        Map<String, Faculty> map = new HashMap<String, Faculty>();

        try (Database db = new DatabaseBuilder(file)
                .setReadOnly(true)
                .open()) {

            // Iterate & map
            Table table = db.getTable("Faculties");
            for (Row row : table) {
                Faculty faculty = makeFaculty(row);
                map.put(faculty.getName(), faculty);
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        return map;
    }

    Faculty makeFaculty(Row row) {
        Faculty faculty = new Faculty();
        faculty.name = row.get("FacultyName").toString();
        return faculty;
    }

}
