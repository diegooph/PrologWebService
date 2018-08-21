package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.integracao.router.RouterAfericao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe AfericaoService responsavel por comunicar-se com a interface DAO
 */
public class AfericaoService {
    private static final String TAG = AfericaoService.class.getSimpleName();
    @NotNull
    private final AfericaoDao dao = Injection.provideAfericaoDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    public Long insert(@NotNull final Afericao afericao,
                       @NotNull final Long codUnidade,
                       @NotNull final String userToken) throws ProLogException {
        try {
            afericao.setDataHora(LocalDateTime.now(Clock.systemUTC()));
            return RouterAfericao
                    .create(dao, userToken)
                    .insertAfericao(afericao, codUnidade);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao inserir aferição", e);
            throw exceptionHandler.map(e, "Erro ao inserir aferição, tente novamente");
        }
    }

    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placa,
                                                  @NotNull final String tipoAfericao,
                                                  @NotNull final String userToken) throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getNovaAfericaoPlaca(codUnidade, placa, tipoAfericao);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar NovaAfericao para a placa: " + placa, e);
            throw exceptionHandler.map(e, "Erro ao inicar uma nova aferição, tente novamente");
        }
    }

    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(@NotNull final Long codUnidade,
                                                    @NotNull final Long codPneu,
                                                    @NotNull final String tipoAfericao) throws ProLogException {
        try {
            return dao.getNovaAfericaoAvulsa(codUnidade, codPneu, TipoMedicaoColetadaAfericao.fromString(tipoAfericao));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar NovaAfericao para o pneu: " + codPneu, e);
            throw exceptionHandler.map(e, "Erro ao inicar uma nova aferição, tente novamente");
        }
    }

    public Afericao getByCod(Long codUnidade, Long codAfericao, String userToken) throws ProLogException {
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
    public CronogramaAfericao getCronogramaAfericao(final Long codUnidade, final String userToken) throws
            ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getCronogramaAfericao(codUnidade);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar o cronograma de aferições";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final String userToken,
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

    public List<AfericaoPlaca> getAfericoesPlacas(Long codUnidade,
                                                  String codTipoVeiculo,
                                                  String placaVeiculo,
                                                  String dataInicial,
                                                  String dataFinal,
                                                  int limit,
                                                  long offset,
                                                  final String userToken) throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getAfericoesPlacas(
                            codUnidade,
                            codTipoVeiculo,
                            placaVeiculo,
                            ProLogDateParser.toLocalDate(dataInicial),
                            ProLogDateParser.toLocalDate(dataFinal),
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal),
                    limit,
                    offset);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar aferições avulsas", e);
            throw exceptionHandler.map(e, "Erro ao buscar as aferições, tente novamente");
        }
    }

    @NotNull
    public Report getAfericoesAvulsas(@NotNull final String userToken,
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
                            ProLogDateParser.toLocalDate(dataInicial),
                            ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de aferições avulsas por colaborador (REPORT)", throwable);
            throw exceptionHandler.map(
                    throwable,
                    "Erro ao gerar relatório das aferições avulsas, tente novamente");
        }
    }

    @NotNull
    public Restricao getRestricaoByCodUnidade(Long codUnidade) throws ProLogException {
        try {
            return dao.getRestricaoByCodUnidade(codUnidade);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar as restrições";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }
}