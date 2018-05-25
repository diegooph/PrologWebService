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
    private String codColeta;

    public DestinoAnalise() {
        super(OrigemDestinoConstants.ANALISE);
    }

    public DestinoAnalise(@Nullable final Recapadora recapadoraDestino, @Nullable final String codColeta) {
        super(OrigemDestinoConstants.ANALISE);
        this.recapadoraDestino = recapadoraDestino;
        this.codColeta = codColeta;
    }

    @Nullable
    public Recapadora getRecapadoraDestino() {
        return recapadoraDestino;
    }

    public void setRecapadoraDestino(@Nullable final Recapadora recapadoraDestino) {
        this.recapadoraDestino = recapadoraDestino;
    }

    @Nullable
    public String getCodColeta() {
        return codColeta;
    }

    public void setCodColeta(@Nullable final String codColeta) {
        this.codColeta = codColeta;
    }

    @Override
    public String toString() {
        return "DestinoAnalise{" +
                "recapadoraDestino=" + recapadoraDestino +
                ", codColeta='" + codColeta + '\'' +
                '}';
    }
}
