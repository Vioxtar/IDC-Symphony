import EventModel.EventDataParser;

import java.io.File;


public class IDCSymphony {

    public static void main(String [] args) {

        String filePath = "data/IDC Events.accdb";
        EventDataParser eventDataParser = new EventDataParser(new File(filePath));
        eventDataParser.createEventList();

    }

}