package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericaoPlaca;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ProtheusRodalog extends Sistema {
    @NotNull
    private final ProtheusRodalogRequesterImpl requester;

    public ProtheusRodalog(@NotNull final ProtheusRodalogRequesterImpl requester,
                           @NotNull final SistemaKey sistemaKey,
                           @NotNull final IntegradorProLog integradorProLog,
                           @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @Override
    public Long insertAfericao(@NotNull final Long codUnidade, @NotNull final Afericao afericao) throws Throwable {
        return requester.insert(codUnidade, ProtheusRodalogConverter.convertAfericao(afericao));
    }

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable {
        return ProtheusRodalogConverter.convertCronogramaAfericao(requester.getCronogramaAfericao(codUnidade));
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final String tipoAfericao) throws Throwable {
        return ProtheusRodalogConverter.convertNovaAfericaoPlaca(
                requester.getNovaAfericaoPlaca(codUnidade, placaVeiculo, tipoAfericao));
    }
}
