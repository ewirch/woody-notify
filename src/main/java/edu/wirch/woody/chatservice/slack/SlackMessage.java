package edu.wirch.woody.chatservice.slack;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
@Data
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SlackMessage {
    public static final String DEFAULT_USER_NAME = "Woody-Notify";
    public static final String DEFAULT_ICON_URL = "https://maxcdn.icons8.com/Color/PNG/48/Cinema/woody_woodpecker-48.png";

    String text;
    String channel;
    String userName;
    String icon_url;
    @Singular
    List<Attachment> attachments;

    public static SlackMessageBuilder defaultValues() {
        return builder().icon_url(DEFAULT_ICON_URL);
    }

    @Data
    @Builder
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class Attachment {
        public static final String COLOR_GOOD = "good";
        public static final String COLOR_WARNING = "warning";
        public static final String COLOR_DANGER = "danger";

        String fallback;
        String color;
        String pretext;
        String author_name;
        String author_link;
        String author_icon;
        String title;
        String title_link;
        String text;
        @Singular
        List<Field> fields;
        String image_url;
        String thumb_url;
        String footer;
        String footer_icon;
        long ts;
        @Singular("mrkdwn_in")
        List<String> mrkdwn_in;
    }

    @Data
    @Builder
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class Field {
        String title;
        String value;
        @Accessors(prefix = "f")
        boolean fShort;
    }
}
