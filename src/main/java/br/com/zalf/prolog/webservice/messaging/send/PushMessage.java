package br.com.zalf.prolog.webservice.messaging.send;

import br.com.zalf.prolog.webservice.messaging.AndroidAppScreens;
import br.com.zalf.prolog.webservice.messaging.AndroidLargeIcon;
import br.com.zalf.prolog.webservice.messaging.AndroidSmallIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PushMessage {
    @NotNull
    private final String title;
    @NotNull
    private final String body;
    @Nullable
    private final AndroidSmallIcon androidSmallIcon;
    @Nullable
    private final AndroidLargeIcon androidLargeIcon;
    @Nullable
    private final String imageUrl;
    @Nullable
    private final AndroidAppScreens screenToNavigate;
    @Nullable
    private final String screenMetadata;

    public PushMessage(@NotNull final String title,
                       @NotNull final String body,
                       @Nullable final AndroidSmallIcon androidSmallIcon,
                       @Nullable final AndroidLargeIcon androidLargeIcon,
                       @Nullable final String imageUrl,
                       @Nullable final AndroidAppScreens screenToNavigate,
                       @Nullable final String screenMetadata) {
        this.title = title;
        this.body = body;
        this.androidSmallIcon = androidSmallIcon;
        this.androidLargeIcon = androidLargeIcon;
        this.imageUrl = imageUrl;
        this.screenToNavigate = screenToNavigate;
        this.screenMetadata = screenMetadata;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public String getBody() {
        return body;
    }

    @Nullable
    public AndroidSmallIcon getAndroidSmallIcon() {
        return androidSmallIcon;
    }

    @Nullable
    public AndroidLargeIcon getAndroidLargeIcon() {
        return androidLargeIcon;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    @Nullable
    public AndroidAppScreens getScreenToNavigate() {
        return screenToNavigate;
    }

    @Nullable
    public String getScreenMetadata() {
        return screenMetadata;
    }

    @NotNull
    public Map<String, String> getData() {
        final Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("body", body);
        if (androidSmallIcon != null) {
            map.put("androidSmallIcon", androidSmallIcon.getIconIdAsString());
        }
        if (androidLargeIcon != null) {
            map.put("androidLargeIcon", androidLargeIcon.getIconIdAsString());
        }
        if (imageUrl != null) {
            map.put("imageUrl", imageUrl);
        }
        if (screenToNavigate != null) {
            map.put("screenToNavigate", screenToNavigate.getScreenIdAsString());
        }
        if (screenMetadata != null) {
            map.put("screenMetadata", screenMetadata);
        }
        return map;
    }

    @NotNull
    public String getFullMessageAsString() {
        return toString();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "PushMessage{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", androidSmallIcon=" + androidSmallIcon +
                ", androidLargeIcon=" + androidLargeIcon +
                ", imageUrl='" + imageUrl + '\'' +
                ", screenToNavigate=" + screenToNavigate +
                ", screenMetadata='" + screenMetadata + '\'' +
                '}';
    }

    public static final class Builder {
        private String title;
        private String body;
        private AndroidSmallIcon androidSmallIcon;
        private AndroidLargeIcon androidLargeIcon;
        private String imageUrl;
        private AndroidAppScreens screenToNavigate;
        private String metadataScreen;

        private Builder() {

        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public Builder withAndroidSmallIcon(AndroidSmallIcon androidSmallIcon) {
            this.androidSmallIcon = androidSmallIcon;
            return this;
        }

        public Builder withAndroidLargeIcon(AndroidLargeIcon androidLargeIcon) {
            this.androidLargeIcon = androidLargeIcon;
            return this;
        }

        public Builder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder withScreenToNavigate(AndroidAppScreens screenToNavigate) {
            this.screenToNavigate = screenToNavigate;
            return this;
        }

        public Builder withMetadataScreen(String metadataScreen) {
            this.metadataScreen = metadataScreen;
            return this;
        }

        @NotNull
        public PushMessage build() {
            return new PushMessage(
                    title,
                    body,
                    androidSmallIcon,
                    androidLargeIcon,
                    imageUrl,
                    screenToNavigate,
                    metadataScreen);
        }
    }
}
