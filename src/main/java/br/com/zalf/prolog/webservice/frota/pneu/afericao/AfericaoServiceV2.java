package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.newimpl.AfericaoIntegrada;
import br.com.zalf.prolog.webservice.integracao.newimpl.Integrado;
import br.com.zalf.prolog.webservice.integracao.router.RouterAfericao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Classe AfericaoService responsavel por comunicar-se com a interface DAO
 */
@Service
public class AfericaoServiceV2 implements AfericaoIntegrada {
    private static final String TAG = AfericaoServiceV2.class.getSimpleName();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();
    @NotNull
    private final AfericaoDaoV2 dao;

    @Autowired
    public AfericaoServiceV2(@NotNull final AfericaoDaoV2 dao) {
        this.dao = dao;
    }

    @NotNull
    @Override
    @Integrado(recursoIntegrado = RecursoIntegrado.AFERICAO)
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws ProLogException {
        try {
            return dao.insert(codUnidade, afericao, deveAbrirServico);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao inserir aferição", e);
            throw exceptionHandler.map(e, "Erro ao inserir aferição, tente novamente");
        }
    }

    @NotNull
    public CronogramaAfericao getCronogramaAfericao(@NotNull final String userToken,
                                                    @NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getCronogramaAfericao(codUnidades);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar cronograma de aferições\n" +
                    "userToken: " + userToken + "\n" +
                    "codUnidades: " + codUnidades.toString(), t);
            throw exceptionHandler.map(t, "Erro ao buscar cronograma de aferições, tente novamente");
        }
    }

    @NotNull
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final AfericaoBuscaFiltro afericaoBusca,
                                                  @NotNull final String userToken)
            throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getNovaAfericaoPlaca(afericaoBusca);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar NovaAfericao para a placa: " + afericaoBusca.getPlacaVeiculo(), e);
            throw exceptionHandler.map(e, "Erro ao iniciar uma nova aferição, tente novamente");
        }
    }

    @NotNull
    List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final String userToken,
                                                    @NotNull final Long codUnidade) throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getPneusAfericaoAvulsa(codUnidade);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar os pneus disponíveis para aferição avulsa";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    NovaAfericaoAvulsa getNovaAfericaoAvulsa(@NotNull final String userToken,
                                             @NotNull final Long codUnidade,
                                             @NotNull final Long codPneu,
                                             @NotNull final String tipoAfericao) throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getNovaAfericaoAvulsa(codUnidade, codPneu, TipoMedicaoColetadaAfericao.fromString(tipoAfericao));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar NovaAfericao para o pneu: " + codPneu, e);
            throw exceptionHandler.map(e, "Erro ao iniciar uma nova aferição, tente novamente");
        }
    }

    @NotNull
    List<AfericaoPlaca> getAfericoesPlacas(@NotNull final Long codUnidade,
                                           @NotNull final String codTipoVeiculo,
                                           @NotNull final String placaVeiculo,
                                           @NotNull final String dataInicial,
                                           @NotNull final String dataFinal,
                                           final int limit,
                                           final long offset,
                                           final String userToken) throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getAfericoesPlacas(
                            codUnidade,
                            codTipoVeiculo,
                            placaVeiculo,
                            PrologDateParser.toLocalDate(dataInicial),
                            PrologDateParser.toLocalDate(dataFinal),
                            limit,
                            offset);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar aferições. Unidade: "
                    + codUnidade + " || Tipo: "
                    + codTipoVeiculo + " || Placa: "
                    + placaVeiculo, e);
            throw exceptionHandler.map(e, "Erro ao buscar as aferições, tente novamente");
        }
    }

    @NotNull
    List<AfericaoAvulsa> getAfericoesAvulsas(@NotNull final Long codUnidade,
                                             @NotNull final String dataInicial,
                                             @NotNull final String dataFinal,
                                             final int limit,
                                             final long offset) throws ProLogException {
        try {
            return dao.getAfericoesAvulsas(
                    codUnidade,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal),
                    limit,
                    offset);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar aferições avulsas", e);
            throw exceptionHandler.map(e, "Erro ao buscar as aferições, tente novamente");
        }
    }

    @NotNull
    Report getAfericoesAvulsas(@NotNull final String userToken,
                               @NotNull final Long codUnidade,
                               @Nullable final Long codColaborador,
                               @NotNull final String dataInicial,
                               @NotNull final String dataFinal) throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getAfericoesAvulsas(
                            codUnidade,
                            codColaborador,
                            PrologDateParser.toLocalDate(dataInicial),
                            PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de aferições avulsas por colaborador (REPORT)", throwable);
            throw exceptionHandler.map(
                    throwable,
                    "Erro ao gerar relatório das aferições avulsas, tente novamente");
        }
    }

    @NotNull
    Afericao getByCod(@NotNull final Long codUnidade,
                      @NotNull final Long codAfericao,
                      @NotNull final String userToken) throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getAfericaoByCodigo(codUnidade, codAfericao);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar a aferição: " + codAfericao, e);
            throw exceptionHandler.map(e, "Erro ao buscar a aferição, tente novamente");
        }
    }

    @NotNull
    Restricao getRestricaoByCodUnidade(final Long codUnidade) throws ProLogException {
        try {
            return dao.getRestricaoByCodUnidade(codUnidade);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar as restrições";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }
}