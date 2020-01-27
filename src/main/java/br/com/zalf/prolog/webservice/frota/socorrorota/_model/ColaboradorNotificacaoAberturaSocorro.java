package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import br.com.zalf.prolog.webservice.push.send.PushDestination;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColaboradorNotificacaoAberturaSocorro implements PushDestination {
    @NotNull
    private final String tokenPushFirebase;

    public ColaboradorNotificacaoAberturaSocorro(@NotNull final String tokenPushFirebase) {
        this.tokenPushFirebase = tokenPushFirebase;
    }

    @NotNull
    public String getTokenPushFirebase() {
        return tokenPushFirebase;
    }

    @NotNull
    @Override
    public String provideTokenPushFirebase() {
        return getTokenPushFirebase();
    }
}
