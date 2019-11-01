package br.com.zalf.prolog.webservice.frota.pneu._model;

import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;
import com.google.common.base.Preconditions;

/**
 * Created on 25/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PneuAnalise extends Pneu {
    private Recapadora recapadora;
    private String codigoColeta;

    public PneuAnalise() {
        super(PneuTipo.PNEU_ANALISE);
        setStatus(StatusPneu.ANALISE);
    }

    @Override
    public void setStatus(final StatusPneu status) {
        Preconditions.checkArgument(status == StatusPneu.ANALISE);
        super.setStatus(status);
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
