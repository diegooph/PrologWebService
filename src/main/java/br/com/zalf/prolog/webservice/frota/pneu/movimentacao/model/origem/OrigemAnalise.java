package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model.PneuServicoRealizado;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Zart on 02/03/17.
 */
public final class OrigemAnalise extends Origem {

    @NotNull
    private List<PneuServicoRealizado> servicosRealizados;

    public OrigemAnalise(@NotNull final List<PneuServicoRealizado> servicosRealizados) {
        super(OrigemDestinoEnum.ANALISE);
        this.servicosRealizados = servicosRealizados;
    }

    @NotNull
    public List<PneuServicoRealizado> getServicosRealizados() {
        return servicosRealizados;
    }

    public void setServicosRealizados(@NotNull final List<PneuServicoRealizado> servicosRealizados) {
        this.servicosRealizados = servicosRealizados;
    }

    @Override
    public String toString() {
        return "OrigemAnalise{" +
                "servicosRealizados=" + servicosRealizados +
                '}';
    }
}
