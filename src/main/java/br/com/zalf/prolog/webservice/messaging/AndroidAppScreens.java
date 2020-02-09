package br.com.zalf.prolog.webservice.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-27
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum AndroidAppScreens {
    VISUALIZAR_SOCORRO_ROTA(1);

    private final int screenId;

    AndroidAppScreens(final int screenId) {
        this.screenId = screenId;
    }

    public int getScreenId() {
        return screenId;
    }

    @NotNull
    public String getScreenIdAsString() {
        return String.valueOf(screenId);
    }
}
