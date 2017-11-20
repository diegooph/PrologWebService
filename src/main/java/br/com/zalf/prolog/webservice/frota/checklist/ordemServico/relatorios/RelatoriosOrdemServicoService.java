package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Created by luiz on 26/04/17.
 */
public class RelatoriosOrdemServicoService {

    private RelatoriosOrdemServicoDao dao = new RelatoriosOrdemServicoDaoImpl();
    private static final String TAG = RelatoriosOrdemServicoService.class.getSimpleName();

    void getItensMaiorQuantidadeNokCsv(@NotNull OutputStream outputStream,
                                       @NotNull Long codUnidade,
                                       @NotNull long dataInicial,
                                       @NotNull long dataFinal) {
        try {
            dao.getItensMaiorQuantidadeNokCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os itens com maior quantidade de apontamentos nok (CSV). \n" +
                    "Unidade: %d \n" +
                    "Período %d a %d", codUnidade, dataInicial, dataFinal), e);
        }
    }

    @NotNull
    Report getItensMaiorQuantidadeNokReport(@NotNull Long codUnidade,
                                            @NotNull long dataInicial,
                                            @NotNull long dataFinal) {
        try {
            return dao.getItensMaiorQuantidadeNokReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os itens com maior quantidade de apontamentos nok (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Período: %d a %d", codUnidade, dataInicial, dataFinal), e);
            return null;
        }
    }

    void getMediaTempoConsertoItemCsv(@NotNull OutputStream outputStream,
                                      @NotNull Long codUnidade,
                                      @NotNull long dataInicial,
                                      @NotNull long dataFinal) {
        try {
            dao.getMediaTempoConsertoItemCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar a média de tempo de conserto dos itens(CSV). \n" +
                    "Unidade: %d \n" +
                    "Período %d a %d", codUnidade, dataInicial, dataFinal), e);
        }
    }

    @NotNull
    Report getMediaTempoConsertoItemReport(@NotNull Long codUnidade,
                                           @NotNull long dataInicial,
                                           @NotNull long dataFinal) {
        try {
            return dao.getMediaTempoConsertoItemReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a média de tempo de conserto dos itens(CSV). \n" +
                    "Unidade: %d \n" +
                    "Período %d a %d", codUnidade, dataInicial, dataFinal), e);
            return null;
        }
    }

    void getProdutividadeMecanicosCsv(@NotNull OutputStream outputStream,
                                      @NotNull Long codUnidade,
                                      @NotNull long dataInicial,
                                      @NotNull long dataFinal) {
        try {
            dao.getProdutividadeMecanicosCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao o relatório com a produtividade dos mecânicos(CSV). \n" +
                    "Unidade: %d \n" +
                    "Período %d a %d", codUnidade, dataInicial, dataFinal), e);
        }
    }

    @NotNull
    Report getProdutividadeMecanicosReport(@NotNull Long codUnidade,
                                           @NotNull long dataInicial,
                                           @NotNull long dataFinal) {
        try {
            return dao.getProdutividadeMecanicosReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao o relatório com a produtividade dos mecânicos(REPORT). \n" +
                    "Unidade: %d \n" +
                    "Período %d a %d", codUnidade, dataInicial, dataFinal), e);
            return null;
        }
    }

    @NotNull
    public Report getEstratificacaoOsReport(@NotNull Long codUnidade, @NotNull String placa,
                                            @NotNull Long dataInicial, @NotNull Long dataFinal, @NotNull String statusOs,
                                            @NotNull String statusItem) {
        try {
            return dao.getEstratificacaoOsReport(codUnidade, placa, new Date(dataInicial), new Date(dataFinal), statusOs, statusItem);
        }catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a estratificação das OS(REPORT). \n" +
                    "Unidade: %d \n" +
                    "Placa: %s \n" +
                    "statusOs: %s \n" +
                    "statusItem: %s \n" +
                    "Período %d a %d", codUnidade, placa, statusOs, statusItem, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getEstratificacaoOsCsv(@NotNull OutputStream outputStream, @NotNull Long codUnidade, @NotNull String placa,
                                       @NotNull Long dataInicial, @NotNull Long dataFinal, @NotNull String statusOs,
                                       @NotNull String statusItem) {
        try {
            dao.getEstratificacaoOsCsv(outputStream, codUnidade, placa, new Date(dataInicial), new Date(dataFinal), statusOs, statusItem);
        }catch (SQLException | IOException e){
            Log.e(TAG, String.format("Erro ao buscar a estratificação das OS(CSV). \n " +
                    "Unidade: %d \n" +
                    "Placa: %s \n" +
                    "statusOs: %s \n" +
                    "statusItem %s \n" +
                    "Período %d a %d", codUnidade, placa, statusOs, statusItem, dataInicial, dataFinal), e);
        }
    }
}