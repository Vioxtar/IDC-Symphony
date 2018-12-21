package idc.symphony;

import idc.symphony.music.DBConductor;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class IDCSymphony {
    public static void main(String [] args) throws SQLException {
        conductAndPlay(new File("data/IDC Events.db"));
    }

    private static void conductAndPlay(File file) throws SQLException {
        DBConductor conductor = new DBConductor(prepareConnection(file));
        conductor.conduct();
    }

    private static Connection prepareConnection(File file) throws SQLException {
        SQLiteConfig conf = new SQLiteConfig();
        conf.setReadOnly(true);

        return conf.createConnection("jdbc:sqlite:" + file.getPath());
    }
}