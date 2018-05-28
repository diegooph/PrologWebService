package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;

/**
 * Created on 25/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuAnalise extends Pneu {
    private Recapadora recapadora;
    private String codigoColeta;

    public PneuAnalise() {
        super(StatusPneu.ANALISE);
    }

    public PneuAnalise(final Recapadora recapadora, final String codigoColeta) {
        super(StatusPneu.ANALISE);
        this.recapadora = recapadora;
        this.codigoColeta = codigoColeta;
    }

    public Recapadora getRecapadora() {
        return recapadora;
    }

    public void setRecapadora(final Recapadora recapadora) {
        this.recapadora = recapadora;
    }

    public String getCodigoColeta() {
        return codigoColeta;
    }

    public void setCodigoColeta(final String codigoColeta) {
        this.codigoColeta = codigoColeta;
    }

    @Override
    public String toString() {
        return "PneuAnalise{" +
                "recapadora=" + recapadora +
                ", codigoColeta='" + codigoColeta + '\'' +
                '}';
    }
}
