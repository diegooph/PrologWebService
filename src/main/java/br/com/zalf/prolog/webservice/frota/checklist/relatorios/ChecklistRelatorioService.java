package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
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
        }
    }

    @NotNull
    Report getChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidade,
                                                 @NotNull final String dataInicial,
                                                 @NotNull final String dataFinal) {
        try {
            return dao.getChecklistsRealizadosDiaAmbevReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com os checklists realizados por dia (REPORT)", e);
            return null;
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
        }
    }

    @NotNull
    Report getExtratoChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidade,
                                                        @NotNull final String dataInicial,
                                                        @NotNull final String dataFinal) {
        try {
            return dao.getExtratoChecklistsRealizadosDiaAmbevReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o extrato dos checklists realizados por dia (CSV)", e);
            return null;
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
        }
    }

    @NotNull
    Report getTempoRealizacaoChecklistsMotoristasReport(@NotNull final List<Long> codUnidade,
                                                        @NotNull final String dataInicial,
                                                        @NotNull final String dataFinal) {
        try {
            return dao.getTempoRealizacaoChecklistsMotoristasReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com o tempo de realização por motorista (REPORT)", e);
            return null;
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
        }
    }

    @NotNull
    Report getResumoChecklistsReport(@NotNull final List<Long> codUnidade,
                                     @NotNull final String placa,
                                     @NotNull final String dataInicial,
                                     @NotNull final String dataFinal) {
        try {
            return dao.getResumoChecklistsReport(
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
    Report getEstratificacaoRespostasNokReport(@NotNull final List<Long> codUnidade,
                                               @NotNull final String placa,
                                               @NotNull final String dataInicial,
                                               @NotNull final String dataFinal) {
        try {
            return dao.getEstratificacaoRespostasNokReport(
                    codUnidade,
                    placa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com a estratificação das respostas NOK dos checklists (REPORT)", e);
            return null;
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
        }
    }

    public Report getListagemModelosChecklistReport(@NotNull final List<Long> codUnidades) {
        try {
            return dao.getListagemModelosChecklistReport(
                    codUnidades);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com a listagem de modelos dos checklists (REPORT)", e);
            return null;
        }
    }

    public void getListagemModelosChecklistCsv(@NotNull final OutputStream outputStream,
                                               @NotNull final List<Long> codUnidades) {
        try {
            dao.getListagemModelosChecklistCsv(
                    outputStream,
                    codUnidades);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o relatório com a listagem de modelos dos checklists (CSV)", e);
        }
    }
}