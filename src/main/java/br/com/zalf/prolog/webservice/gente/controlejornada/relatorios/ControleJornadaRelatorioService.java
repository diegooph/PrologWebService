package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornadaRelatorio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 28/08/2017.
 */
public class ControleJornadaRelatorioService {
    private static final String TAG = ControleJornadaRelatorioService.class.getSimpleName();
    @NotNull
    private final ControleJornadaRelatoriosDao dao = Injection.provideControleJornadaRelatoriosDao();

    public void getMarcacoesDiariasCsv(final OutputStream out, final Long codUnidade, final Long dataInicial, final Long dataFinal, final String cpf) {
        try {
            dao.getMarcacoesDiariasCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos realizados (CSV). \n" +
                    "codUnidade: %d \n" +
                    "cpf: %s \n" +
                    "dataInicial: %d \n" +
                    "dataFinal: %d", codUnidade, cpf, dataInicial, dataFinal), e);
        }
    }

    public Report getMarcacoesDiariasReport(final Long codUnidade, final Long dataInicial, final Long dataFinal, final String cpf) {
        try {
            return dao.getMarcacoesDiariasReport(codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos realizados (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "cpf: %s \n" +
                    "dataInicial: %d \n" +
                    "dataFinal: %d", codUnidade, cpf, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getIntervalosMapasCsv(final OutputStream out, final Long codUnidade, final Long dataInicial, final Long dataFinal) {
        try {
            dao.getIntervalosMapasCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos por mapas realizados (CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getIntervalosMapasReport(final Long codUnidade, final Long dataInicial, final Long dataFinal) {
        try {
            return dao.getIntervalosMapasReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos por mapas realizados (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getAderenciaIntervalosDiariaCsv(final OutputStream out, final Long codUnidade, final Long dataInicial, final Long dataFinal) {
        try {
            dao.getAderenciaIntervalosDiariaCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência diária(CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getAderenciaIntervalosDiariaReport(final Long codUnidade, final Long dataInicial, final Long dataFinal) {
        try {
            return dao.getAderenciaIntervalosDiariaReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência diária(REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getAderenciaMarcacoesColaboradoresCsv(final OutputStream out,
                                                      final Long codUnidade,
                                                      final Long cpf,
                                                      final String dataInicial,
                                                      final String dataFinal) {
        try {
            dao.getAderenciaMarcacoesColaboradoresCsv(
                    out,
                    codUnidade,
                    cpf,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência por colaborador(CSV). \n" +
                    "codUnidade: %d\n" +
                    "cpf: %d\n" +
                    "dataInicial: %s\n" +
                    "dataFinal: %s", codUnidade, cpf, dataInicial, dataFinal), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relatório, tente novamente");
        }
    }

    public Report getAderenciaMarcacoesColaboradoresReport(final Long codUnidade,
                                                           final Long cpf,
                                                           final String dataInicial,
                                                           final String dataFinal) {
        try {
            return dao.getAderenciaMarcacoesColaboradoresReport(
                    codUnidade,
                    cpf,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência por colaborador(REPORT).\n" +
                    "codUnidade: %d\n" +
                    "cpf: %d\n" +
                    "dataInicial: %s\n" +
                    "dataFinal: %s", codUnidade, cpf, dataInicial, dataFinal), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relatório, tente novamente");
        }
    }

    public void getIntervalosPadraoPortaria1510(@NotNull final OutputStream out,
                                                @NotNull final Long codUnidade,
                                                @NotNull final Long codTipoIntervalo,
                                                @NotNull final String cpf,
                                                @NotNull final String dataInicial,
                                                @NotNull final String dataFinal) {
        try {
            dao.getRelatorioPadraoPortaria1510Csv(
                    out,
                    codUnidade,
                    codTipoIntervalo,
                    cpf,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório csv no padrão da portaria 1510. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %d \n" +
                    "cpf: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, codTipoIntervalo, cpf, dataInicial, dataFinal), e);
        }
    }

    @NotNull
    public List<FolhaPontoRelatorio> getFolhaPontoRelatorio(@NotNull final Long codUnidade,
                                                            @NotNull final String codTipoIntervalo,
                                                            @NotNull final String cpf,
                                                            @NotNull final String dataInicial,
                                                            @NotNull final String dataFinal,
                                                            final boolean apenasColaboradoresAtivos) throws ProLogException {
        try {
            return dao.getFolhaPontoRelatorio(
                    codUnidade,
                    codTipoIntervalo,
                    cpf,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal),
                    apenasColaboradoresAtivos);
        } catch (final Throwable e) {
            final String errorMessage = String.format("Erro ao buscar o relatório de folha de ponto. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %s \n" +
                    "cpf: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, codTipoIntervalo, cpf, dataInicial, dataFinal);
            Log.e(TAG, errorMessage, e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relatório, tente novamente");
        }
    }

    @NotNull
    public List<FolhaPontoJornadaRelatorio> getFolhaPontoJornadaRelatorio(
            @NotNull final Long codUnidade,
            @NotNull final String codTipoIntervalo,
            @NotNull final String cpf,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal,
            final boolean apenasColaboradoresAtivos) throws ProLogException {
        try {
            return dao.getFolhaPontoJornadaRelatorio(
                    codUnidade,
                    codTipoIntervalo,
                    cpf,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal),
                    apenasColaboradoresAtivos);
        } catch (final Throwable e) {
            final String errorMessage = String.format("Erro ao buscar o relatório de folha de ponto jornada.\n" +
                    "codUnidade: %d\n" +
                    "codTipoIntervalo: %s\n" +
                    "cpf: %s\n" +
                    "dataInicial: %s\n" +
                    "dataFinal: %s", codUnidade, codTipoIntervalo, cpf, dataInicial, dataFinal);
            Log.e(TAG, errorMessage, e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relatório, tente novamente");
        }
    }

    @NotNull
    public Report getMarcacoesComparandoEscalaDiariaReport(@NotNull final Long codUnidade,
                                                           @NotNull final Long codTipoIntervalo,
                                                           @NotNull final String dataInicial,
                                                           @NotNull final String dataFinal) {
        try {
            return dao.getMarcacoesComparandoEscalaDiariaReport(
                    codUnidade,
                    codTipoIntervalo,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar report do relatório de marcações comparando com escala diária. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, codTipoIntervalo, dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public void getMarcacoesComparandoEscalaDiariaCsv(@NotNull final OutputStream out,
                                                      @NotNull final Long codUnidade,
                                                      @NotNull final Long codTipoIntervalo,
                                                      @NotNull final String dataInicial,
                                                      @NotNull final String dataFinal) {
        try {
            dao.getMarcacoesComparandoEscalaDiariaCsv(
                    out,
                    codUnidade,
                    codTipoIntervalo,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final IOException | SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar csv do relatório de marcações comparando com escala diária. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, codTipoIntervalo, dataInicial, dataFinal), e);
            throw new RuntimeException(e);
        }
    }

    public void getTotalTempoByTipoIntervaloCsv(@NotNull final OutputStream out,
                                                @NotNull final Long codUnidade,
                                                @NotNull final String codTipoIntervalo,
                                                @NotNull final String dataInicial,
                                                @NotNull final String dataFinal) {
        try {
            dao.getTotalTempoByTipoIntervaloCsv(
                    out,
                    codUnidade,
                    codTipoIntervalo,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar csv do relatório de total de tempo para cada tipo de intervalo. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, codTipoIntervalo, dataInicial, dataFinal), t);
            throw new RuntimeException(t);
        }
    }

    @NotNull
    public Report getTotalTempoByTipoIntervaloReport(@NotNull final Long codUnidade,
                                                     @NotNull final String codTipoIntervalo,
                                                     @NotNull final String dataInicial,
                                                     @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getTotalTempoByTipoIntervaloReport(
                    codUnidade,
                    codTipoIntervalo,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            final String errorMessage = String.format(
                    "Erro ao buscar report do relatório de total de tempo para cada tipo de intervalo. \n" +
                            "codUnidade: %d \n" +
                            "codTipoIntervalo: %s \n" +
                            "dataInicial: %s \n" +
                            "dataFinal: %s", codUnidade, codTipoIntervalo, dataInicial, dataFinal);
            Log.e(TAG, errorMessage, e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao gerar relatório, tente novamente");
        }
    }


    public void getMarcacoesExportacaoGenericaCsv(@NotNull final OutputStream out,
                                                  @NotNull final Long codUnidade,
                                                  @Nullable final Long codTipoIntervalo,
                                                  @Nullable final Long codColaborador,
                                                  final boolean apenasMarcacoesAtivas,
                                                  @NotNull final String dataInicial,
                                                  @NotNull final String dataFinal) {
        try {
            dao.getMarcacoesExportacaoGenericaCsv(
                    out,
                    codUnidade,
                    codTipoIntervalo,
                    codColaborador,
                    apenasMarcacoesAtivas,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar o relatório csv para exportação genérica de marcações.\n" +
                    "codUnidade: %d\n" +
                    "codTipoIntervalo: %d\n" +
                    "codColaborador: %d\n" +
                    "dataInicial: %s\n" +
                    "dataFinal: %s", codUnidade, codTipoIntervalo, codColaborador, dataInicial, dataFinal), t);
        }
    }
}