package org.petermfc.service;

import org.junit.Test;
import org.petermfc.exception.CredentialsException;
import org.petermfc.exception.SubtitleException;
import org.petermfc.model.Subtitle;

import java.util.Collection;
import java.util.Collections;

public class OpenSubtitlesProviderTest {
    @Test
    public void getMovieTest() throws SubtitleException, CredentialsException {
        OpenSubtitlesProvider oss = new OpenSubtitlesProvider();
        Collection<Subtitle> subtitles = oss.findSubtitles("Your favorite movie", Collections.singletonList("pol"));
        for(Subtitle subtitle : subtitles) {
            System.out.println(subtitle.toString());
        }
    }
}
