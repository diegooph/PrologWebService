package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created by luiz on 25/04/17.
 */
class ChecklistRelatorioService {
    private static final String TAG = ChecklistRelatorioService.class.getSimpleName();
    @NotNull
    private final ChecklistRelatorioDao dao = Injection.provideChecklistRelatorioDao();

    void getChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final Long codUnidade,
                                       @NotNull final String dataInicial,
                                       @NotNull final String dataFinal) {
        try {
            dao.getChecklistsRealizadosDiaCsv(
                    outputStream,
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com os checklists realizados por dia (CSV)", e);
        }
    }

    @NotNull
    Report getChecklistsRealizadosDiaReport(@NotNull final Long codUnidade,
                                            @NotNull final String dataInicial,
                                            @NotNull final String dataFinal) {
        try {
            return dao.getChecklistsRealizadosDiaReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com os checklists realizados por dia (REPORT)", e);
            return null;
        }
    }

    void getExtratoChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final Long codUnidade,
                                              @NotNull final String dataInicial,
                                              @NotNull final String dataFinal) {
        try {
            dao.getExtratoChecklistsRealizadosDiaCsv(
                    outputStream,
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o extrato dos checklists realizados por dia (CSV)", e);
        }
    }

    @NotNull
    Report getExtratoChecklistsRealizadosDiaReport(@NotNull final Long codUnidade,
                                                   @NotNull final String dataInicial,
                                                   @NotNull final String dataFinal) {
        try {
            return dao.getExtratoChecklistsRealizadosDiaReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o extrato dos checklists realizados por dia (CSV)", e);
            return null;
        }
    }

    void getTempoRealizacaoChecklistMotoristaCsv(@NotNull final OutputStream outputStream,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final String dataInicial,
                                                 @NotNull final String dataFinal) {
        try {
            dao.getTempoRealizacaoChecklistMotoristaCsv(
                    outputStream,
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o tempo de realização por motorista (CSV)", e);
        }
    }

    @NotNull
    Report getTempoRealizacaoChecklistMotoristaReport(@NotNull final Long codUnidade,
                                                      @NotNull final String dataInicial,
                                                      @NotNull final String dataFinal) {
        try {
            return dao.getTempoRealizacaoChecklistMotoristaReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o tempo de realização por motorista (REPORT)", e);
            return null;
        }
    }

    void getResumoChecklistCsv(@NotNull final OutputStream outputStream,
                               @NotNull final Long codUnidade,
                               @NotNull final String placa,
                               @NotNull final String dataInicial,
                               @NotNull final String dataFinal) {
        try {
            dao.getResumoChecklistCsv(
                    outputStream,
                    codUnidade,
                    placa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o resumo dos checklist (CSV)", e);
        }
    }

    @NotNull
    Report getResumoChecklistReport(@NotNull final Long codUnidade,
                                    @NotNull final String placa,
                                    @NotNull final String dataInicial,
                                    @NotNull final String dataFinal) {
        try {
            return dao.getResumoChecklistReport(
                    codUnidade,
                    placa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o resumo dos checklist (REPORT)", e);
            return null;
        }
    }

    @NotNull
    Report getEstratificacaoRespostasNokChecklistReport(@NotNull final Long codUnidade,
                                                        @NotNull final String placa,
                                                        @NotNull final String dataInicial,
                                                        @NotNull final String dataFinal) {
        try {
            return dao.getEstratificacaoRespostasNokChecklistReport(
                    codUnidade,
                    placa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com a estratificação das respostas NOK dos checklists (REPORT)", e);
            return null;
        }
    }

    void getEstratificacaoRespostasNokChecklistCsv(@NotNull final OutputStream outputStream,
                                                   @NotNull final Long codUnidade,
                                                   @NotNull final String placa,
                                                   @NotNull final String dataInicial,
                                                   @NotNull final String dataFinal) {
        try {
            dao.getEstratificacaoRespostasNokChecklistCsv(
                    outputStream,
                    codUnidade,
                    placa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com a estratificação das respostas NOK dos checklists (CSV)", e);
        }
    }
}