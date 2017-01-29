package org.petermfc.presenter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.StatusBar;
import org.petermfc.Globals.JobState;
import org.petermfc.controls.MessageBox;
import org.petermfc.event.JobStateChangedEvent;
import org.petermfc.event.StatusChangeEvent;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppPresenter implements Initializable {

    @Inject
    private EventBus eventBus;

    @FXML
    public TabPane tabPane;
    @FXML
    public ProgressBar statusProgressBar;
    @FXML
    public StatusBar statusBar;
    @FXML
    public Label statusSavedFilePath;
    @FXML
    public Label statusText;
    @FXML
    public ProgressBar statusOperationIndicator;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventBus.register(this);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        statusSavedFilePath.setOnMouseEntered(e -> {
            statusSavedFilePath.getScene().setCursor(Cursor.HAND);
            statusSavedFilePath.setStyle("-fx-underline: true; -fx-stroke: blue;");
        });
        statusSavedFilePath.setOnMouseExited(e -> {
            statusSavedFilePath.getScene().setCursor(Cursor.DEFAULT);
            statusSavedFilePath.setStyle("-fx-underline: false; -fx-stroke: default;");
        });
        statusSavedFilePath.setOnMouseClicked(e -> {
                    try {
                        Desktop.getDesktop().open(new File(statusSavedFilePath.getText()).getParentFile());
                    } catch (IOException ex) {
                        MessageBox.showExceptionForm(ex);
                    }
                }
        );
    }

    @Subscribe
    public void statusUpdateHandler(StatusChangeEvent event) {
        statusText.setText(event.getMessageText());
    }

    @Subscribe
    public void jobsHandler(JobStateChangedEvent event) {
        boolean isInterrputed = JobState.INTERRUPTED.equals(event.getJobState());
        switch (event.getJobType()) {
            case DOWNLOADING: {
                if (JobState.PROGRESS_UPDATE.equals(event.getJobState())) {
                    Platform.runLater(() -> statusProgressBar.setProgress(event.getDoubleData()));
                } else if (JobState.STARTED.equals(event.getJobState())) {
                    Platform.runLater(() -> {
                        statusProgressBar.setVisible(true);
                        statusText.setText("Downloading");
                    });
                } else if (JobState.FINISHED.equals(event.getJobState())) {
                    Platform.runLater(() -> {
                        statusText.setText("Download finished!:");
                        statusSavedFilePath.setVisible(true);
                        statusSavedFilePath.setText(event.getStringData());
                        statusProgressBar.setVisible(false);
                    });
                }
            }
            break;
            case SEARCHING: {
                if (JobState.STARTED.equals(event.getJobState())) {
                    Platform.runLater(() -> {
                        statusSavedFilePath.setVisible(false);
                        statusOperationIndicator.setVisible(true);
                        statusOperationIndicator.setProgress(-1);
                        statusText.setText("Searching...");
                    });
                } else if (JobState.FINISHED.equals(event.getJobState())) {
                    Platform.runLater(() -> {
                        statusOperationIndicator.setProgress(1);
                        statusOperationIndicator.setVisible(false);
                        statusText.setText(event.getStringData() + " items found.");
                    });
                }
                if (isInterrputed) {
                    Platform.runLater(() -> {
                        statusOperationIndicator.setVisible(false);
                        statusText.setText("Operation aborted!");
                    });
                }
            }
            break;
        }
    }
}
