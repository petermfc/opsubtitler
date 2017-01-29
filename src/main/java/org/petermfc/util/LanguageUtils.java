package org.petermfc.util;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LanguageUtils {
    public static List<Pair<String, String>> prepareLanguages() {
        String[] languages = Locale.getISOLanguages();
        List<Pair<String, String>> languagesList = new ArrayList<>(languages.length);
        for(String lang : languages) {
            Locale locale = new Locale(lang);
            languagesList.add(new Pair<>(locale.getISO3Language(), locale.getDisplayLanguage()));
        }
        languagesList.sort(Comparator.comparing(Pair::getValue));
        return languagesList;
    }
}
