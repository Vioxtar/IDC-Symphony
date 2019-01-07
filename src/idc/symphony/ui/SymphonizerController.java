package idc.symphony.ui;

import idc.symphony.ui.logging.LogHandler;
import idc.symphony.ui.property.CachedResponse;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Top-Level UI Behaviour Controller
 */
public class SymphonizerController {
    private File appConfFile = new File("idcsymphonizer.properties");
    private SymphonizerProperties appConfig;
    private Logger userLog = Logger.getLogger("Symphony");

    SymphonizerWindow application;

    @FXML Button btnVisualize;
    @FXML Button btnSaveMIDI;
    @FXML Button btnSaveMP4;
    @FXML Button btnDBFile;
    @FXML Button btnMIDIFile;
    @FXML CheckBox useExistingMIDI;
    @FXML ListView<Label> listUserLog;
    @FXML TextField txtDBPath;
    @FXML TextField txtMIDIPath;
    @FXML TitledPane logPane;

    private BooleanProperty isVisualizing =
            new SimpleBooleanProperty(this, "Visualization in Progress",false);
    private ObjectProperty<File> midiFile =
            new SimpleObjectProperty<>(this, "MIDI File", null);
    private ObjectProperty<File> dbFile =
            new SimpleObjectProperty<>(this, "SQLite Database",null);

    private static final FileChooser.ExtensionFilter DB_FILTER =
            new FileChooser.ExtensionFilter("SQLite Database", "*.db");
    private static final FileChooser.ExtensionFilter MIDI_FILTER =
            new FileChooser.ExtensionFilter("MIDI File", "*.midi");

    // Lower level controllers - unaware of existence of UI
    private ConductingController conductingController = new ConductingController();

    /**
     * Loads persistent state from file.
     * To be called from injector in order to ensure controller can change window state.
     */
    void loadState() {
        dbFile.set(new File(appConfig.getDBPath()));
        midiFile.set(new File(appConfig.getMIDIPath()));

        useExistingMIDI.setSelected(appConfig.getUseExternalMIDI());
        useExistingMIDI.selectedProperty().addListener((selected) ->
            appConfig.setUseExternalMIDI(useExistingMIDI.isSelected())
        );

        application.show();
    }

    /**
     * Controller initialization - UI control bindings, dependency injection.
     */
    @FXML
    void initialize() {
        userLog.addHandler(new LogHandler(listUserLog, 1000));
        userLog.setLevel(Level.FINER);
        conductingController.attachLogger(userLog);

        appConfig = new SymphonizerProperties(appConfFile, userLog);

        isVisualizing = new SimpleBooleanProperty(false);

        // externalMIDI state is invalid if user chose to use existing MIDI but has no valid file.
        BooleanProperty externalMIDIInvalid = new SimpleBooleanProperty();
        externalMIDIInvalid.bind(
                Bindings.createBooleanBinding(() ->
                    useExistingMIDI.isSelected() && txtMIDIPath.getText().isEmpty(),
                useExistingMIDI.selectedProperty(),
                txtMIDIPath.textProperty()));

        // Button setup
        btnDBFile.disableProperty().bind(isVisualizing.or(conductingController.conductingProperty()));
        btnSaveMIDI.disableProperty().bind(isVisualizing.or(txtDBPath.textProperty().isEmpty()));
        btnSaveMP4.disableProperty().bind(
                isVisualizing
                        .or(txtDBPath.textProperty().isEmpty())
                        .or(externalMIDIInvalid)
                        .or(conductingController.conductingProperty()));

        btnVisualize.disableProperty().bind(
                txtDBPath.textProperty().isEmpty()
                        .or(externalMIDIInvalid)
                        .or(conductingController.conductingProperty()));

        // Text Field Setup
        dbFile.addListener((ignore) -> validateFile(dbFile, ".db"));
        txtDBPath.textProperty().bind(
                Bindings.createStringBinding(() -> getFilePath(dbFile) , dbFile));

        midiFile.addListener((ignore) -> validateFile(midiFile, ".midi"));
        txtMIDIPath.textProperty().bind(
                Bindings.createStringBinding(() -> getFilePath(midiFile), midiFile));

        conductingController.patternCache().addInvalidator(
                useExistingMIDI.selectedProperty(), dbFile, midiFile
        );

    }

    /**
     * Invalidation handler - invalidates file value changes if file does not exist or is unreadable.
     *
     * @param file      File to validate
     * @param extension Extension file is expected to have
     */
    private void validateFile(ObjectProperty<File> file, String extension) {
        File val = file.get();
        if (val != null) {
            if (!val.exists() || !val.canRead()) {
                userLog.warning(String.format("Couldn't find or read %s: %s", file.getName(), val.getPath()));
                file.setValue(null);
            } else if (!val.getName().endsWith(extension)) {
                userLog.warning(String.format("Invalid format for %s: %s (expected %s)",
                        file.getName(), val.getName(), extension));
            }
        }
    }

    /**
     * For binding, binds the path of a file to string properties.
     *
     * @param file Observable file value
     * @return     File path
     */
    private String getFilePath(ObservableValue<File> file) {
        File value = file.getValue();
        return (value != null) ? value.getPath() : "";
    }

    /**
     * Choose event database file
     */
    @FXML
    void chooseDBFile() {
        FileChooser chooser = initChooser(DB_FILTER, new File(appConfig.getDBPath()));
        chooser.getExtensionFilters().clear();
        chooser.getExtensionFilters().add(DB_FILTER);
        chooser.setSelectedExtensionFilter(DB_FILTER);
        chooser.setInitialDirectory(new File(appConfig.getDBPath()));
        File file = chooser.showOpenDialog(btnDBFile.getScene().getWindow());

        if (file != null) {
            dbFile.set(file);
            appConfig.setDBPath(file.getPath());
        }
    }

    /**
     * Choose External MIDI to use instead of generated musics
     */
    @FXML
    void chooseMIDIFile() {
        FileChooser chooser = initChooser(MIDI_FILTER, new File(appConfig.getMIDIPath()));
        File file = chooser.showOpenDialog(btnMIDIFile.getScene().getWindow());

        if (file != null) {
            midiFile.set(file);
            appConfig.setMIDIPath(file.getPath());
        }
    }

    /**
     * Saves generated pattern to MIDI file
     * May generate pattern if required (uses cache)
     */
    @FXML void saveMIDI() {
        FileChooser chooser = initChooser(MIDI_FILTER, new File(appConfig.getMIDIOutputPath()));
        File file = chooser.showSaveDialog(btnSaveMIDI.getScene().getWindow());

        if (file != null) {
            appConfig.setMIDIOutputPath(file.getPath());
            conductingController.patternCache().getAsync(
                    (callback) -> conductingController.getFullMIDIAsync(dbFile.get(), callback),
                    (pattern) -> this.saveMIDIPattern(file, pattern)
            );
        }
    }

    /**
     * Saves given pattern to MIDI file
     * @param targetFile Target file
     * @param pattern Source pattern
     */
    private void saveMIDIPattern(File targetFile, Pattern pattern) {
        if (pattern == null) {
            userLog.severe("Failed to generate MIDI from DB.");
            return;
        }

        conductingController.saveMIDI(
                targetFile,
                pattern,
                (Success) -> {
                    if (Success != null) {
                        userLog.info(String.format("Exported generated MIDI to %s", targetFile.getPath()));
                    } else {
                        userLog.severe(String.format("Failed to save generated MIDI to %s", targetFile.getPath()));
                    }
                });
    }

    /**
     * Creates a file choosing dialogue
     *
     * @param filter      Limiting extension filter
     * @param defaultPath Default path to start dialogue window in
     * @return file chooser dialogue window instance
     */
    private FileChooser initChooser(FileChooser.ExtensionFilter filter, File defaultPath) {
        FileChooser chooser = new FileChooser();

        if (filter != null) {
            chooser.getExtensionFilters().clear();
            chooser.getExtensionFilters().add(filter);
            chooser.setSelectedExtensionFilter(filter);
        }

        if (defaultPath != null && defaultPath.exists()) {
            if(defaultPath.isDirectory()) {
                chooser.setInitialDirectory(defaultPath);
            } else {
                chooser.setInitialDirectory(defaultPath.getParentFile());
                chooser.setInitialFileName(defaultPath.getName());
            }
        }

        return chooser;
    }

    /**
     * Saves persistent state on application closed
     */
    void shutdown() {
        appConfig.save();
    }
}
