package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model;

import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;

/**
 * Created on 25/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuMovimentacaoAnalise extends PneuMovimentacao {
    private Recapadora recapadora;
    private String codColeta;

    public PneuMovimentacaoAnalise() {
        super(TipoPneuMovimentacao.PNEU_ANALISE);
    }

    public PneuMovimentacaoAnalise(final Recapadora recapadora, final String codColeta) {
        super(TipoPneuMovimentacao.PNEU_ANALISE);
        this.recapadora = recapadora;
        this.codColeta = codColeta;
    }

    public Recapadora getRecapadora() {
        return recapadora;
    }

    public void setRecapadora(final Recapadora recapadora) {
        this.recapadora = recapadora;
    }

    public String getCodColeta() {
        return codColeta;
    }

    public void setCodColeta(final String codColeta) {
        this.codColeta = codColeta;
    }

    @Override
    public String toString() {
        return "PneuMovimentacaoAnalise{" +
                "recapadora=" + recapadora +
                ", codColeta='" + codColeta + '\'' +
                '}';
    }
}
