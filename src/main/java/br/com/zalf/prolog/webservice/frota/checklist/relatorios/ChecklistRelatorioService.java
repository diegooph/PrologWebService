package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created by luiz on 25/04/17.
 */
class ChecklistRelatorioService {
    private static final String TAG = ChecklistRelatorioService.class.getSimpleName();
    @NotNull
    private final ChecklistRelatorioDao dao = Injection.provideChecklistRelatorioDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    void getChecklistsRealizadosDiaAmbevCsv(@NotNull final OutputStream outputStream,
                                            @NotNull final List<Long> codUnidade,
                                            @NotNull final String dataInicial,
                                            @NotNull final String dataFinal) {
        try {
            dao.getChecklistsRealizadosDiaAmbevCsv(
                    outputStream,
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com os checklists realizados por dia (CSV)", e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    Report getChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidade,
                                                 @NotNull final String dataInicial,
                                                 @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getChecklistsRealizadosDiaAmbevReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com os checklists realizados por dia (REPORT)", e);
            throw exceptionHandler.map(e,
                    "Erro ao buscar o relatório com os checklists realizados por dia," +
                            " tente novamente");
        }
    }

    void getExtratoChecklistsRealizadosDiaAmbevCsv(@NotNull final OutputStream outputStream,
                                                   @NotNull final List<Long> codUnidade,
                                                   @NotNull final String dataInicial,
                                                   @NotNull final String dataFinal) {
        try {
            dao.getExtratoChecklistsRealizadosDiaAmbevCsv(
                    outputStream,
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o extrato dos checklists realizados por dia (CSV)", e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    Report getExtratoChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidade,
                                                        @NotNull final String dataInicial,
                                                        @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getExtratoChecklistsRealizadosDiaAmbevReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o extrato dos checklists realizados por dia (CSV)", e);
            throw exceptionHandler.map(e,
                    "Erro ao buscar o relatório com o extrato dos checklists realizados por dia," +
                            " tente novamente");
        }
    }

    void getTempoRealizacaoChecklistsMotoristasCsv(@NotNull final OutputStream outputStream,
                                                   @NotNull final List<Long> codUnidade,
                                                   @NotNull final String dataInicial,
                                                   @NotNull final String dataFinal) {
        try {
            dao.getTempoRealizacaoChecklistsMotoristasCsv(
                    outputStream,
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o tempo de realização por motorista (CSV)", e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    Report getTempoRealizacaoChecklistsMotoristasReport(@NotNull final List<Long> codUnidade,
                                                        @NotNull final String dataInicial,
                                                        @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getTempoRealizacaoChecklistsMotoristasReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o tempo de realização por motorista (REPORT)", e);
            throw exceptionHandler.map(e,
                    "Erro ao buscar o relatório com o tempo de realização por motorista," +
                            " tente novamente");
        }
    }

    void getResumoChecklistsCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidade,
                                @NotNull final String placa,
                                @NotNull final String dataInicial,
                                @NotNull final String dataFinal) {
        try {
            dao.getResumoChecklistsCsv(
                    outputStream,
                    codUnidade,
                    placa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o resumo dos checklist (CSV)", e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    Report getResumoChecklistsReport(@NotNull final List<Long> codUnidade,
                                     @NotNull final String placa,
                                     @NotNull final String dataInicial,
                                     @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getResumoChecklistsReport(
                    codUnidade,
                    placa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o resumo dos checklist (REPORT)", e);
            throw exceptionHandler.map(e,
                    "Erro ao buscar o relatório com o resumo dos checklist," +
                            " tente novamente");
        }
    }

    @NotNull
    Report getEstratificacaoRespostasNokReport(@NotNull final List<Long> codUnidade,
                                               @NotNull final String placa,
                                               @NotNull final String dataInicial,
                                               @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getEstratificacaoRespostasNokReport(
                    codUnidade,
                    placa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com a estratificação das respostas NOK dos checklists (REPORT)", e);
            throw exceptionHandler.map(e,
                    "Erro ao buscar o relatório com a estratificação das respostas NOK," +
                            " tente novamente");
        }
    }

    void getEstratificacaoRespostasNokCsv(@NotNull final OutputStream outputStream,
                                          @NotNull final List<Long> codUnidade,
                                          @NotNull final String placa,
                                          @NotNull final String dataInicial,
                                          @NotNull final String dataFinal) {
        try {
            dao.getEstratificacaoRespostasNokCsv(
                    outputStream,
                    codUnidade,
                    placa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com a estratificação das respostas NOK dos checklists (CSV)", e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    Report getListagemModelosChecklistReport(@NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return dao.getListagemModelosChecklistReport(codUnidades);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com a listagem de modelos dos checklists (REPORT)", e);
            throw exceptionHandler.map(e,
                    "Erro ao buscar o relatório com a listagem de modelos dos checklists," +
                            " tente novamente");
        }
    }

    void getListagemModelosChecklistCsv(@NotNull final OutputStream outputStream,
                                        @NotNull final List<Long> codUnidades) {
        try {
            dao.getListagemModelosChecklistCsv(
                    outputStream,
                    codUnidades);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com a listagem de modelos dos checklists (CSV)", e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    Report getDadosGeraisChecklistReport(@NotNull final List<Long> codUnidades,
                                         @NotNull final String dataInicial,
                                         @NotNull final String dataFinal,
                                         final Integer codColaborador,
                                         final String placa) throws ProLogException {
        try {
            return dao.getDadosGeraisChecklistReport(
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal),
                    codColaborador,
                    placa);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório de dados dos checklists (REPORT)", e);
            throw exceptionHandler.map(e,
                    "Erro ao buscar o relatório de dados dos checklists," +
                            " tente novamente");
        }
    }

    void getDadosGeraisChecklistCsv(@NotNull final OutputStream outputStream,
                                    @NotNull final List<Long> codUnidades,
                                    @NotNull final String dataInicial,
                                    @NotNull final String dataFinal,
                                    final Integer codColaborador,
                                    final String placa) {
        try {
            dao.getDadosGeraisChecklistCsv(
                    outputStream,
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal),
                    codColaborador,
                    placa);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório de dados gerais dos checklists (CSV)", e);
            throw new RuntimeException(e);
        }
    }
}