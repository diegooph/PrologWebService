package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.util.List;

/**
 * Created by luiz on 26/04/17.
 */
class OrdemServicoRelatorioService {
    private static final String TAG = OrdemServicoRelatorioService.class.getSimpleName();
    @NotNull
    private final OrdemServicoRelatorioDao dao = Injection.provideRelatoriosOrdemServicoDao();

    @NotNull
    public Report getEstratificacaoOsReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final String statusOs,
                                            @NotNull final String statusItemOs,
                                            @Nullable final String dataInicialAbertura,
                                            @Nullable final String dataFinalAbertura,
                                            @Nullable final String dataInicialResolucao,
                                            @Nullable final String dataFinalResolucao) {
        try {
            return dao.getEstratificacaoOsReport(
                    codUnidades,
                    statusOs,
                    statusItemOs,
                    StringUtils.isNullOrEmpty(dataInicialAbertura)
                            ? null
                            : PrologDateParser.toLocalDate(dataInicialAbertura),
                    StringUtils.isNullOrEmpty(dataFinalAbertura)
                            ? null
                            : PrologDateParser.toLocalDate(dataFinalAbertura),
                    StringUtils.isNullOrEmpty(dataInicialResolucao)
                            ? null
                            : PrologDateParser.toLocalDate(dataInicialResolucao),
                    StringUtils.isNullOrEmpty(dataFinalResolucao)
                            ? null
                            : PrologDateParser.toLocalDate(dataFinalResolucao));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar a estratifica????o das OS (REPORT)\n" +
                                             "Unidades: %s\n" +
                                             "statusOs: %s\n" +
                                             "statusItemOs: %s\n" +
                                             "Data Inicial Abertura: %s\n" +
                                             "Data Final Abertura: %s\n" +
                                             "Data Inicial Resolu????o: %s\n" +
                                             "Data Final Resolu????o: %s",
                                     codUnidades.toString(),
                                     statusOs,
                                     statusItemOs,
                                     dataInicialAbertura,
                                     dataFinalAbertura,
                                     dataInicialResolucao,
                                     dataFinalResolucao), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relat??rio, tente novamente");
        }
    }

    void getItensMaiorQuantidadeNokCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final String dataInicial,
                                       @NotNull final String dataFinal) {
        try {
            dao.getItensMaiorQuantidadeNokCsv(
                    outputStream,
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar o relat??rio com os itens com maior quantidade de apontamentos " +
                                             "nok (CSV)\n" +
                                             "Unidades: %s\n" +
                                             "Data Inicial: %s\n" +
                                             "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relat??rio, tente novamente");
        }
    }

    @NotNull
    Report getItensMaiorQuantidadeNokReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final String dataInicial,
                                            @NotNull final String dataFinal) {
        try {
            return dao.getItensMaiorQuantidadeNokReport(
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar o relat??rio com os itens com maior quantidade de apontamentos " +
                                             "nok (REPORT)\n" +
                                             "Unidades: %s\n" +
                                             "Data Inicial: %s\n" +
                                             "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relat??rio, tente novamente");
        }
    }

    void getMediaTempoConsertoItemCsv(@NotNull final OutputStream outputStream,
                                      @NotNull final List<Long> codUnidades,
                                      @NotNull final String dataInicial,
                                      @NotNull final String dataFinal) {
        try {
            dao.getMediaTempoConsertoItemCsv(
                    outputStream,
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar a m??dia de tempo de conserto dos itens(CSV)\n" +
                                             "Unidades: %s\n" +
                                             "Data Inicial: %s\n" +
                                             "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relat??rio, tente novamente");
        }
    }

    @NotNull
    Report getMediaTempoConsertoItemReport(@NotNull final List<Long> codUnidades,
                                           @NotNull final String dataInicial,
                                           @NotNull final String dataFinal) {
        try {
            return dao.getMediaTempoConsertoItemReport(
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar a m??dia de tempo de conserto dos itens (CSV)\n" +
                                             "Unidades: %s\n" +
                                             "Data Inicial: %s\n" +
                                             "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relat??rio, tente novamente");
        }
    }

    void getProdutividadeMecanicosCsv(@NotNull final OutputStream outputStream,
                                      @NotNull final List<Long> codUnidades,
                                      @NotNull final String dataInicial,
                                      @NotNull final String dataFinal) {
        try {
            dao.getProdutividadeMecanicosCsv(
                    outputStream,
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao o relat??rio com a produtividade dos mec??nicos(CSV)\n" +
                                             "Unidades: %s\n" +
                                             "Data Inicial: %s\n" +
                                             "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relat??rio, tente novamente");
        }
    }

    @NotNull
    Report getProdutividadeMecanicosReport(@NotNull final List<Long> codUnidades,
                                           @NotNull final String dataInicial,
                                           @NotNull final String dataFinal) {
        try {
            return dao.getProdutividadeMecanicosReport(
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao o relat??rio com a produtividade dos mec??nicos(REPORT)\n" +
                                             "Unidades: %s\n" +
                                             "Data Inicial: %s\n" +
                                             "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relat??rio, tente novamente");
        }
    }

    void getEstratificacaoOsCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades,
                                @NotNull final String statusOs,
                                @NotNull final String statusItemOs,
                                @Nullable final String dataInicialAbertura,
                                @Nullable final String dataFinalAbertura,
                                @Nullable final String dataInicialResolucao,
                                @Nullable final String dataFinalResolucao) {
        try {
            dao.getEstratificacaoOsCsv(
                    outputStream,
                    codUnidades,
                    statusOs,
                    statusItemOs,
                    StringUtils.isNullOrEmpty(dataInicialAbertura)
                            ? null
                            : PrologDateParser.toLocalDate(dataInicialAbertura),
                    StringUtils.isNullOrEmpty(dataFinalAbertura)
                            ? null
                            : PrologDateParser.toLocalDate(dataFinalAbertura),
                    StringUtils.isNullOrEmpty(dataInicialResolucao)
                            ? null
                            : PrologDateParser.toLocalDate(dataInicialResolucao),
                    StringUtils.isNullOrEmpty(dataFinalResolucao)
                            ? null
                            : PrologDateParser.toLocalDate(dataFinalResolucao));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar a estratifica????o das OS (CSV)\n" +
                                             "Unidades: %s\n" +
                                             "statusOs: %s\n" +
                                             "statusItemOs %s\n" +
                                             "Data Inicial Abertura: %s\n" +
                                             "Data Final Abertura: %s\n" +
                                             "Data Inicial Resolu????o: %s\n" +
                                             "Data Final Resolu????o: %s",
                                     codUnidades.toString(),
                                     statusOs,
                                     statusItemOs,
                                     dataInicialAbertura,
                                     dataFinalAbertura,
                                     dataInicialResolucao,
                                     dataFinalResolucao), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relat??rio, tente novamente");
        }
    }
}