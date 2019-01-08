package idc.symphony.db;

import javafx.concurrent.Task;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * Abstract SQLite database connection holding controller, with support for asynchronous requests
 */
public abstract class DBController {
    private static File dbFileCache = null;
    private static Connection dbConnection = null;
    private static SQLiteConfig SQL_CONF = new SQLiteConfig();
    static {
        SQL_CONF.setReadOnly(true);
    }

    /**
     * Get connection from file.
     * If file is the same as in cache and connection is still valid, retrieves connection from cache
     * @param file
     * @return
     */
    protected Connection getConnection(File file) throws SQLException {
        if (dbFileCache != null && dbFileCache.equals(file) && !dbConnection.isClosed()) {
            return dbConnection;
        }

        dbFileCache = file;
        dbConnection = SQL_CONF.createConnection(fileToSQLURI(file));

        return dbConnection;
    }

    /**
     * JDBC Local SQLite URI translator
     * @param file Local SQLite file
     * @return JDBC URI
     */
    private String fileToSQLURI(File file) {
        return String.format("jdbc:sqlite:%s", file.getPath());
    }


    /**
     * Generic task launcher
     * Creates a daemon background thread to execute given task.
     *
     * @param task     Task to launch
     * @param callback Task return value receiving callback
     * @param <T>      Task return type
     */
    protected <T> void launchTask(Task<T> task, Consumer<T> callback) {
        task.setOnSucceeded((state) -> callback.accept(task.getValue()));
        task.setOnFailed((state) -> callback.accept(null));
        task.setOnCancelled((state) -> callback.accept(null));

        Thread thread = new Thread(task);
        thread.setName(task.getTitle());
        thread.setDaemon(true);
        thread.start();
    }
}
