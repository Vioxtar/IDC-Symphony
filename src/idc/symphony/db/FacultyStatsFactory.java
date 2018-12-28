package idc.symphony.db;

import idc.symphony.stats.FacultyStat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FacultyStatsFactory {
    private static String SELECT_FACULTIES =
            "SELECT ID as FacultyID, FacultyName, StudyField FROM Faculties";

    public static Map<Integer, FacultyStat> fromDB(Connection dbConnection) throws SQLException {
        HashMap<Integer, FacultyStat> facultyStatMap = new HashMap<>();

        PreparedStatement selectFaculties = dbConnection.prepareStatement(SELECT_FACULTIES);
        ResultSet facultiesResult = selectFaculties.executeQuery();

        while (facultiesResult.next()) {
            int facultyID = facultiesResult.getInt("FacultyID");
            String facultyName = facultiesResult.getString("FacultyName");
            boolean studyField = facultiesResult.getBoolean("StudyField");

            FacultyStat facultyStat = new FacultyStat(facultyID, studyField, facultyName);
            facultyStatMap.put(facultyID, facultyStat);
        }

        return facultyStatMap;
    }
}
