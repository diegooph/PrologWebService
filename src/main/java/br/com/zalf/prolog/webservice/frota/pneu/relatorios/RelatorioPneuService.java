package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Aderencia;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Faixa;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Classe responsável por comunicar-se com a interface DAO.
 */
public class RelatorioPneuService {
    private static final String TAG = RelatorioPneuService.class.getSimpleName();
    private final RelatorioPneuDao dao = Injection.provideRelatorioPneuDao();

    public List<Faixa> getQtdPneusByFaixaSulco(@NotNull final List<Long> codUnidades,
                                               @NotNull final List<String> status) {
        try {
            return dao.getQtdPneusByFaixaSulco(codUnidades, status);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório de faixas de sulco", e);
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) {
        try {
            return dao.getQtPneusByFaixaPressao(codUnidades, status);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório de faixas de pressão", e);
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) {
        try {
            return dao.getAderenciaByUnidade(ano, mes, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência por unidade. \n" +
                    "Unidade: %d \n" +
                    "Ano: %d \n" +
                    "Mês: %d", codUnidade, ano, mes), e);
            throw new RuntimeException(e);
        }
    }

    public void getPrevisaoTrocaCsv(@NotNull final OutputStream outputStream,
                                    @NotNull final List<Long> codUnidades,
                                    @NotNull final String dataInicial,
                                    @NotNull final String dataFinal) throws RuntimeException {
        try {
            dao.getPrevisaoTrocaCsv(
                    outputStream,
                    codUnidades,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca (CSV). \n" +
                    "Unidades: %s \n" +
                    "Período: %s a %s", codUnidades.toString(), dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public Report getPrevisaoTrocaReport(@NotNull final List<Long> codUnidades,
                                         @NotNull final String dataInicial,
                                         @NotNull final String dataFinal) {
        try {
            return dao.getPrevisaoTrocaReport(
                    codUnidades,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca (REPORT). \n" +
                    "Unidades: %s \n" +
                    "Período: %s a %s", codUnidades, dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public void getPrevisaoTrocaConsolidadoCsv(@NotNull final OutputStream outputStream,
                                               @NotNull final List<Long> codUnidades,
                                               @NotNull final String dataInicial,
                                               @NotNull final String dataFinal) throws RuntimeException {
        try {
            dao.getPrevisaoTrocaConsolidadoCsv(
                    outputStream,
                    codUnidades,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca consolidado (CSV). \n" +
                    "Unidades: %s \n" +
                    "Período: %s a %s", codUnidades.toString(), dataInicial, dataFinal), e);
            throw new RuntimeException();
        }
    }

    public Report getPrevisaoTrocaConsolidadoReport(@NotNull final List<Long> codUnidades,
                                                    @NotNull final String dataInicial,
                                                    @NotNull final String dataFinal) {
        try {
            return dao.getPrevisaoTrocaConsolidadoReport(
                    codUnidades,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca consolidado (REPORT). \n" +
                    "Unidades: %s \n" +
                    "Período: %s a %s", codUnidades, dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public void getAderenciaPlacasCsv(@NotNull final OutputStream outputStream,
                                      @NotNull final List<Long> codUnidades,
                                      @NotNull final String dataInicial,
                                      @NotNull final String dataFinal) throws RuntimeException {
        try {
            dao.getAderenciaPlacasCsv(
                    outputStream,
                    codUnidades,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência das placas (CSV). \n" +
                    "Unidades: %s \n" +
                    "Período: %s a %s", codUnidades, dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public Report getAderenciaPlacasReport(@NotNull final List<Long> codUnidades,
                                           @NotNull final String dataInicial,
                                           @NotNull final String dataFinal) {
        try {
            return dao.getAderenciaPlacasReport(
                    codUnidades,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência das placas (REPORT). \n" +
                    "Unidades: %s \n" +
                    "Período: %s a %s", codUnidades, dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public Report getPneusDescartadosReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final String dataInicial,
                                            @NotNull final String dataFinal) {
        try {
            return dao.getPneusDescartadosReport(
                    codUnidades,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de pneus descartados (REPORT). \n" +
                    "Unidades: %s \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidades, dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public void getPneusDescartadosCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final String dataInicial,
                                       @NotNull final String dataFinal) {
        try {
            dao.getPneusDescartadosCsv(
                    outputStream,
                    codUnidades,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de pneus descartados (CSV). \n" +
                    "Unidades: %s \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidades, dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public void getDadosUltimaAfericaoCsv(@NotNull final OutputStream outputStream,
                                          @NotNull final List<Long> codUnidades) throws RuntimeException {
        try {
            dao.getDadosUltimaAfericaoCsv(outputStream, codUnidades);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os dados da última aferição (CSV). \n" +
                    "Unidades: %s", codUnidades), e);
            throw new RuntimeException(e);
        }
    }

    public Report getDadosUltimaAfericaoReport(@NotNull final List<Long> codUnidades) {
        try {
            return dao.getDadosUltimaAfericaoReport(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os dados da última aferição (REPORT). \n" +
                    "Unidades: %s", codUnidades), e);
            throw new RuntimeException(e);
        }
    }

    public void getResumoGeralPneusCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @Nullable final String status) throws RuntimeException {
        try {
            dao.getResumoGeralPneusCsv(outputStream, codUnidades, status);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de resumo geral dos pneus (CSV). \n" +
                    "Unidades: %s\n" +
                    "Status: %s", codUnidades, status), e);
            throw new RuntimeException(e);
        }
    }

    public Report getResumoGeralPneusReport(@NotNull final List<Long> codUnidades, @Nullable final String status) {
        try {
            return dao.getResumoGeralPneusReport(codUnidades, status);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de resumo geral dos pneus (REPORT). \n" +
                    "Unidades: %s\n" +
                    "Status: %s", codUnidades, status), e);
            throw new RuntimeException(e);
        }
    }
}
