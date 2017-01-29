package org.petermfc.model;

import java.util.HashMap;
import java.util.Map;

public class Config {
    private Map<Class<?>, ProviderConfig> servicesConfiguration = new HashMap<>();

    public Config() {

    }

    public void setServiceConfig(Class<?> clazz, ProviderConfig pc) {
        servicesConfiguration.put(clazz, pc);
    }

    public ProviderConfig getServiceConfig(Class<?> clazz) {
        return servicesConfiguration.get(clazz);
    }
}
