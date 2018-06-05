package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.model.ServicoRealizadoRecapadora;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Zart on 02/03/17.
 */
public final class OrigemAnalise extends Origem {

    @Nullable
    private List<ServicoRealizadoRecapadora> servicosRealizados;

    public OrigemAnalise() {
        super(OrigemDestinoEnum.ANALISE);
    }

    @Nullable
    public List<ServicoRealizadoRecapadora> getServicosRealizados() {
        return servicosRealizados;
    }

    public void setServicosRealizados(@Nullable final List<ServicoRealizadoRecapadora> servicosRealizados) {
        this.servicosRealizados = servicosRealizados;
    }

    @Override
    public String toString() {
        return "OrigemAnalise{" +
                "servicosRealizados=" + servicosRealizados +
                '}';
    }
}
