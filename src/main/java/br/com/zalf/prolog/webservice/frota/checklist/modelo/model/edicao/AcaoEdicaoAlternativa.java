package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import org.jetbrains.annotations.NotNull;

/**
 * Ações que a alternativa pode sofrer na edição de um modelo de checklist.
 *
 * Created on 16/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum AcaoEdicaoAlternativa {
    DELETADA("DELETADA"),
    ALTERADA("ALTERADA"),
    CRIADA("CRIADA");

    @NotNull
    private final String string;

    AcaoEdicaoAlternativa(@NotNull final String string) {
        this.string = string;
    }

    @NotNull
    public String asString() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }
}