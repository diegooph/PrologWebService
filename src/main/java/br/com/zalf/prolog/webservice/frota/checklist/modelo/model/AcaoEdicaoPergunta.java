package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Ações que a pergunta pode sofrer na edição de um modelo de checklist.
 *
 * Created on 16/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum AcaoEdicaoPergunta {
    DELETADA("DELETADA"),
    ALTERADA_NOME("ALTERADA_NOME"),
    ALTERADA_INFOS("ALTERADA_INFOS"),
    CRIADA("CRIADA");

    @NotNull
    private final String string;

    AcaoEdicaoPergunta(@NotNull final String string) {
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