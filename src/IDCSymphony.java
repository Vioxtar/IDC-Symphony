import EventModel.Event;
import EventModel.EventDataParser;
import EventTypeModel.EventType;
import EventTypeModel.EventTypeDataParser;
import FacultyModel.FacultiesDataParser;
import FacultyModel.Faculty;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;


public class IDCSymphony {

    public static void main(String [] args) {

        String filePath = "data/IDC Events.accdb";
        File file = new File(filePath);

        EventDataParser eventDataParser = new EventDataParser(file);
        ArrayList<Event> eventsList = eventDataParser.createEventList();

        FacultiesDataParser facultyParser = new FacultiesDataParser(file);
        Map<String, Faculty> faculties = facultyParser.createFacultiesMap();

        EventTypeDataParser eventTypeParser = new EventTypeDataParser(file);
        Map<String, EventType> eventTypes = eventTypeParser.createEventTypesMap();

        
    }

}