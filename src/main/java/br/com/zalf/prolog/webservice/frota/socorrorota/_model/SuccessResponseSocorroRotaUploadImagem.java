package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-02-13
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SuccessResponseSocorroRotaUploadImagem {
    @NotNull
    private final String urlImagem;

    public SuccessResponseSocorroRotaUploadImagem(@NotNull final String urlImagem) {
        this.urlImagem = urlImagem;
    }

    @NotNull
    public String getUrlImagem() {
        return urlImagem;
    }
}
