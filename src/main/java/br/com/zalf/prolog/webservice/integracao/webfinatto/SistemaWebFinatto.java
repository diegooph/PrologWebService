package br.com.zalf.prolog.webservice.integracao.webfinatto;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.webfinatto.data.SistemaWebFinattoRequester;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SistemaWebFinatto extends Sistema {
    @NotNull
    private static final String TAG = SistemaWebFinatto.class.getSimpleName();
    @NotNull
    private final SistemaWebFinattoRequester requester;
    @NotNull
    private final IntegracaoDao integracaoDao;

    public SistemaWebFinatto(@NotNull final SistemaWebFinattoRequester requester,
                             @NotNull final SistemaKey sistemaKey,
                             @NotNull final RecursoIntegrado recursoIntegrado,
                             @NotNull final IntegradorProLog integradorProLog,
                             @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, recursoIntegrado, userToken);
        this.integracaoDao = Injection.provideIntegracaoDao();
        this.requester = requester;
    }

    @Override
    @NotNull
    public CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable {
        return super.getCronogramaAfericao(codUnidades);
    }

    @Override
    @NotNull
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final String tipoAfericao) throws Throwable {
        return super.getNovaAfericaoPlaca(codUnidade, placaVeiculo, tipoAfericao);
    }

    @Override
    @NotNull
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        return super.getPneusAfericaoAvulsa(codUnidade);
    }

    @Override
    @NotNull
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable {
        return super.getNovaAfericaoAvulsa(codUnidade, codPneu, tipoMedicaoColetadaAfericao);
    }

    @Override
    @NotNull
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        return super.insertAfericao(codUnidade, afericao, deveAbrirServico);
    }
}
