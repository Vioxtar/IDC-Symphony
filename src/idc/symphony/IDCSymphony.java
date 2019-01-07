package idc.symphony;

import idc.symphony.music.DBConductor;
import org.jfugue.player.Player;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class IDCSymphony {
    public static void main(String [] args) throws SQLException {
//        conductAndPlay(new File("data/IDC Events.db"));
        Player player = new Player();

        player.play("T144 D6w Ri C6i D6i E6i D6i C6i Bi D6w");
    }

    private static void conductAndPlay(File file) throws SQLException {
        DBConductor conductor = new DBConductor();
        conductor.conduct(prepareConnection(file));
    }

    private static Connection prepareConnection(File file) throws SQLException {
        SQLiteConfig conf = new SQLiteConfig();
        conf.setReadOnly(true);

        return conf.createConnection("jdbc:sqlite:" + file.getPath());
    }
}