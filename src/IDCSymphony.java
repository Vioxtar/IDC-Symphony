import EventModel.Event;
import EventModel.EventDataParser;

import java.io.File;
import java.util.ArrayList;


public class IDCSymphony {

    public static void main(String [] args) {

        String filePath = "data/IDC Events.accdb";
        EventDataParser eventDataParser = new EventDataParser(new File(filePath));
        ArrayList<Event> eventsList = eventDataParser.createEventList();

    }

}