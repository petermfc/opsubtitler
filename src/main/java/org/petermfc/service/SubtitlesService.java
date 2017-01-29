package org.petermfc.service;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import org.petermfc.controls.MessageBox;
import org.petermfc.exception.CredentialsException;
import org.petermfc.exception.SubtitleException;
import org.petermfc.model.Config;
import org.petermfc.model.ProviderConfig;
import org.petermfc.model.Subtitle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SubtitlesService {
    private SubtitlesProvider oss;
    private Config config;

    private final ReadOnlyBooleanWrapper searchRunning = new ReadOnlyBooleanWrapper();
    public ReadOnlyBooleanProperty searchRunningProperty() {
        return searchRunning.getReadOnlyProperty();
    }
    public SubtitlesService(Config config) {
        this.oss = new OpenSubtitlesProvider();
        oss.setConfigurationCallback(this::getConfig);
        this.config = config;
    }

    private Collection<Subtitle> resultsList =  Collections.emptySet();

    public Collection<Subtitle> getResultsList() {
        return resultsList;
    }

    public Thread findSubtitles(String query, Collection<String> languages) throws SubtitleException, CredentialsException {

        final Collection<Subtitle> subtitleCollection = new ArrayList<>();
        Thread findThread = new Thread(() -> {
            searchRunning.set(true);
            try {
                subtitleCollection.addAll(oss.findSubtitles(query, languages));

            } catch (CredentialsException | SubtitleException e) {
                MessageBox.showExceptionForm(e);
            } finally {
                if(!Thread.currentThread().isInterrupted()) {
                    resultsList = subtitleCollection;
                    searchRunning.set(false);
                }
            }
        });
        findThread.start();
        return findThread;
    }

    private ProviderConfig getConfig(Class<?> clazz) {
        return config.getServiceConfig(clazz);
    }
}
