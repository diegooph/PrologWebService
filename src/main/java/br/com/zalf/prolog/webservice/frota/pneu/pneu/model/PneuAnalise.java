package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;

/**
 * Created on 25/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuAnalise extends Pneu {
    private Recapadora recapadora;
    private String codColeta;

    public PneuAnalise() {
        super(StatusPneu.ANALISE);
    }

    public PneuAnalise(final Recapadora recapadora, final String codColeta) {
        super(StatusPneu.ANALISE);
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
        return "PneuAnalise{" +
                "recapadora=" + recapadora +
                ", codColeta='" + codColeta + '\'' +
                '}';
    }
}
