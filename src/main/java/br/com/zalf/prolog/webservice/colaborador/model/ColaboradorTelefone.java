package br.com.zalf.prolog.webservice.colaborador.model;

import org.jetbrains.annotations.NotNull;

/**
 * Informações sobre o telefone do colaborador.
 */
public class ColaboradorTelefone {
    @NotNull
    private Integer prefixoPais;

    @NotNull
    private String telefone;

    public ColaboradorTelefone(@NotNull Integer prefixoPais,
                               @NotNull String telefone) {
        this.prefixoPais = prefixoPais;
        this.telefone = telefone;
    }

    public Integer getPrefixoPais() {
        return prefixoPais;
    }

    public String getTelefone() {
        return telefone;
    }
}
