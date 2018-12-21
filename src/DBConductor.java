import Stats.YearStats;
import db.events.YearStatsFactory;
import org.jfugue.pattern.Pattern;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConductor {
    public static final int DEFAULT_TEMPO = 120;
    public static final float DEFAULT_SECONDS_PER_SEQUENCE = 10;
    public static final float DEFAULT_SECONDS_PER_EMPTY_YEAR = 2;

    private Connection dbConnection;
    private int tempo = DEFAULT_TEMPO;
    private float secsPerSequence = DEFAULT_SECONDS_PER_SEQUENCE;
    private float secsPerEmptyYear = DEFAULT_SECONDS_PER_EMPTY_YEAR;

    public DBConductor(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Pattern conduct() throws SQLException {
        YearStats stats = YearStatsFactory.fromDB(dbConnection);
        EventsBoleroStructure structure = prepareBoleroStructure(stats);





        return null;
    }

    private EventsBoleroStructure prepareBoleroStructure(YearStats stats) throws SQLException {
        return new EventsBoleroStructure(stats);
    }
}
