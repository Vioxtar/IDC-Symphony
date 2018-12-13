package EventModel;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EventDataParser {

    // In charge of parsing event data

    File file;

    public EventDataParser(File file) {
        this.file = file;
    }

    // Creates a bunch of events, enlists them, and returns the list
    public ArrayList<Event> createEventList() {

        ArrayList<Event> list = new ArrayList<>();

        try (Database db = new DatabaseBuilder(file)
                .setReadOnly(true)
                .open()) {

            // Iterate & enlist
            Table table = db.getTable("Events");
            for (Row row : table) {
                Event event = makeEvent(row);
                list.add(event);
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        return list;
    }

    // The actual parsing of each event data is done here
    public Event makeEvent(Row row) {

        Event event = new Event();

        event.year = Short.parseShort(row.get("EventYear").toString());
        event.month = Byte.parseByte(row.get("EventMonth").toString());
        event.day = Byte.parseByte(row.get("EventDay").toString());

        return event;
    }

}
