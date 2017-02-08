package edu.wirch.woody.bitbucket.conf;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
public class Presenter {
    private static final String TEMPLATE = "/edu/wirch/woody/bitbucket/conf/globalConfig.vm";

    private final UserManager userManager;
    private final TemplateRenderer templateRenderer;
    private final PluginSettingsStore settingsStore;

    @Autowired
    public Presenter(@ComponentImport final UserManager userManager,
                     @ComponentImport final TemplateRenderer templateRenderer,
                     final PluginSettingsStore settingsStore) {
        this.userManager = userManager;
        this.templateRenderer = templateRenderer;
        this.settingsStore = settingsStore;
    }

    boolean get(final Writer out) throws IOException {
        if (!isAdmin()) return false;

        final Map<String, Object> context = new HashMap<>();
        settingsStore.load()
                     .toMap(context);
        render(out, context);
        return true;
    }

    void set(final Map<String, String> params, final Writer out) throws IOException {
        final GlobalConfiguration.Builder builder = settingsStore.loadBuilder();
        builder.fromMap(params);

        final Map<String, Object> context = new HashMap<>();
        final Map<String, String> errors = GlobalConfiguration.validator(builder)
                                                              .validate();
        final GlobalConfiguration conf = builder.build();
        if (errors.isEmpty()) {
            settingsStore.store(conf);
            context.put("saved", true);
        } else {
            context.put("errors", errors);
        }
        conf.toMap(context);
        render(out, context);
    }


    private boolean isAdmin() {
        final UserKey userKey = userManager.getRemoteUserKey();
        return userKey != null && userManager.isSystemAdmin(userKey);
    }

    private void render(final Writer out, final Map<String, Object> context) throws IOException {
        templateRenderer.render(TEMPLATE, context, out);
    }
}
