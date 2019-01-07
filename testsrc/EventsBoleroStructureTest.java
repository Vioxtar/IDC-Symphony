import idc.symphony.db.YearDataFactory;
import idc.symphony.music.conducting.EventsBoleroStructure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class EventsBoleroStructureTest {
    private static final boolean PRINT_DEBUG = true;

    private static Connection dbConnection;
    private static EventsBoleroStructure structure;

    @BeforeAll
    private static void createFakeTable() throws SQLException {
        SQLiteConfig sqlConf = new SQLiteConfig();
        sqlConf.setReadOnly(true);

        dbConnection = sqlConf.createConnection("jdbc:sqlite:data/IDC Events.db");
        structure = new EventsBoleroStructure(YearDataFactory.fromDB(dbConnection, null));

        if(PRINT_DEBUG) {
            printStructure();
        }
    }

    private static void printStructure() {
        StringBuilder buddy = new StringBuilder();
        buddy.append("Bolero YearStructure:\n");

        int totalSeqs = 0;
        int emptyYears = 0;
        boolean firstNonEmpty = false;

        for (int year = 1994; year <= 2018; year++) {
            buddy.append(year)
                .append(", [");

            int[] yearSeqs = structure.eventsPerSequence(year);
            firstNonEmpty = firstNonEmpty || (yearSeqs.length > 0);
            if (firstNonEmpty && yearSeqs.length == 0) {
                emptyYears++;
            }
            totalSeqs += yearSeqs.length;
            for (int i = 0; i < yearSeqs.length; i++) {
                buddy.append(yearSeqs[i]);


                if (i + 1 < yearSeqs.length) {
                    buddy.append(", ");
                }
            }

            buddy.append("]\n");
        }

        buddy.append("Total: ").append(totalSeqs).append("\n");
        buddy.append("Empty Years: ").append(emptyYears).append("\n");
        buddy.append("Estimated Song Length: ").append(totalSeqs * 10 + emptyYears * 2);
        System.out.println(buddy.toString());
    }

    @Test
    void interesting_year_should_be_more_than_one_seq() {
        assertTrue(structure.eventsPerSequence(2018).length > 1);
    }

    @Test
    void short_year_should_still_have_one_seq() {
        assertEquals(structure.eventsPerSequence(1995).length, 1);
    }

    @Test
    void year_with_no_events_should_be_skipped() {
        assertEquals(structure.eventsPerSequence(1997).length, 0);
    }
}