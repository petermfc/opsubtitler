package org.petermfc.presenter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.petermfc.view.AboutView;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML
    public MenuBar menuBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void menuFile_Close(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public void menuHelp_About(ActionEvent event) {
        AboutView aboutView = new AboutView();
        Scene scene = new Scene(aboutView.getView());
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(menuBar.getScene().getWindow());
        dialogStage.setScene(scene);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.showAndWait();
    }
}
