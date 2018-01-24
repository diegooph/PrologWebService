package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoConstants;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.MotivoDescarte;

import javax.validation.constraints.NotNull;

/**
 * Created by Zart on 02/03/17.
 */
public final class DestinoDescarte extends Destino {

    @NotNull
    private MotivoDescarte motivoDescarte;

    public DestinoDescarte() {
        super(OrigemDestinoConstants.DESCARTE);
    }

    public DestinoDescarte(@NotNull MotivoDescarte motivoDescarte) {
        super(OrigemDestinoConstants.DESCARTE);
        this.motivoDescarte = motivoDescarte;
    }

    @NotNull
    public MotivoDescarte getMotivoDescarte() {
        return motivoDescarte;
    }

    public void setMotivoDescarte(@NotNull final MotivoDescarte motivoDescarte) {
        this.motivoDescarte = motivoDescarte;
    }
}
