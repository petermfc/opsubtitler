package org.petermfc.model;

import java.util.Map;

public class ProviderConfig {
    private Map<String, String> configMap;
    private char[] pass = null;

    public String getSetting(String key) {
        String ret = configMap.get(key);
        if(ret == null)
            ret = "";
        return ret;
    }

    public char[] getPassword() {
        return pass;
    }

    public ProviderConfig(Map<String, String> configMap, char[] pass) {
        this.configMap = configMap;
        this.pass = pass;
    }
}
