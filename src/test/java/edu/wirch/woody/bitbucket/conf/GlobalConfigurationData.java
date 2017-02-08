package edu.wirch.woody.bitbucket.conf;

public class GlobalConfigurationData {

    private static final String WEBHOOK_DEFAULT = "http://server.com/some/64aeou4a6u4e6u4ao";

    private GlobalConfigurationData() {
    }

    public static GlobalConfiguration.Builder withDefaults() {
        return GlobalConfiguration.builder().setWebhook(WEBHOOK_DEFAULT);
    }
}
