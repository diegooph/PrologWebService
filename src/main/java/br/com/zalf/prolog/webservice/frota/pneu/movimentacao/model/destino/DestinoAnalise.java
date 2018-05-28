package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoConstants;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Zart on 02/03/17.
 */
public final class DestinoAnalise extends Destino {

    @Nullable
    private Recapadora recapadoraDestino;
    @Nullable
    private String codigoColeta;

    public DestinoAnalise() {
        super(OrigemDestinoConstants.ANALISE);
    }

    public DestinoAnalise(@Nullable final Recapadora recapadoraDestino, @Nullable final String codigoColeta) {
        super(OrigemDestinoConstants.ANALISE);
        this.recapadoraDestino = recapadoraDestino;
        this.codigoColeta = codigoColeta;
    }

    @Nullable
    public Recapadora getRecapadoraDestino() {
        return recapadoraDestino;
    }

    public void setRecapadoraDestino(@Nullable final Recapadora recapadoraDestino) {
        this.recapadoraDestino = recapadoraDestino;
    }

    @Nullable
    public String getCodigoColeta() {
        return codigoColeta;
    }

    public void setCodigoColeta(@Nullable final String codigoColeta) {
        this.codigoColeta = codigoColeta;
    }

    @Override
    public String toString() {
        return "DestinoAnalise{" +
                "recapadoraDestino=" + recapadoraDestino +
                ", codigoColeta='" + codigoColeta + '\'' +
                '}';
    }
}
