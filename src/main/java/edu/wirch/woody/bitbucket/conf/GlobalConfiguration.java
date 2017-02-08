package edu.wirch.woody.bitbucket.conf;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.unmodifiableMap;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalConfiguration {
    private final String webhook;

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

        void fromMap(final Map<String, String> map) {
            new Mapper().fromMap(map, this);
        }

        GlobalConfiguration build() {
            return new GlobalConfiguration(webhook);
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

    static class Mapper {
        private static final String WEBHOOK = "webhook";

        void toMap(final GlobalConfiguration conf, final Map<String, Object> map) {
            map.put(WEBHOOK, conf.getWebhook());
        }

        void fromMap(final Map<String, String> map, final GlobalConfiguration.Builder builder) {
            ifExisting(WEBHOOK, map, builder::setWebhook);
        }

        private void ifExisting(final String key, final Map<String, String> map, final Consumer<String> consumer) {
            if (!map.containsKey(key)) return;

            final String value = map.get(key);
            if (value == null) return;

            consumer.accept(value);
        }
    }

}
