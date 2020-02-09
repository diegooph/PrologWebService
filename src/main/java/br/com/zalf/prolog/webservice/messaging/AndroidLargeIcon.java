package br.com.zalf.prolog.webservice.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-27
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum AndroidLargeIcon {
    PROLOG(1),
    SOS_NOTIFICATION(2);

    private final int iconId;

    AndroidLargeIcon(final int iconId) {
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

    @NotNull
    public String getIconIdAsString() {
        return String.valueOf(iconId);
    }
}

