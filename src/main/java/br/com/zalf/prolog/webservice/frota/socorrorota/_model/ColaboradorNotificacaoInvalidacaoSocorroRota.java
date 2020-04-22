package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import br.com.zalf.prolog.webservice.messaging.push._model.PushDestination;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-04-16
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColaboradorNotificacaoInvalidacaoSocorroRota implements PushDestination {
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final String tokenPushFirebase;

    public ColaboradorNotificacaoInvalidacaoSocorroRota(@NotNull final Long codColaborador,
                                                        @NotNull final String tokenPushFirebase) {
        this.codColaborador = codColaborador;
        this.tokenPushFirebase = tokenPushFirebase;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @NotNull
    @Override
    public String getTokenPushFirebase() {
        return tokenPushFirebase;
    }

    @NotNull
    @Override
    public String getUserIdAssociatedWithToken() {
        return String.valueOf(codColaborador);
    }
}
