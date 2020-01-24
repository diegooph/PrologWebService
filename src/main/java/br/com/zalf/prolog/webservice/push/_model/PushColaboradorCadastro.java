package br.com.zalf.prolog.webservice.push._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PushColaboradorCadastro {
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final String tokenPushFirebase;

    public PushColaboradorCadastro(@NotNull final Long codColaborador,
                                   @NotNull final String tokenPushFirebase) {
        this.codColaborador = codColaborador;
        this.tokenPushFirebase = tokenPushFirebase;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @NotNull
    public String getTokenPushFirebase() {
        return tokenPushFirebase;
    }
}
