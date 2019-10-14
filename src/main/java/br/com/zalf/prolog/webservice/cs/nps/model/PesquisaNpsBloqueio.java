package br.com.zalf.prolog.webservice.cs.nps.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-10-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PesquisaNpsBloqueio {
    @NotNull
    private final Long codPesquisaNps;
    @NotNull
    private final Long codColaboradorBloqueio;

    public PesquisaNpsBloqueio(@NotNull final Long codPesquisaNps,
                                @NotNull final Long codColaboradorBloqueio) {
        this.codPesquisaNps = codPesquisaNps;
        this.codColaboradorBloqueio = codColaboradorBloqueio;
    }

    @NotNull
    public Long getCodPesquisaNps() {
        return codPesquisaNps;
    }

    @NotNull
    public Long getCodColaboradorBloqueio() {
        return codColaboradorBloqueio;
    }
}