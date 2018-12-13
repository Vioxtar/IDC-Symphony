package History.EventTypeModel;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EventTypeDataParser {

    // In charge of parsing faculty data

    File file;

    public EventTypeDataParser(File file) {
        this.file = file;
    }

    public Map<Integer, EventType> createEventTypesMap() {
        Map<Integer, EventType> map = new HashMap<Integer, EventType>();

        try (Database db = new DatabaseBuilder(file)
                .setReadOnly(true)
                .open()) {

            // Iterate & map
            Table table = db.getTable("EventTypes");
            for (Row row : table) {
                EventType eventType = makeEventType(row);
                map.put(eventType.getID(), eventType);
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        return map;
    }

    EventType makeEventType(Row row) {
        EventType eventType = new EventType();
        eventType.id = Integer.parseInt(row.get("ID").toString());
        eventType.name = row.get("EventType").toString();
        return eventType;
    }

}
