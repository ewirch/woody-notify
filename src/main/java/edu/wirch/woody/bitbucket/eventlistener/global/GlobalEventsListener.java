package edu.wirch.woody.bitbucket.eventlistener.global;

import com.atlassian.bitbucket.event.auth.AuthenticationFailureEvent;
import com.atlassian.bitbucket.event.backup.BackupFailedEvent;
import com.atlassian.bitbucket.event.maintenance.MaintenanceEndedEvent;
import com.atlassian.bitbucket.event.maintenance.MaintenanceStartedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import edu.wirch.woody.bitbucket.conf.GlobalConfiguration;
import edu.wirch.woody.bitbucket.conf.PluginSettingsStore;
import edu.wirch.woody.bitbucket.eventlistener.pullrequest.StringWriterTemplateRenderer;
import edu.wirch.woody.chatservice.ChatServiceFactory;
import edu.wirch.woody.chatservice.Target;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

@Component
public class GlobalEventsListener {
    private static final String BASE_PATH = "/edu/wirch/woody/bitbucket/notification/global/";

    private final ChatServiceFactory chatServiceFactory;
    private final StringWriterTemplateRenderer templateRenderer;
    private final PluginSettingsStore settingsStore;

    @Autowired
    public GlobalEventsListener(final ChatServiceFactory chatServiceFactory,
                                @ComponentImport final TemplateRenderer templateRenderer,
                                final PluginSettingsStore settingsStore) {
        this.chatServiceFactory = chatServiceFactory;
        this.templateRenderer = new StringWriterTemplateRenderer(templateRenderer);
        this.settingsStore = settingsStore;
    }


    @EventListener
    public void onAuthenticationFailure(final AuthenticationFailureEvent event) {
        final Map<String, Object> context = new HashMap<>();
        context.put("user", event.getUsername());

        sendNotificationIfEnabled(enabledBy(GlobalConfiguration::isAuthenticationFailureEvent), "authFailed.vm", context);
    }

    @EventListener
    public void onBackupFailed(final BackupFailedEvent event) {
        sendNotificationIfEnabled(enabledBy(GlobalConfiguration::isBackupFailedEvent), "backupFailed.vm", emptyMap());
    }

    @EventListener
    public void onMaintenanceStarted(final MaintenanceStartedEvent event) {
        sendNotificationIfEnabled(enabledBy(GlobalConfiguration::isMaintenanceStartedEvent), "maintenanceStarted.vm", emptyMap());
    }

    @EventListener
    public void onMaintenanceEnded(final MaintenanceEndedEvent event) {
        sendNotificationIfEnabled(enabledBy(GlobalConfiguration::isMaintenanceStartedEvent), "maintenanceEnded.vm", emptyMap());
    }

    private void sendNotificationIfEnabled(final Enabled enabled, final String template, final Map<String, Object> context) {
        final Optional<String> chat = enabled.getChat();
        //noinspection OptionalIsPresent
        if (chat.isPresent()) {
            chatServiceFactory.create()
                              .send(Target.ofEscaped(chat.get()), templateRenderer.render(context,BASE_PATH + template));
        }
    }


    private Enabled enabledBy(final Function<GlobalConfiguration, Boolean> isEventEnabled) {
        return new Enabled(isEventEnabled);
    }

    private final class Enabled {
        private final GlobalConfiguration conf;
        private final String chat;

        private final Function<GlobalConfiguration, Boolean> isEventEnabled;

        private Enabled(final Function<GlobalConfiguration, Boolean> isEventEnabled) {
            this.isEventEnabled = isEventEnabled;
            this.conf = settingsStore.load();
            this.chat = conf.getGlobalEventChannel();
        }

        Optional<String> getChat() {
            if (!isEventEnabled.apply(conf)) return Optional.empty();
            if (StringUtils.isBlank(chat)) return Optional.empty();
            return Optional.of(chat);
        }
    }
}
