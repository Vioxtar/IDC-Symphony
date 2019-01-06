package idc.symphony.ui;

import java.awt.*;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Properties file to keep last valid application state for convenience;
 * Limit repeated tasks.
 */
public class SymphonizerProperties {
    private static String INPUT_CLASS = "INPUT";
    private static String DB_FILE = INPUT_CLASS + "." + "SQL_DB_FILE";
    private static String MIDI_FILE = INPUT_CLASS + "." + "SQL_DB_FILE";
    private static String USE_EXTERNAL_MIDI = INPUT_CLASS + "." + "USE_EXTERNAL_MIDI";

    private static String OUTPUT_CLASS = "OUTPUT";
    private static String MIDI_OUTPUT = OUTPUT_CLASS + "." + "MIDI_OUTPUT";
    private static String MP4_OUTPUT = OUTPUT_CLASS + "." + "MP4_OUTPUT";

    private static String DISPLAY_CLASS = "DISPLAY";
    private static String WIN_POS_LEFT = DISPLAY_CLASS + "." + "POS_LEFT";
    private static String WIN_POS_TOP = DISPLAY_CLASS + "." + "POS_TOP";
    private static String WIN_WIDTH = DISPLAY_CLASS + "." + "WIDTH";
    private static String WIN_HEIGHT = DISPLAY_CLASS + "." + "HEIGHT";
    private static String MAXIMIZED = DISPLAY_CLASS + "." + "MAXIMIZED";

    private static Properties DEFAULT = new Properties();

    static {
        // INPUT
        DEFAULT.setProperty(DB_FILE, "data\\IDCEvents.db");
        DEFAULT.setProperty(MIDI_FILE, "data\\external.midi");
        DEFAULT.setProperty(USE_EXTERNAL_MIDI, "0");

        // OUTPUT
        DEFAULT.setProperty(MIDI_OUTPUT, "output.midi");
        DEFAULT.setProperty(MP4_OUTPUT, "outout.mp4");

        // DISPLAY
        Dimension screenSize;
        try {
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        } catch (Exception ex) {
            screenSize = new Dimension(1280, 720);
        }

        int defaultWinWidth = Math.max(540, screenSize.width / 2);
        int defaultWinHeight = Math.max(370, (int)(screenSize.height * 0.8) - 80);
        int defaultPosX = (screenSize.width - defaultWinWidth) / 2;
        int defaultPosY = (screenSize.height - defaultWinHeight) / 2;

        DEFAULT.setProperty(WIN_POS_LEFT, String.valueOf(defaultPosX));
        DEFAULT.setProperty(WIN_POS_TOP, String.valueOf(defaultPosY));
        DEFAULT.setProperty(WIN_HEIGHT, String.valueOf(defaultWinWidth));
        DEFAULT.setProperty(WIN_WIDTH, String.valueOf(defaultWinHeight));
        DEFAULT.setProperty(MAXIMIZED, String.valueOf(false));
    }

    private Properties properties;
    private Logger logger;
    private File file;

    public SymphonizerProperties() {
        this(null, null);
    }

    public SymphonizerProperties(File file) {
        this(file, null);
    }

    public SymphonizerProperties(Logger logger) {
        this(null, logger);
    }

    public SymphonizerProperties(File file, Logger logger) {
        this.properties = new Properties();
        this.properties.putAll(DEFAULT);
        this.logger = logger;
        this.file = file;

        FileInputStream input = null;
        if (file != null) {
            try {
                if (file.exists()) {
                    input = new FileInputStream(file);
                    properties.load(input);
                }
            } catch (IOException ex) {
                if (logger != null) {
                    logger.warning("Failed to create config file, state will not be saved.");
                }

                Logger.getGlobal().log(Level.WARNING, ex.getMessage(), ex);
            } finally {
                safeClose(input);

                // Ensures properties always have default variables for user clarity
                save();
            }
        }

    }

    private void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
                Logger.getGlobal().log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    private void logInvalidType(String key, Class type) {
        if (logger != null) {
            logger.warning(
                    String.format("Invalid config value: %s\n\tExpected %s ",
                            key, type.getTypeName()));
        }
    }

    private int getInt(String key) {
        try {
            return Integer.valueOf(properties.getProperty(key));
        } catch (NumberFormatException ex) {
            logInvalidType(key, Integer.class);
            return Integer.valueOf(DEFAULT.getProperty(key));
        }
    }

    private void setInt(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }

    private boolean getBoolean(String key) {
        try {
            return Boolean.valueOf(properties.getProperty(key));
        } catch (NumberFormatException ex) {
            logInvalidType(key, Boolean.class);
            return Boolean.valueOf(DEFAULT.getProperty(key));
        }
    }

    private void setBoolean(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public int getWindowWidth() {
        return getInt(properties.getProperty(WIN_WIDTH));
    }

    public void setWindowWidth(int width) {
        setInt(WIN_WIDTH, width);
    }

    public int getWindowHeight() {
        return getInt(properties.getProperty(WIN_HEIGHT));
    }

    public void setWindowHeight(int height) {
        setInt(WIN_HEIGHT, height);
    }

    public int getWindowLeft() {
        return getInt(properties.getProperty(WIN_POS_LEFT));
    }

    public void setWindowLeft(int left) {
        setInt(WIN_POS_LEFT, left);
    }

    public int getWindowTop() {
        return getInt(properties.getProperty(WIN_POS_TOP));
    }

    public void setWindowTop(int top) {
        setInt(WIN_POS_TOP, top);
    }
    
    public String getDBPath() {
        return properties.getProperty(DB_FILE);
    }
    
    public void setDBPath(String path) {
        properties.setProperty(DB_FILE, path);
    }

    public String getMIDIPath() {
        return properties.getProperty(MIDI_FILE);
    }

    public void setMIDIPath(String path) {
        properties.setProperty(MIDI_FILE, path);
    }

    public boolean getUseExternalMIDI() {
        return getBoolean(properties.getProperty(USE_EXTERNAL_MIDI));
    }

    public void setUseExternalMIDI(boolean selected) {
        setBoolean(USE_EXTERNAL_MIDI, selected);
    }

    public boolean getMaximized() {
        return getBoolean(properties.getProperty(MAXIMIZED));
    }

    public void setMaximized(boolean maximized) {
        setBoolean(USE_EXTERNAL_MIDI, maximized);
    }

    public void save() {
        if (file != null) {
            try (FileOutputStream output = new FileOutputStream(file)) {
                properties.store(output, "IDC Symphonizer client preferences");
            } catch (IOException ex) {
                if (logger != null) {
                    logger.warning("Could not save Symphonizer application state");
                }
            }
        }
    }
}
