package idc.symphony.db;

import idc.symphony.data.EventType;
import idc.symphony.data.FacultyData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EventTypesFactory {
    private static String SELECT_TYPES =
            "SELECT ID as TypeID, EventType as TypeName FROM EventTypes";

    public static Map<Integer, EventType> fromDB(Connection dbConnection) throws SQLException {
        HashMap<Integer, EventType> eventTypeMap = new HashMap<>();

        PreparedStatement selectFaculties = dbConnection.prepareStatement(SELECT_TYPES);
        ResultSet facultiesResult = selectFaculties.executeQuery();

        while (facultiesResult.next()) {
            int typeID = facultiesResult.getInt("TypeID");
            String typeName = facultiesResult.getString("TypeName");

            EventType eventType = new EventType(typeID, typeName);
            eventTypeMap.put(typeID, eventType);
        }

        return eventTypeMap;
    }
}
