package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuFotoCadastro {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String urlFoto;

    public PneuFotoCadastro(final Long codigo, final String urlFoto) {
        this.codigo = codigo;
        this.urlFoto = urlFoto;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getUrlFoto() {
        return urlFoto;
    }
}