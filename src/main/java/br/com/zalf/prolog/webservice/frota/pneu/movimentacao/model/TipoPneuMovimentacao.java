package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 26/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoPneuMovimentacao {

    PNEU_ANALISE("PNEU_ANALISE");

    @NotNull
    private final String string;

    TipoPneuMovimentacao(@NotNull final String string) {
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
