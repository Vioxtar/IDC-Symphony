import History.History;

import java.io.File;


public class IDCSymphony {

    public static void main(String [] args) {

        String filePath = "data/IDC Events.accdb";
        File dbFile = new File(filePath);

        // Build the events data class
        History history = new History(dbFile);

        // Sort the events list by date
        history.sortEvents();

        // Print all event information
        for (int i = 0; i < history.getEventCount(); i++) {
            System.out.println(history.getEvent(i));
        }

    }

}