package edu.wirch.woody.bitbucket.conf;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.Collections.unmodifiableMap;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalConfiguration {
    private final String webhook;
    private final String globalEventChannel;
    private final boolean authenticationFailureEvent;
    private final boolean backupFailedEvent;
    private final boolean maintenanceStartedEvent;
    private final boolean maintenanceEndedEvent;

    static Builder builder() {
        return new Builder();
    }

    static Validator validator(final Builder builder) {
        return new Validator(builder);
    }

    static Mapper mapper() {
        return new Mapper();
    }

    void toMap(final Map<String, Object> map) {
        new Mapper().toMap(this, map);
    }

    @Data
    @Accessors(chain = true)
    public static class Builder {
        String webhook = "";
        String globalEventChannel;
        boolean authenticationFailureEvent;
        boolean backupFailedEvent;
        boolean maintenanceStartedEvent;
        boolean maintenanceEndedEvent;

        public Builder fromMap(final Map<String, String> map) {
            new Mapper().fromMap(map, this);
            return this;
        }

        public GlobalConfiguration build() {
            return new GlobalConfiguration(webhook, globalEventChannel, authenticationFailureEvent, backupFailedEvent, maintenanceStartedEvent,
                    maintenanceEndedEvent);
        }
    }

    @RequiredArgsConstructor
    static class Validator {
        private final Builder builder;
        private final Map<String, String> errors = new HashMap<>();

        Map<String, String> validate() {
            errors.clear();
            validateWebHook();
            return unmodifiableMap(errors);
        }

        private void validateWebHook() {
            if (StringUtils.isBlank(builder.getWebhook())) {
                errors.put("webhook", "Must not be empty.");
                return;
            }
            if (!builder.getWebhook()
                        .startsWith("http")) {
                errors.put("webhook", "Should be a http or https protocol.");
                return;
            }
        }
    }

    @Slf4j
    static class Mapper {
        private static final String WEBHOOK = "webhook";
        private static final String GLOBALEVENTCHANNEL = "globalEventChannel";
        private static final String AUTHENTICATIONFAILUREEVENT = "authenticationFailureEvent";
        private static final String BACKUPFAILEDEVENT = "backupFailedEvent";
        private static final String MAINTENANCESTARTEDEVENT = "maintenanceStartedEvent";
        private static final String MAINTENANCEENDEDEVENT = "maintenanceEndedEvent";

        void toMap(final GlobalConfiguration conf, final Map<String, Object> map) {
            map.put(WEBHOOK, getOrDefault(conf.getWebhook(), ""));
            map.put(GLOBALEVENTCHANNEL, getOrDefault(conf.getGlobalEventChannel(), ""));
            map.put(AUTHENTICATIONFAILUREEVENT, conf.isAuthenticationFailureEvent());
            map.put(BACKUPFAILEDEVENT, conf.isBackupFailedEvent());
            map.put(MAINTENANCESTARTEDEVENT, conf.isMaintenanceStartedEvent());
            map.put(MAINTENANCEENDEDEVENT, conf.isMaintenanceEndedEvent());
        }

        private String getOrDefault(final String value, final String defaultValue) {
            return value == null ? defaultValue : value;
        }

        void fromMap(final Map<String, String> map, final GlobalConfiguration.Builder builder) {
            ifExisting(WEBHOOK, map, builder::setWebhook);
            ifExisting(GLOBALEVENTCHANNEL, map, builder::setGlobalEventChannel);
            builder.setAuthenticationFailureEvent(convert(map.get(AUTHENTICATIONFAILUREEVENT)));
            builder.setBackupFailedEvent(convert(map.get(BACKUPFAILEDEVENT)));
            builder.setMaintenanceStartedEvent(convert(map.get(MAINTENANCESTARTEDEVENT)));
            builder.setMaintenanceEndedEvent(convert(map.get(MAINTENANCEENDEDEVENT)));
        }

        private Boolean convert(final String value) {
            if (value == null) {return false;}
            return Objects.equals("on", value);
        }

        private void ifExisting(final String key, final Map<String, String> map, final Consumer<String> consumer) {
            if (!map.containsKey(key)) return;

            final String value = map.get(key);
            if (value == null) return;

            consumer.accept(value);
        }
    }

}
