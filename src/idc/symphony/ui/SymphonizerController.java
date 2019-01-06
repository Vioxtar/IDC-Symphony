package idc.symphony.ui;

import idc.symphony.ui.logging.LogHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UI Behaviour Controller
 */
public class SymphonizerController {
    File appConfFile = new File("idcsymphonizer.properties");
    SymphonizerProperties appConfig;
    Logger userLog = Logger.getLogger("Symphony");

    @FXML SymphonizerWindow application;

    @FXML Button btnVisualize;
    @FXML Button btnSaveMIDI;
    @FXML Button btnSaveMP4;
    @FXML Button btnDBFile;
    @FXML Button btnMIDIFile;

    @FXML TextField txtDBPath;
    SimpleObjectProperty<File> dbFile =
            new SimpleObjectProperty<>(this, "SQLite Database",null);

    @FXML TextField txtMIDIPath;
    SimpleObjectProperty<File> midiFile =
            new SimpleObjectProperty<>(this, "MIDI File", null);;

    @FXML TextArea SymphonizerLog;

    @FXML TitledPane logPane;
    @FXML ListView<Label> listView;

    @FXML CheckBox useExistingMIDI;

    SimpleBooleanProperty isVisualizing = new SimpleBooleanProperty(false);
    SimpleBooleanProperty externalMIDIInvalid = new SimpleBooleanProperty(false);

    private static final FileChooser.ExtensionFilter DB_FILTER =
            new FileChooser.ExtensionFilter("SQLite Database", "*.db");
    private static final FileChooser.ExtensionFilter MIDI_FILTER =
            new FileChooser.ExtensionFilter("MIDI File", "*.midi");

    public SymphonizerController() {

    }

    void loadState() {
        dbFile.set(new File(appConfig.getDBPath()));
        midiFile.set(new File(appConfig.getMIDIPath()));

        application.show();
    }

    @FXML
    void initialize() {
        userLog.addHandler(new LogHandler(listView, 100));
        userLog.setLevel(Level.FINE);

        appConfig = new SymphonizerProperties(appConfFile, userLog);

        isVisualizing = new SimpleBooleanProperty(false);

        // externalMIDI state is invalid if user chose to use existing MIDI but has no valid file.
        externalMIDIInvalid = new SimpleBooleanProperty();
        externalMIDIInvalid.bind(
                Bindings.createBooleanBinding(() -> useExistingMIDI.isSelected() && txtMIDIPath.getText().isEmpty(),
                useExistingMIDI.selectedProperty(),
                txtMIDIPath.textProperty()));

        btnDBFile.disableProperty().bind(isVisualizing);
        btnSaveMIDI.disableProperty().bind(isVisualizing.or(txtDBPath.textProperty().isEmpty()));
        btnSaveMP4.disableProperty().bind(isVisualizing.or(txtDBPath.textProperty().isEmpty()));
        btnVisualize.disableProperty().bind(txtDBPath.textProperty().isEmpty());

        dbFile.addListener((ignore) -> validateFile(dbFile));
        midiFile.addListener((ignore) -> validateFile(midiFile));
    }

    private void validateFile(SimpleObjectProperty<File> file) {
        File val = file.get();
        if (val != null && (!val.exists() || !val.canRead())) {
            userLog.warning(String.format("Couldn't find %s: %s", file.getName(), val.getPath()));
            file.setValue(null);
        } else {

        }
    }

    @FXML
    void chooseSQLiteFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().clear();
        chooser.getExtensionFilters().add(DB_FILTER);
        chooser.setSelectedExtensionFilter(DB_FILTER);
        File file = chooser.showOpenDialog(btnDBFile.getScene().getWindow());

        if (file != null) {
            txtDBPath.setText(file.getPath());
            // TODO: save DB File location
        }
    }

    @FXML
    void chooseMIDIFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().clear();
        chooser.setSelectedExtensionFilter(MIDI_FILTER);
        File file = chooser.showOpenDialog(btnMIDIFile.getScene().getWindow());

        if (file != null) {
            txtMIDIPath.setText(file.getPath());
            // TODO: save DB File location
        }
    }

    public void shutdown() {

    }
}
