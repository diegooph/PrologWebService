package br.com.zalf.prolog.webservice.frota.pneu._model;

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
    private final boolean fotoSincronizada;

    public PneuFotoCadastro(@NotNull final Long codigo, @NotNull final String urlFoto, final boolean fotoSincronizada) {
        this.codigo = codigo;
        this.urlFoto = urlFoto;
        this.fotoSincronizada = fotoSincronizada;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getUrlFoto() {
        return urlFoto;
    }

    public boolean isFotoSincronizada() {
        return fotoSincronizada;
    }
}