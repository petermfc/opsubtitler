package org.petermfc.presenter;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutPresenter implements Initializable {
    private static final String OPENSUBTITLES_URL = "http://www.opensubtitles.org";

    @Inject
    private HostServices hostServices;

    @FXML
    public Button btnOkay;
    @FXML
    public ImageView osLogo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String path = getClass().getResource("/oslogo.gif").toString();
        osLogo.setImage(new Image(path));
        osLogo.setOnMouseClicked(e -> hostServices.showDocument(OPENSUBTITLES_URL));
        Tooltip.install(osLogo, new Tooltip("Click to visit the site"));

        btnOkay.setOnAction(e -> {
            Stage stage = (Stage)btnOkay.getScene().getWindow();
            stage.close();
        });
    }
}
