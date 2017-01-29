package org.petermfc.presenter;

import com.google.common.eventbus.EventBus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.CheckComboBox;
import org.petermfc.Globals.JobState;
import org.petermfc.Globals.JobType;
import org.petermfc.controls.MessageBox;
import org.petermfc.event.JobStateChangedEvent;
import org.petermfc.exception.CredentialsException;
import org.petermfc.exception.DownloadException;
import org.petermfc.exception.SubtitleException;
import org.petermfc.model.Subtitle;
import org.petermfc.service.FileDownloader;
import org.petermfc.service.SubtitlesService;
import org.petermfc.util.LanguageUtils;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FrontPresenter implements Initializable {
    @Inject
    private EventBus eventBus;

    @FXML
    public CheckComboBox<Pair<String, String>> cbLanguages;
    @FXML
    public Button btnSearch;
    @FXML
    public Button btnInterrupt;
    @FXML
    private TextField txtKeywords;
    @FXML
    private TableView<Subtitle> tblResults;
    @FXML
    private TableColumn<Subtitle, String> colName;
    @FXML
    private TableColumn<Subtitle, String> colLanguage;
    @FXML
    private TableColumn<Subtitle, String> colFormat;
    @FXML
    private TableColumn<Subtitle, String> colLink;
    @Inject
    private SubtitlesService subtitlesService;
    @Inject
    private FileDownloader fileDownloader;

    private Thread searchThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cbLanguages.getItems().addAll(LanguageUtils.prepareLanguages());
        cbLanguages.setConverter(new StringConverter<Pair<String, String>>() {
            @Override
            public String toString(Pair<String, String> object) {
                return object.getValue();
            }
            @Override
            public Pair<String, String> fromString(String string) {
                return null;
            }
        });

        final String systemLang = Locale.getDefault().getISO3Language();
        List<Pair<String, String>> defaultCheck = cbLanguages.getItems()
                .stream()
                .filter(s -> s.getKey().equals("eng") || s.getKey().equals(systemLang))
                .collect(Collectors.toList());

        cbLanguages.getCheckModel().check(defaultCheck.get(0));
        cbLanguages.getCheckModel().check(defaultCheck.get(1));

        colName.setCellValueFactory(cellValue -> cellValue.getValue().titleProperty());
        colLanguage.setCellValueFactory(cellValue -> cellValue.getValue().languageProperty());
        colFormat.setCellValueFactory(cellValue -> cellValue.getValue().formatProperty());
        colLink.setCellValueFactory(cellValue -> cellValue.getValue().downloadLinkProperty());

        colLink.setCellFactory(col -> {
            TableCell<Subtitle, String> cell = new TableCell<Subtitle, String> () {
               @Override
                protected void updateItem(String item, boolean empty) {
                   super.updateItem(item, empty);
                   setText(empty ? null : item);
               }
            };
            cell.setOnMouseClicked(e -> {
                if(!cell.isEmpty()) {
                    saveFile((Subtitle)cell.getTableRow().getItem());
                }
            });
            cell.setOnMouseEntered(e -> {
                cell.getScene().setCursor(Cursor.HAND);
                cell.setStyle("-fx-underline: true; -fx-stroke: blue;");
            });
            cell.setOnMouseExited(e -> {
                cell.getScene().setCursor(Cursor.DEFAULT);
                cell.setStyle("-fx-underline: false; -fx-stroke: default;");
            });
            return cell;
        });



        fileDownloader.transferredBytesProperty().addListener((observable, oldValue, newValue) ->
                eventBus.post(new JobStateChangedEvent(JobType.DOWNLOADING, JobState.PROGRESS_UPDATE, (double)(fileDownloader.getTransferredBytes() / fileDownloader.getFileSize())))
        );

        fileDownloader.downloadFinishedProperty().addListener((o, oldVal, newVal) ->
                {
                    if (newVal) {
                        eventBus.post(new JobStateChangedEvent(JobType.DOWNLOADING, JobState.FINISHED, fileDownloader.getOutputFilePath()));
                    } else {
                        eventBus.post(new JobStateChangedEvent(JobType.DOWNLOADING, JobState.STARTED, ""));
                    }
                }
        );
        subtitlesService.searchRunningProperty().addListener((o, oldVal, newVal) ->
                 {
                            if (newVal) {
                                eventBus.post(new JobStateChangedEvent(JobType.SEARCHING, JobState.STARTED, ""));
                                btnInterrupt.setVisible(true);
                                btnSearch.setVisible(false);
                            } else {
                                Collection<Subtitle> subtitlesCollection = subtitlesService.getResultsList();
                                eventBus.post(new JobStateChangedEvent(JobType.SEARCHING, JobState.FINISHED, String.valueOf(subtitlesCollection.size())));
                                btnInterrupt.setVisible(false);
                                btnSearch.setVisible(true);
                                tblResults.getItems().clear();
                                tblResults.getItems().addAll(subtitlesCollection);
                                cbLanguages.setPrefWidth(txtKeywords.getPrefWidth());
                            }
                        }
        );

        txtKeywords.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if(db.hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY);
            } else {
                e.consume();
            }
        });

        txtKeywords.setOnDragDropped(dde -> {
            Dragboard db = dde.getDragboard();
            boolean success = false;
            if(db.hasFiles()) {
                success = true;
                File file = db.getFiles().get(0);
                String fileName = file.getName().replaceAll("\\..*", "");
                txtKeywords.setText(fileName);
            }
            dde.setDropCompleted(success);
            dde.consume();
        });
    }

    @FXML
    private void searchSubtitles() {
        String keywords = txtKeywords.getText();
        if(StringUtils.isNotBlank(keywords)) {
            try {
                ObservableList<Pair<String, String>> checkedLanguages = cbLanguages.getCheckModel().getCheckedItems();

                Collection<String> languages = checkedLanguages
                        .stream()
                        .map(Pair::getKey)
                        .collect(Collectors.toList());

                searchThread = subtitlesService.findSubtitles(keywords, languages);
            } catch (CredentialsException | SubtitleException e) {
                MessageBox.showExceptionForm(e);
            }
        } else {
            MessageBox.showWarning("You have not entered any keywords. No way to tell what you are looking for :)");
        }
    }

    private void saveFile(Subtitle s) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select a folder");
        File selectedDir = dirChooser.showDialog(txtKeywords.getScene().getWindow());
        if(selectedDir != null) {
            String selectedDirPath = selectedDir.getPath();
            String newFilePath = FilenameUtils.concat(selectedDirPath, s.getTitle());
            try {
                fileDownloader.save(newFilePath, s.getDownloadLink());
            } catch (DownloadException e) {
                MessageBox.showExceptionForm(e);
            }
        }
    }

    public void abortSearchingSubtitles() {
        if(searchThread != null) {
            searchThread.interrupt();
            searchThread = null;

            eventBus.post(new JobStateChangedEvent(JobType.DOWNLOADING, JobState.INTERRUPTED, fileDownloader.getOutputFilePath()));
            eventBus.post(new JobStateChangedEvent(JobType.SEARCHING, JobState.INTERRUPTED, fileDownloader.getOutputFilePath()));

            interruptCleanUp();
        }
    }

    private void interruptCleanUp() {
        btnInterrupt.setVisible(false);
        btnSearch.setVisible(true);
    }
}
