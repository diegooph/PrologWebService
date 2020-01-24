package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColaboradorNotificacaoAberturaSocorro {
    @NotNull
    private final String tokenPushFirebase;

    public ColaboradorNotificacaoAberturaSocorro(@NotNull final String tokenPushFirebase) {
        this.tokenPushFirebase = tokenPushFirebase;
    }

    @NotNull
    public String getTokenPushFirebase() {
        return tokenPushFirebase;
    }
}
