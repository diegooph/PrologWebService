package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColaboradorNotificacaoAberturaSocorroRota {
    @NotNull
    private final Long codColaborador;
    @Nullable
    private final String emailColaborador;
    @Nullable
    private final String[] tokensPushFirebase;

    public ColaboradorNotificacaoAberturaSocorroRota(@NotNull final Long codColaborador,
                                                     @Nullable final String emailColaborador,
                                                     @Nullable final String[] tokensPushFirebase) {
        this.codColaborador = codColaborador;
        this.emailColaborador = emailColaborador;
        this.tokensPushFirebase = tokensPushFirebase;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @Nullable
    public String getEmailColaborador() {
        return emailColaborador;
    }

    @Nullable
    public String[] getTokensPushFirebase() {
        return tokensPushFirebase;
    }
}
