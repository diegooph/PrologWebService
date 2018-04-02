package br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public final class ServicoRelatorioService {
    private static final String TAG =  ServicoRelatorioService.class.getSimpleName();
    private final ServicoRelatorioDao dao = Injection.provideServicoRelatorioDao();

    public Report getEstratificacaoServicosFechadosReport(@NotNull final Long codUnidade,
                                                          @NotNull final String dataInicial,
                                                          @NotNull final String dataFinal) {
        try {
            return dao.getEstratificacaoServicosFechadosReport(
                    codUnidade,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços fechados (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidade, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getEstratificacaoServicosFechadosCsv(@NotNull final OutputStream outputStream,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final String dataInicial,
                                                     @NotNull final String dataFinal) throws RuntimeException {
        try {
            dao.getEstratificacaoServicosFechadosCsv(
                    outputStream,
                    codUnidade,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços fechados (CSV). \n" +
                    "Unidade: %d \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidade, dataInicial, dataFinal), e);
            throw new RuntimeException();
        }
    }

    public Report getEstratificacaoServicosAbertosReport(@NotNull final Long codUnidade,
                                                          @NotNull final String dataInicial,
                                                          @NotNull final String dataFinal) {
        try {
            return dao.getEstratificacaoServicosFechadosReport(
                    codUnidade,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços abertos (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidade, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getEstratificacaoServicosAbertosCsv(@NotNull final OutputStream outputStream,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final String dataInicial,
                                                     @NotNull final String dataFinal) throws RuntimeException {
        try {
            dao.getEstratificacaoServicosFechadosCsv(
                    outputStream,
                    codUnidade,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços abertos (CSV). \n" +
                    "Unidade: %d \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidade, dataInicial, dataFinal), e);
            throw new RuntimeException();
        }
    }
}