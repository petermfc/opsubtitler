package org.petermfc.presenter;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.petermfc.Globals.Setting;
import org.petermfc.controls.MessageBox;
import org.petermfc.event.StatusChangeEvent;
import org.petermfc.model.Config;
import org.petermfc.model.ProviderConfig;
import org.petermfc.service.OpenSubtitlesProvider;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingsPresenter implements Initializable {
    private final static String CONFIG_FILE = "config.properties";
    @Inject
    private EventBus eventBus;
    @Inject
    private Config config;
    @FXML
    public CheckBox chUserCred;
    @FXML
    public CheckBox chUserAgent;
    @FXML
    public TextField txtUserAgent;
    @FXML
    public TextField txtUsername;
    @FXML
    public TextField txtPassword;

    @FXML
    public Button btnSave;
    @FXML
    public Button btnRestoreDefaults;

    private PropertiesConfiguration propertyConfiguration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        propertyConfiguration = new PropertiesConfiguration();
        txtUserAgent.disableProperty().bind(chUserAgent.selectedProperty().not());
        txtUsername.disableProperty().bind(chUserCred.selectedProperty().not());
        txtPassword.disableProperty().bind(chUserCred.selectedProperty().not());

        txtUserAgent.setOnKeyPressed((e) -> {btnSave.setDisable(false); btnRestoreDefaults.setDisable(false); });
        txtUsername.setOnKeyPressed((e) -> {btnSave.setDisable(false); btnRestoreDefaults.setDisable(false); });
        txtPassword.setOnKeyPressed((e) -> {btnSave.setDisable(false); btnRestoreDefaults.setDisable(false); });
        chUserCred.setOnAction((e) -> {btnSave.setDisable(false); btnRestoreDefaults.setDisable(false); });
        chUserAgent.setOnAction((e) -> {btnSave.setDisable(false); btnRestoreDefaults.setDisable(false); });

        btnSave.setOnAction(event -> saveConfig());

        btnRestoreDefaults.setOnAction(event -> {
            boolean res = MessageBox.showYesNoQuestion("Are you sure?");
            if(res) {
                propertyConfiguration.clear();
                chUserAgent.setSelected(false);
                chUserCred.setSelected(false);
                txtUserAgent.setText(null);
                txtUsername.setText(null);
                txtPassword.setText(null);
                eventBus.post(new StatusChangeEvent("Defaults restored"));
            }
        });

        initConfig(null);
    }

    private void saveConfig() {
        boolean allGood = true;
        char[] pass = null;
        try {
            File fileConf = new File(CONFIG_FILE);
            if (!fileConf.exists()) {
                if (!fileConf.createNewFile()) {
                    allGood = false;
                }
            }
            if (allGood) {
                propertyConfiguration.clear();
                propertyConfiguration.setProperty(Setting.USE_CUSTOM_AGENT, chUserAgent.isSelected());
                propertyConfiguration.setProperty(Setting.USE_CUSTOM_LOGIN_DATA, chUserCred.isSelected());
                propertyConfiguration.setProperty(Setting.USER_AGENT, txtUserAgent.getText());
                propertyConfiguration.setProperty(Setting.USER_NAME, txtUsername.getText());
                pass = txtPassword.getText().toCharArray();

                propertyConfiguration.save(fileConf);
                eventBus.post(new StatusChangeEvent("Settings saved: " + CONFIG_FILE));

            } else {
                MessageBox.showError("Cannot write settings to file: " + fileConf.getAbsolutePath());
            }
        } catch (IOException | ConfigurationException e) {
            MessageBox.showExceptionForm(e);
            allGood = false;
        }
        if(allGood) {
            initConfig(pass);
        }
    }

    private void initConfig(char[] pass) {
        File fileConf = new File(CONFIG_FILE);
        if(fileConf.exists()) {
            try {
                propertyConfiguration.clear();
                propertyConfiguration.load(fileConf);

                Map<String, String> osSettings = new HashMap<>();
                osSettings.put(Setting.USE_CUSTOM_AGENT, (String)propertyConfiguration.getProperty(Setting.USE_CUSTOM_AGENT));
                osSettings.put(Setting.USE_CUSTOM_LOGIN_DATA, (String)propertyConfiguration.getProperty(Setting.USE_CUSTOM_LOGIN_DATA));
                osSettings.put(Setting.USER_AGENT, (String) propertyConfiguration.getProperty(Setting.USER_AGENT));
                osSettings.put(Setting.USER_NAME, (String) propertyConfiguration.getProperty(Setting.USER_NAME));

                chUserAgent.setSelected(Boolean.valueOf(osSettings.get(Setting.USE_CUSTOM_AGENT)));
                chUserCred.setSelected(Boolean.valueOf(osSettings.get(Setting.USE_CUSTOM_LOGIN_DATA)));
                txtUserAgent.setText(osSettings.get(Setting.USER_AGENT));
                txtUsername.setText(osSettings.get(Setting.USER_NAME));
                txtPassword.setText(osSettings.get(Setting.PASSWORD));

                config.setServiceConfig(OpenSubtitlesProvider.class, new ProviderConfig(osSettings, pass));
            } catch (ConfigurationException e) {
                MessageBox.showExceptionForm(e);
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        txtUserAgent.disableProperty().unbind();
        txtUsername.disableProperty().unbind();
        txtPassword.disableProperty().unbind();
    }
}
