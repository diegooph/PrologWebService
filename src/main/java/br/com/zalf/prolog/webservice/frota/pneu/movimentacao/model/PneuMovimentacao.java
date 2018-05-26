package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 25/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuMovimentacao extends Pneu {

    private final TipoPneuMovimentacao tipoPneuMovimentacao;

    public PneuMovimentacao(@NotNull final TipoPneuMovimentacao tipoPneuMovimentacao) {
        super();
        this.tipoPneuMovimentacao = tipoPneuMovimentacao;
    }

    public TipoPneuMovimentacao getTipoPneuMovimentacao() {
        return tipoPneuMovimentacao;
    }

    @Override
    public String toString() {
        return "PneuMovimentacao{" +
                "tipoPneuMovimentacao=" + tipoPneuMovimentacao +
                '}';
    }
}
