package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.Aderencia;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.Faixa;
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
    @NotNull
    private final RelatorioPneuDao dao = Injection.provideRelatorioPneuDao();

    public void getFarolAfericaoCsv(@NotNull final OutputStream outputStream,
                                    @NotNull final List<Long> codUnidades,
                                    @NotNull final String dataInicial,
                                    @NotNull final String dataFinal) {
        try {
            dao.getFarolAfericaoCsv(
                    outputStream,
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de aferições avulsas (CSV).\n" +
                    "Unidades: " + codUnidades.toString() + "\n" +
                    "Data inicial: " + dataInicial + "\n" +
                    "Data final: " + dataFinal + "\n", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable,
                            "Erro ao gerar relatório, tente novamente");
        }
    }

    public void getPneusComDesgasteIrregularCsv(final OutputStream outputStream,
                                                final List<Long> codUnidades,
                                                final String statusPneu) {
        try {
            dao.getPneusComDesgasteIrregularCsv(
                    outputStream,
                    codUnidades,
                    statusPneu != null ? StatusPneu.fromString(statusPneu) : null);
        } catch (final Throwable throwable) {
            Log.e(TAG,
                    String.format("Erro ao buscar o relatório de desgaste irregular dos pneus (CSV).\n" +
                            "Unidades: %s", codUnidades.toString()),
                    throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getPneusComDesgasteIrregularReport(final List<Long> codUnidades,
                                                     final String statusPneu) throws ProLogException {
        try {
            return dao.getPneusComDesgasteIrregularReport(
                    codUnidades,
                    statusPneu != null ? StatusPneu.fromString(statusPneu) : null);
        } catch (final Throwable throwable) {
            Log.e(TAG,
                    String.format("Erro ao buscar o relatório de desgaste irregular dos pneus (REPORT).\n" +
                            "Unidades: %s", codUnidades.toString()),
                    throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable,
                            "Erro ao gerar relatório, tente novamente");
        }
    }

    public void getStatusAtualPneusCsv(final OutputStream outputStream,
                                       final List<Long> codUnidades) {
        try {
            dao.getStatusAtualPneusCsv(outputStream, codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de status atual dos pneus (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getStatusAtualPneusReport(final List<Long> codUnidades) throws ProLogException {
        try {
            return dao.getStatusAtualPneusReport(codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de status atual dos pneus (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable,
                            "Erro ao gerar relatório, tente novamente");
        }
    }

    public void getKmRodadoPorPneuPorVidaCsv(@NotNull final OutputStream outputStream,
                                             @NotNull final List<Long> codUnidades) {
        try {
            dao.getKmRodadoPorPneuPorVidaCsv(outputStream, codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de km percorrido por pneu por vida (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getKmRodadoPorPneuPorVidaReport(@NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return dao.getKmRodadoPorPneuPorVidaReport(codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de km percorrido por pneu por vida (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable,
                            "Erro ao gerar relatório, tente novamente");
        }
    }

    public void getAfericoesAvulsasCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final String dataInicial,
                                       @NotNull final String dataFinal) {
        try {
            dao.getAfericoesAvulsasCsv(
                    outputStream,
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de aferições avulsas (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getAfericoesAvulsasReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final String dataInicial,
                                            @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getAfericoesAvulsasReport(
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de aferições avulsas (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable,
                            "Erro ao gerar relatório das aferições avulsas, tente novamente");
        }
    }

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
        } catch (Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório de faixas de pressão", e);
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) {
        try {
            return dao.getAderenciaByUnidade(ano, mes, codUnidade);
        } catch (Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência por unidade. \n" +
                    "Unidade: %d \n" +
                    "Ano: %d \n" +
                    "Mês: %d", codUnidade, ano, mes), e);
            throw new RuntimeException(e);
        }
    }

    public void getPrevisaoTrocaEstratificadoCsv(@NotNull final OutputStream outputStream,
                                                 @NotNull final List<Long> codUnidades,
                                                 @NotNull final String dataInicial,
                                                 @NotNull final String dataFinal) throws RuntimeException {
        try {
            dao.getPrevisaoTrocaEstratificadoCsv(
                    outputStream,
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca (CSV). \n" +
                    "Unidades: %s \n" +
                    "Período: %s a %s", codUnidades.toString(), dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public Report getPrevisaoTrocaEstratificadoReport(@NotNull final List<Long> codUnidades,
                                                      @NotNull final String dataInicial,
                                                      @NotNull final String dataFinal) {
        try {
            return dao.getPrevisaoTrocaEstratificadoReport(
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
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

    public Report getVencimentoDotReport(@NotNull final List<Long> codUnidades,
                                         @NotNull final String userToken) throws ProLogException {
        try {
            return dao.getVencimentoDotReport(codUnidades, userToken);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de vencimento de DOT (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }

    public void getVencimentoDotCsv(@NotNull final OutputStream out,
                                    @NotNull final List<Long> codUnidades,
                                    @NotNull final String userToken) {
        try {
            dao.getVencimentoDotCsv(out, codUnidades, userToken);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de vencimento de DOT (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    public void getCustoPorKmCsv(@NotNull final OutputStream outputStream,
                                 @NotNull final List<Long> codUnidades) {
        try {
            dao.getCustoPorKmCsv(outputStream, codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o relatório de custo por KM (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }
}
