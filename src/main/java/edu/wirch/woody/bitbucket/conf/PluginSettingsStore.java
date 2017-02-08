package edu.wirch.woody.bitbucket.conf;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.base.Throwables;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@Component
@ParametersAreNonnullByDefault
public class PluginSettingsStore {
    private static final String KEY = "edu.wirch.woody.bitbucket.conf";
    private static final String GLOBAL_CONF = "global";

    private final ObjectMapper objectMapper;
    private final PluginSettings settingsStore;

    @Autowired
    public PluginSettingsStore(@ComponentImport final PluginSettingsFactory pluginSettingsFactory) {
        objectMapper = new ObjectMapper();
        settingsStore = pluginSettingsFactory.createSettingsForKey(KEY);
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    void store(final GlobalConfiguration conf) {
        try {
            final String serialized = objectMapper.writeValueAsString(conf);
            settingsStore.put(GLOBAL_CONF, serialized);
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public GlobalConfiguration load() {
        return loadBuilder().build();
    }

    GlobalConfiguration.Builder loadBuilder() {
        final Object value = settingsStore.get(GLOBAL_CONF);
        if (!(value instanceof String)) {
            return GlobalConfiguration.builder();
        }

        return parse((String) value);
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private GlobalConfiguration.Builder parse(final String value) {
        try {
            return objectMapper.readValue(value, GlobalConfiguration.Builder.class);
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
