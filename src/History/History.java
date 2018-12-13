package History;

import History.EventTypeModel.EventType;
import History.EventTypeModel.EventTypeDataParser;
import History.FacultyModel.FacultiesDataParser;
import History.FacultyModel.Faculty;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class History {

    // A History is the triplet of parsed faculties/event types, and the event list itself

    File file;
    ArrayList<Event> eventList;
    Map<Integer, Faculty> faculties;
    Map<Integer, EventType> eventTypes;

    public History(File file) {
        this.file = file;

        // Parse faculties & event types
        FacultiesDataParser facultyParser = new FacultiesDataParser(file);
        this.faculties = facultyParser.createFacultiesMap();

        EventTypeDataParser eventTypeParser = new EventTypeDataParser(file);
        this.eventTypes = eventTypeParser.createEventTypesMap();

        // Build the events list
        EventListBuilder eventListBuilder = new EventListBuilder(this);
        this.eventList = eventListBuilder.buildEventsList();
    }

    public Event getEvent(int i) {
        return this.eventList.get(i);
    }

    public void sortEvents() {
        Collections.sort(this.eventList);
    }

    public ArrayList<Event> getEventList() {
        return this.eventList;
    }

    public int getEventCount() {
        return this.eventList.size();
    }

    public Map<Integer, Faculty> getFaculties() {
        return this.faculties;
    }

    public Map<Integer, EventType> getEventTypes() {
        return this.eventTypes;
    }

    // A nested event list builder
    private static class EventListBuilder {

        History parent;

        EventListBuilder(History parent) {
            this.parent = parent;
        }

        // Creates a bunch of events, enlists them, and returns the list
        ArrayList<Event> buildEventsList() {

            // Then parse the events

            ArrayList<Event> list = new ArrayList<>();

            try (Database db = new DatabaseBuilder(parent.file)
                    .setReadOnly(true)
                    .open()) {

                // Iterate & enlist
                Table table = db.getTable("Events");
                for (Row row : table) {
                    // Pass the faculties and eventTypes to tie their value instances to each event
                    Event event = makeEvent(row);
                    list.add(event);
                }

            } catch (IOException e){
                e.printStackTrace();
            }

            return list;
        }

        // The actual parsing of each event data is done here
        private Event makeEvent(Row row) {

            Event event = new Event();

            event.year = Short.parseShort(row.get("EventYear").toString());
            event.month = Byte.parseByte(row.get("EventMonth").toString());
            event.day = Byte.parseByte(row.get("EventDay").toString());

            event.type = parent.eventTypes.get(Integer.parseInt(row.get("EventType").toString()));
            event.faculty = parent.faculties.get(Integer.parseInt(row.get("EventFaculty").toString()));

            event.shortDescription = row.get("EventShortDescription").toString();
            event.richDescription = row.get("EventRichDescription").toString();

            return event;
        }

    }
}
