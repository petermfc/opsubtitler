package org.petermfc.model;

import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Subtitle {
    private final StringProperty title;
    private final StringProperty language;
    private final StringProperty downloadLink;
    private final StringProperty format;

    public Subtitle(String title, String downloadLink, String language, String format) {
        this.title = new SimpleStringProperty(title);
        this.downloadLink = new SimpleStringProperty(downloadLink);
        this.language = new SimpleStringProperty(language);
        this.format = new SimpleStringProperty(format);
    }

    public Subtitle(SubtitleInfo info) {
        this.title = new SimpleStringProperty(info.getFileName());
        this.language = new SimpleStringProperty(info.getLanguage());
        this.downloadLink = new SimpleStringProperty(info.getDownloadLink());
        this.format = new SimpleStringProperty(info.getFormat());
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty languageProperty() {
        return language;
    }

    public StringProperty downloadLinkProperty() {
        return downloadLink;
    }

    public StringProperty formatProperty() {
        return format;
    }

    public String getTitle() {
        return title.get();
    }

    public String getLanguage() {
        return language.get();
    }

    public String getDownloadLink() {
        return downloadLink.get();
    }

    public String getFormat() {
        return format.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subtitle subtitle = (Subtitle) o;

        if (!getTitle().equals(subtitle.getTitle())) return false;
        if (!getLanguage().equals(subtitle.getLanguage())) return false;
        if (!getDownloadLink().equals(subtitle.getDownloadLink())) return false;
        return getFormat().equals(subtitle.getFormat());
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + getLanguage().hashCode();
        result = 31 * result + getDownloadLink().hashCode();
        result = 31 * result + getFormat().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Subtitle{" +
                "title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", downloadLink='" + downloadLink + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
