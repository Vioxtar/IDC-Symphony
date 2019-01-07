package idc.symphony.db;

import idc.symphony.data.FacultyData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *  Creates Faculty data structure, such that each faculty has a parent faculty, apart form a single root level faculty.
 *  Tree structure to be used for visualization.
 */
public class FacultyDataFactory {
    /**
     * Hierarchical select - union automatically calculates by order of dependency,
     * so Level counter allows us to inject parent FacultyDatas in one iteration.
     */
    private static String SELECT_FACULTIES =
            "WITH hierarchy AS (\n" +
                "    SELECT ID,\n" +
                "           FacultyName,\n" +
                "           StudyField,\n" +
                "           ParentFaculty,\n" +
                "           CAST (0 AS VARBINARY (100) ) AS Level\n" +
                "      FROM Faculties\n" +
                "     WHERE ParentFaculty is NULL\n" +
                "    UNION ALL\n" +
                "    SELECT b.ID,\n" +
                "           b.FacultyName,\n" +
                "           b.StudyField,\n" +
                "           b.ParentFaculty,\n" +
                "           a.Level + CAST (1 AS VARBINARY (100) ) AS Level\n" +
                "      FROM Faculties AS b\n" +
                "           INNER JOIN\n" +
                "           hierarchy a ON a.ID = b.ParentFaculty\n" +
                ")\n" +
            "SELECT ID AS FacultyID,\n" +
            "       FacultyName,\n" +
            "       StudyField,\n" +
            "       ParentFaculty AS ParentID\n" +
            "  FROM hierarchy\n" +
            " ORDER BY Level;\n";

    public static Map<Integer, FacultyData> fromDB(Connection dbConnection) throws SQLException {
        HashMap<Integer, FacultyData> facultyMap = new HashMap<>();

        PreparedStatement selectFaculties = dbConnection.prepareStatement(SELECT_FACULTIES);
        ResultSet facultiesResult = selectFaculties.executeQuery();

        while (facultiesResult.next()) {
            int facultyID = facultiesResult.getInt("FacultyID");
            String facultyName = facultiesResult.getString("FacultyName");
            boolean studyField = facultiesResult.getBoolean("StudyField");
            int parentID = facultiesResult.getInt("ParentID");

            FacultyData parent = null;
            if (!facultiesResult.wasNull()) {
                parent = facultyMap.get(parentID);
            }

            FacultyData faculty = new FacultyData(facultyID, studyField, facultyName, parent);
            facultyMap.put(facultyID, faculty);
        }

        return facultyMap;
    }
}
