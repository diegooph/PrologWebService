package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Zart on 02/03/17.
 */
public enum OrigemDestinoEnum {
    VEICULO(StatusPneu.EM_USO),
    ESTOQUE(StatusPneu.ESTOQUE),
    DESCARTE(StatusPneu.DESCARTE),
    ANALISE(StatusPneu.ANALISE);

    @NotNull
    final StatusPneu statusPneu;

    OrigemDestinoEnum(@NotNull final StatusPneu statusPneu) {
        this.statusPneu = statusPneu;
    }

    @NotNull
    public String asString() {
        return statusPneu.asString();
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public StatusPneu toStatusPneu() {
        return statusPneu;
    }
}