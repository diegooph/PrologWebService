package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;

/**
 * Created by luiz on 25/04/17.
 */
public interface ChecklistRelatorioDao {

    void getChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final Long codUnidade,
                                       @NotNull final LocalDate dataInicial,
                                       @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getChecklistsRealizadosDiaReport(@NotNull final Long codUnidade,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable;

    void getExtratoChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final Long codUnidade,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getExtratoChecklistsRealizadosDiaReport(@NotNull final Long codUnidade,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable;

    void getTempoRealizacaoChecklistMotoristaCsv(@NotNull final OutputStream outputStream,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final LocalDate dataInicial,
                                                 @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getTempoRealizacaoChecklistMotoristaReport(@NotNull final Long codUnidade,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal) throws Throwable;

    void getResumoChecklistCsv(@NotNull final OutputStream outputStream,
                               @NotNull final Long codUnidade,
                               @NotNull final String placa,
                               @NotNull final LocalDate dataInicial,
                               @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getResumoChecklistReport(@NotNull final Long codUnidade,
                                    @NotNull final String placa,
                                    @NotNull final LocalDate dataInicial,
                                    @NotNull final LocalDate dataFinal) throws Throwable;

    void getEstratificacaoRespostasNokChecklistCsv(@NotNull final OutputStream outputStream,
                                                   @NotNull final Long codUnidade,
                                                   @NotNull final String placa,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getEstratificacaoRespostasNokChecklistReport(@NotNull final Long codUnidade,
                                                        @NotNull final String placa,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable;
}