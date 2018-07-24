package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.error.ColaboradorExceptionHandler;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 28/08/2017.
 */
public class ControleIntervaloRelatorioService {

    private static final String TAG = ControleIntervaloRelatorioService.class.getSimpleName();
    private ControleIntervaloRelatoriosDao dao = Injection.provideControleIntervaloRelatoriosDao();
    private final ColaboradorExceptionHandler exceptionHandler = Injection.provideColaboradorExceptionHandler();

    public void getIntervalosCsv(OutputStream out, Long codUnidade, Long dataInicial, Long dataFinal, String cpf) {
        try {
            dao.getIntervalosCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos realizados (CSV). \n" +
                    "codUnidade: %d \n" +
                    "cpf: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, cpf, dataInicial, dataFinal), e);
        }
    }

    public Report getIntervalosReport(Long codUnidade, Long dataInicial, Long dataFinal, String cpf) {
        try {
            return dao.getIntervalosReport(codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos realizados (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "cpf: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, cpf, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getIntervalosMapasCsv(OutputStream out, Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            dao.getIntervalosMapasCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos por mapas realizados (CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getIntervalosMapasReport(Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            return dao.getIntervalosMapasReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos por mapas realizados (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getAderenciaIntervalosDiariaCsv(OutputStream out, Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            dao.getAderenciaIntervalosDiariaCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência diária(CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getAderenciaIntervalosDiariaReport(Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            return dao.getAderenciaIntervalosDiariaReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência diária(REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getAderenciaIntervalosColaboradorCsv(OutputStream out, Long codUnidade, Long dataInicial, Long
            dataFinal, String cpf) {
        try {
            dao.getAderenciaIntervalosColaboradorCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência por colaborador(CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getAderenciaIntervalosColaboradorReport(Long codUnidade, Long dataInicial, Long dataFinal, String
            cpf) {
        try {
            return dao.getAderenciaIntervalosColaboradorReport(codUnidade, new Date(dataInicial), new Date(dataFinal)
                    , cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência por colaborador(REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
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
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório csv no padrão da portaria 1510. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %d \n" +
                    "cpf: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, codTipoIntervalo, cpf, dataInicial, dataFinal), e);
        }
    }

    public List<FolhaPontoRelatorio> getFolhaPontoRelatorio(@NotNull final Long codUnidade,
                                                            @NotNull final String codTipoIntervalo,
                                                            @NotNull final String cpf,
                                                            @NotNull final String dataHoraInicial,
                                                            @NotNull final String dataHoraFinal) {
        try {
            return dao.getFolhaPontoRelatorio(
                    codUnidade,
                    codTipoIntervalo,
                    cpf,
                    ProLogDateParser.toLocalDateTime(dataHoraInicial),
                    ProLogDateParser.toLocalDateTime(dataHoraFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de folha de ponto. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %s \n" +
                    "cpf: %s \n" +
                    "dataHoraInicial: %s \n" +
                    "dataHoraFinal: %s", codUnidade, codTipoIntervalo, cpf, dataHoraInicial, dataHoraFinal), e);
            throw new RuntimeException(e);
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
        } catch (SQLException e) {
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
        } catch (IOException | SQLException e) {
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
                                                @NotNull final String dataHoraInicial,
                                                @NotNull final String dataHoraFinal) {
        try {
            dao.getTotalTempoByTipoIntervaloCsv(
                    out,
                    codUnidade,
                    codTipoIntervalo,
                    ProLogDateParser.toLocalDateTime(dataHoraInicial),
                    ProLogDateParser.toLocalDateTime(dataHoraFinal));
        } catch (IOException | SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar csv do relatório de total de tempo para cada tipo de intervalo. \n" +
                    "codUnidade: %d \n" +
                    "codTipoIntervalo: %s \n" +
                    "dataHoraInicial: %s \n" +
                    "dataHoraFinal: %s", codUnidade, codTipoIntervalo, dataHoraInicial, dataHoraFinal), e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Report getTotalTempoByTipoIntervaloReport(@NotNull final Long codUnidade,
                                                     @NotNull final String codTipoIntervalo,
                                                     @NotNull final String dataHoraInicial,
                                                     @NotNull final String dataHoraFinal) throws ProLogException {
        try {
            return dao.getTotalTempoByTipoIntervaloReport(
                    codUnidade,
                    codTipoIntervalo,
                    ProLogDateParser.toLocalDateTime(dataHoraInicial),
                    ProLogDateParser.toLocalDateTime(dataHoraFinal));
        } catch (Throwable e) {
            final String errorMessage = String.format(
                    "Erro ao buscar report do relatório de total de tempo para cada tipo de intervalo. \n" +
                            "codUnidade: %d \n" +
                            "codTipoIntervalo: %s \n" +
                            "dataHoraInicial: %s \n" +
                            "dataHoraFinal: %s", codUnidade, codTipoIntervalo, dataHoraInicial, dataHoraFinal);
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }
}