package br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.PrologDateParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public final class ServicoRelatorioService {
    private static final String TAG = ServicoRelatorioService.class.getSimpleName();
    private final ServicoRelatorioDao dao = Injection.provideServicoRelatorioDao();

    public Report getEstratificacaoServicosFechadosReport(@NotNull final List<Long> codUnidades,
                                                          @NotNull final String dataInicial,
                                                          @NotNull final String dataFinal) {
        try {
            return dao.getEstratificacaoServicosFechadosReport(
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços fechados (REPORT). \n" +
                    "Unidades: %s \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidades, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getEstratificacaoServicosFechadosCsv(@NotNull final OutputStream outputStream,
                                                     @NotNull final List<Long> codUnidades,
                                                     @NotNull final String dataInicial,
                                                     @NotNull final String dataFinal) throws RuntimeException {
        try {
            dao.getEstratificacaoServicosFechadosCsv(
                    outputStream,
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços fechados (CSV). \n" +
                    "Unidades: %s \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidades, dataInicial, dataFinal), e);
            throw new RuntimeException();
        }
    }

    public Report getEstratificacaoServicosAbertosReport(@NotNull final List<Long> codUnidades,
                                                         @NotNull final String dataInicial,
                                                         @NotNull final String dataFinal) {
        try {
            return dao.getEstratificacaoServicosAbertosReport(
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços abertos (REPORT). \n" +
                    "Unidades: %s \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidades, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getEstratificacaoServicosAbertosCsv(@NotNull final OutputStream outputStream,
                                                    @NotNull final List<Long> codUnidades,
                                                    @NotNull final String dataInicial,
                                                    @NotNull final String dataFinal) throws RuntimeException {
        try {
            dao.getEstratificacaoServicosAbertosCsv(
                    outputStream,
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços abertos (CSV). \n" +
                    "Unidades: %s \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidades, dataInicial, dataFinal), e);
            throw new RuntimeException();
        }
    }
}