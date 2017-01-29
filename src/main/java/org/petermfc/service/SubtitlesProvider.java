package org.petermfc.service;

import org.petermfc.exception.CredentialsException;
import org.petermfc.exception.SubtitleException;
import org.petermfc.model.ProviderConfig;
import org.petermfc.model.Subtitle;

import java.util.Collection;
import java.util.function.Function;

public interface SubtitlesProvider {
    void setConfigurationCallback(Function<Class<?>, ProviderConfig> fun);
    Collection<Subtitle> findSubtitles(String query, Collection<String> languages) throws SubtitleException, CredentialsException;
}
