package br.com.zalf.prolog.webservice.colaborador.model;

import org.jetbrains.annotations.NotNull;

/**
 * Informações sobre o telefone do colaborador.
 *
 * Created on 2020-01-23
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ColaboradorTelefone {
    @NotNull
    private final Integer prefixoPais;
    @NotNull
    private final String numero;

    public ColaboradorTelefone(@NotNull final Integer prefixoPais,
                               @NotNull final String numero) {
        this.prefixoPais = prefixoPais;
        this.numero = numero;
    }

    @NotNull
    public Integer getPrefixoPais() {
        return prefixoPais;
    }

    @NotNull
    public String getNumero() {
        return numero;
    }
}
