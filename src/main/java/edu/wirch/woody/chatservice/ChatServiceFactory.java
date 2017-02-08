package edu.wirch.woody.chatservice;

import edu.wirch.woody.bitbucket.conf.PluginSettingsStore;
import edu.wirch.woody.chatservice.slack.SlackChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatServiceFactory {

    private final PluginSettingsStore settingsStore;

    @Autowired
    public ChatServiceFactory(final PluginSettingsStore settingsStore) {
        this.settingsStore = settingsStore;
    }

    public ChatService create() {
        final String webhook = settingsStore.load()
                                            .getWebhook();
        if (StringUtils.isBlank(webhook)) {
            return new NopChatService();
        }
        return new SlackChatService(webhook);
    }

}
