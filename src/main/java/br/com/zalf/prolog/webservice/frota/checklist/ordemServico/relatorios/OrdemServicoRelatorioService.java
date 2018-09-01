package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServico;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created by luiz on 26/04/17.
 */
class OrdemServicoRelatorioService {
    private static final String TAG = OrdemServicoRelatorioService.class.getSimpleName();
    @NotNull
    private OrdemServicoRelatorioDao dao = Injection.provideRelatoriosOrdemServicoDao();

    void getItensMaiorQuantidadeNokCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final String dataInicial,
                                       @NotNull final String dataFinal) {
        try {
            dao.getItensMaiorQuantidadeNokCsv(
                    outputStream, 
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial), 
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os itens com maior quantidade de apontamentos " +
                    "nok (CSV)\n" +
                    "Unidades: %s\n" +
                    "Data Inicial: %s\n" +
                    "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
        }
    }

    @NotNull
    Report getItensMaiorQuantidadeNokReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final String dataInicial,
                                            @NotNull final String dataFinal) {
        try {
            return dao.getItensMaiorQuantidadeNokReport(
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os itens com maior quantidade de apontamentos " +
                    "nok (REPORT)\n" +
                    "Unidades: %s\n" +
                    "Data Inicial: %s\n" +
                    "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
            return null;
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar a média de tempo de conserto dos itens(CSV)\n" +
                    "Unidades: %s\n" +
                    "Data Inicial: %s\n" +
                    "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
        }
    }

    @NotNull
    Report getMediaTempoConsertoItemReport(@NotNull final List<Long> codUnidades,
                                           @NotNull final String dataInicial,
                                           @NotNull final String dataFinal) {
        try {
            return dao.getMediaTempoConsertoItemReport(
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar a média de tempo de conserto dos itens (CSV)\n" +
                    "Unidades: %s\n" +
                    "Data Inicial: %s\n" +
                    "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
            return null;
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao o relatório com a produtividade dos mecânicos(CSV)\n" +
                    "Unidades: %s\n" +
                    "Data Inicial: %s\n" +
                    "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
        }
    }

    @NotNull
    Report getProdutividadeMecanicosReport(@NotNull final List<Long> codUnidades,
                                           @NotNull final String dataInicial,
                                           @NotNull final String dataFinal) {
        try {
            return dao.getProdutividadeMecanicosReport(
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao o relatório com a produtividade dos mecânicos(REPORT)\n" +
                    "Unidades: %s\n" +
                    "Data Inicial: %s\n" +
                    "Data Final: %s", codUnidades.toString(), dataInicial, dataFinal), e);
            return null;
        }
    }

    @NotNull
    public Report getEstratificacaoOsReport(@NotNull final List<Long> codUnidades, 
                                            @NotNull final String placa,
                                            @NotNull final String statusOs,
                                            @NotNull final String statusItemOs,
                                            @NotNull final String dataInicial,
                                            @NotNull final String dataFinal) {
        try {
            return dao.getEstratificacaoOsReport(
                    codUnidades,
                    placa,
                    OrdemServico.Status.fromString(statusOs),
                    ItemOrdemServico.Status.fromString(statusItemOs),
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar a estratificação das OS (REPORT)\n" +
                    "Unidades: %s\n" +
                    "Placa: %s\n" +
                    "statusOs: %s\n" +
                    "statusItemOs: %s\n" +
                    "Data Inicial: %s\n" +
                    "Data Final: %s", codUnidades.toString(), placa, statusOs, statusItemOs, dataInicial, dataFinal), e);
            return null;
        }
    }

    void getEstratificacaoOsCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades,
                                @NotNull final String placa,
                                @NotNull final String statusOs,
                                @NotNull final String statusItemOs,
                                @NotNull final String dataInicial,
                                @NotNull final String dataFinal) {
        try {
            dao.getEstratificacaoOsCsv(
                    outputStream,
                    codUnidades,
                    placa,
                    OrdemServico.Status.fromString(statusOs),
                    ItemOrdemServico.Status.fromString(statusItemOs),
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar a estratificação das OS (CSV)\n" +
                    "Unidades: %s\n" +
                    "Placa: %s\n" +
                    "statusOs: %s\n" +
                    "statusItemOs %s\n" +
                    "Data Inicial: %s\n" +
                    "Data Final: %s", codUnidades.toString(), placa, statusOs, statusItemOs, dataInicial, dataFinal), e);
        }
    }
}