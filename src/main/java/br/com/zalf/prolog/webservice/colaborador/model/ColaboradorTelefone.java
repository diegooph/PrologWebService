package br.com.zalf.prolog.webservice.colaborador.model;

import org.jetbrains.annotations.NotNull;

/**
 * Informações sobre o telefone do colaborador.
 */
public class ColaboradorTelefone {
    @NotNull
    private Integer prefixoPais;

    @NotNull
    private String numero;

    public ColaboradorTelefone(@NotNull Integer prefixoPais,
                               @NotNull String numero) {
        this.prefixoPais = prefixoPais;
        this.numero = numero;
    }

    public Integer getPrefixoPais() {
        return prefixoPais;
    }

    public String getNumero() {
        return numero;
    }
}
