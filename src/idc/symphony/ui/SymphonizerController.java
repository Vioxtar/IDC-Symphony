package idc.symphony.ui;

import idc.symphony.music.conducting.ConductorController;
import idc.symphony.ui.cache.AsyncRequest;
import idc.symphony.ui.logging.LogHandler;
import idc.symphony.visual.Visualizer;
import idc.symphony.visual.VisualizerController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.jfugue.pattern.Pattern;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Top-Level UI Behaviour Controller
 * Acts as a facade for all lower level controllers, such that lower level controllers don't need to know that
 * UI even exists.
 */
public class SymphonizerController {
    /**
     * Project logger
     */
    private Logger logger = Logger.getLogger("idc.symphony");

    /**
     * Persistent configuration file
     */
    private File appConfFile = new File("idcsymphonizer.properties");

    /**
     * Persistent properties
     */
    private SymphonizerProperties appConfig;

    /**
     * Separate log for more user-friendly messages, hiding technical error messages from user
     */
    private Logger userLog = Logger.getLogger("idc.symphony@user");
    {
        userLog.setParent(logger);
    }

    /**
     * Window controlled elements are placed in. Injected.
     */
    SymphonizerWindow application;

    /**
     * Play Visualization button
     */
    Tooltip btnVisualizeTooltip;
    @FXML Button btnPlayVisualize;
    @FXML Button btnStopVisualize;

    /**
     * Save generated MIDI file button
     */
    Tooltip btnSaveMIDITooltip;
    @FXML Button btnSaveMIDI;

    /**
     * Render and save visualization MP4
     */
    Tooltip btnSaveMP4Tooltip;
    @FXML Button btnSaveMP4;

    /**
     *  Choose DB File location
     */
    @FXML Button btnDBFile;

    /**
     * Choose existing MIDI file location
     */
    @FXML Button btnMIDIFile;

    /**
     * Use existing MIDI for generation?
     */
    Tooltip midiInvalidTooltip = new Tooltip("Cannot find existing MIDI");
    @FXML CheckBox useExistingMIDI;

    /**
     *
     */
    @FXML ListView<Label> listUserLog;
    @FXML TextField txtDBPath;
    @FXML TextField txtMIDIPath;
    @FXML TitledPane logPane;
    @FXML TitledPane visualizerPane;
    @FXML Visualizer visualizer;

    private BooleanProperty externalMIDIInvalid = new SimpleBooleanProperty();
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
    private ConductorController conductorController = new ConductorController();
    private VisualizerController visualizerController = new VisualizerController();

    /**
     * Loads persistent state from file.
     * To be called from injector in order to ensure controller can change window state.
     */
    void loadState() {
        File db = new File(appConfig.getDBPath());
        File midi = new File(appConfig.getMIDIPath());
        if (db.exists()) {
            dbFile.set(db);
        }
        if (midi.exists()) {
            midiFile.set(midi);
        }

        useExistingMIDI.setSelected(appConfig.getUseExternalMIDI());
        useExistingMIDI.selectedProperty().addListener((selected) ->
            appConfig.setUseExternalMIDI(useExistingMIDI.isSelected())
        );



        // Get window state
        application.resize(
                appConfig.getWindowWidth(),
                appConfig.getWindowHeight()
        );

        application.reposition(
                appConfig.getWindowLeft(),
                appConfig.getWindowTop()
        );

        application.maximized(
                appConfig.getMaximized()
        );

        // Link window state to config for persistence
        application.windowStage.widthProperty().addListener(
                (ignore) -> appConfig.setWindowWidth((int)application.windowStage.getWidth()));
        application.windowStage.heightProperty().addListener(
                (ignore) -> appConfig.setWindowHeight((int)application.windowStage.getHeight()));

        application.windowStage.xProperty().addListener(
                (ignore) -> appConfig.setWindowLeft((int)application.windowStage.getX()));
        application.windowStage.yProperty().addListener(
                (ignore) -> appConfig.setWindowTop((int)application.windowStage.getY()));

        application.windowStage.maximizedProperty().addListener((
                (ignore)->  appConfig.setMaximized(application.windowStage.isMaximized())));

        application.show();
    }

    /**
     * Controller initialization - UI control bindings, dependency injection.
     */
    @FXML
    void initialize() {
        userLog.addHandler(new LogHandler(listUserLog, 1000));
        userLog.setLevel(Level.FINER);
        conductorController.attachLogger(userLog);

        appConfig = new SymphonizerProperties(appConfFile, userLog);

        isVisualizing = new SimpleBooleanProperty(false);

        // externalMIDI state is invalid if user chose to use existing MIDI but has no valid file.
        externalMIDIInvalid.bind(
                Bindings.createBooleanBinding(() ->
                    useExistingMIDI.isSelected() && txtMIDIPath.getText().isEmpty(),
                useExistingMIDI.selectedProperty(),
                txtMIDIPath.textProperty()));

        // Provide information about why buttons are disabled when MIDI is invalid
        externalMIDIInvalid.addListener(
                (ignore) -> {
                    if (externalMIDIInvalid.get()) {
                        txtMIDIPath.setStyle("-fx-effect: dropshadow( three-pass-box, red , 3.0, 0.0,0.0,0.0)");
                    } else {
                        txtMIDIPath.setStyle("-fx-effect: none");
                    }
                });

        // Button setup
        useExistingMIDI.disableProperty().bind(isVisualizing.or(conductorController.conductingProperty()));
        btnDBFile.disableProperty().bind(isVisualizing.or(conductorController.conductingProperty()));
        btnMIDIFile.disableProperty().bind(isVisualizing.or(conductorController.conductingProperty()));

        btnSaveMIDI.disableProperty().bind(
                isVisualizing
                        .or(txtDBPath.textProperty().isEmpty())
                        .or(externalMIDIInvalid)
                        .or(conductorController.conductingProperty()));

        // TODO: Implement saving to MP4
        btnSaveMP4.setDisable(true);
        /*btnSaveMP4.disableProperty().bind(
                isVisualizing
                        .or(txtDBPath.textProperty().isEmpty())
                        .or(externalMIDIInvalid)
                        .or(conductorController.conductingProperty()));*/

        btnPlayVisualize.disableProperty().bind(
                txtDBPath.textProperty().isEmpty()
                        .or(txtDBPath.textProperty().isEmpty())
                        .or(externalMIDIInvalid)
                        .or(conductorController.conductingProperty()));
        btnPlayVisualize.mouseTransparentProperty().bind(isVisualizing);
        btnPlayVisualize.visibleProperty().bind(isVisualizing.not());
        btnStopVisualize.visibleProperty().bind(isVisualizing);
        btnStopVisualize.mouseTransparentProperty().bind(isVisualizing.not());

        // Text Field Setup
        dbFile.addListener((ignore) -> validateFile(dbFile, ".db"));
        txtDBPath.textProperty().bind(
                Bindings.createStringBinding(() -> getFilePath(dbFile) , dbFile));

        midiFile.addListener((ignore) -> validateFile(midiFile, ".midi"));
        txtMIDIPath.textProperty().bind(
                Bindings.createStringBinding(() -> getFilePath(midiFile), midiFile));

        conductorController.patternCache().addInvalidator(
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
        if (val != null && !val.getPath().isEmpty()) {
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
        FileChooser chooser = initChooser(MIDI_FILTER, new File(appConfig.getMIDIPath()));
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
            conductorController.patternCache().getAsync(
                    getPatternRequest(),
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

        conductorController.saveMIDI(
                targetFile,
                pattern,
                (success) -> {
                    if (success != null) {
                        userLog.info(String.format("Exported generated MIDI to %s", targetFile.getPath()));
                    } else {
                        userLog.severe(String.format("Failed to save generated MIDI to %s", targetFile.getPath()));
                    }
                });
    }

    @FXML void playVisualization() {
        if (!txtDBPath.getText().isEmpty()) {
            conductorController.patternCache().getAsync(
                    getPatternRequest(),
                    this::convertAndPlay
            );
        }
    }

    /**
     * Converts a song into visual events, and plays events in visualizer.
     * @param pattern Song pattern generated from MIDI and DB
     */
    private void convertAndPlay(Pattern pattern) {
        if (pattern == null) {
            userLog.severe("Failed to generate MIDI from DB.");
            return;
        }

        visualizerController.buildAsync(
                dbFile.get(),
                pattern,
                (events) -> {
                    if (events != null) {
                        visualizerController.playPattern(pattern);
                        visualizer.start(events);
                        isVisualizing.set(true);
                    } else {
                        userLog.severe(String.format("Failed to generate visualization data from MIDI"));
                    }
                }
        );
    }

    @FXML void stopVisualization() {
        visualizer.stop();
        visualizerController.stopPlaying();
        isVisualizing.set(false);
    }

    /**
     * Chooses the appropriate pattern generator request according to user choice
     * @return Asynchronous request for pattern generation
     */
    private AsyncRequest<Pattern> getPatternRequest() {
        return (useExistingMIDI.isSelected() && externalMIDIInvalid.get())
                ? (callback) -> conductorController.getInfoMIDIAsync(dbFile.get(), midiFile.get(), callback)
                : (callback) -> conductorController.getFullMIDIAsync(dbFile.get(), callback);
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
        } else {
            // Use running directory if preferred file not found
            chooser.setInitialDirectory(new File("."));
        }

        return chooser;
    }

    /**
     * Saves persistent state on application closed
     */
    void shutdown() {
        visualizerController.stopPlaying();
        appConfig.save();
    }
}
