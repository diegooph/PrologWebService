package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Zart on 02/03/17.
 */
public final class DestinoAnalise extends Destino {
    @NotNull
    private Recapadora recapadoraDestino;
    @Nullable
    private String codigoColeta;

    public DestinoAnalise() {
        super(OrigemDestinoEnum.ANALISE);
    }

    public DestinoAnalise(@NotNull final Recapadora recapadoraDestino, @Nullable final String codigoColeta) {
        super(OrigemDestinoEnum.ANALISE);
        this.recapadoraDestino = recapadoraDestino;
        this.codigoColeta = codigoColeta;
    }

    @NotNull
    public Recapadora getRecapadoraDestino() {
        return recapadoraDestino;
    }

    public void setRecapadoraDestino(@NotNull final Recapadora recapadoraDestino) {
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
