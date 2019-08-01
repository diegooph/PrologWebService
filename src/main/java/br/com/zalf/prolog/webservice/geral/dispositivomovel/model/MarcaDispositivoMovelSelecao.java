package br.com.zalf.prolog.webservice.geral.dispositivomovel.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class MarcaDispositivoMovelSelecao {
    @NotNull
    private final Long codMarca;
    private final String nome;

    public MarcaDispositivoMovelSelecao(
            @NotNull final Long codMarca,
            @NotNull final String nome) {
        this.codMarca = codMarca;
        this.nome = nome;
    }

    @NotNull
    public Long getCodMarca() {
        return codMarca;
    }

    public String getNome() {
        return nome;
    }
}
