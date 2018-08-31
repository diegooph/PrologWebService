package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by luiz on 25/04/17.
 */
public interface ChecklistRelatorioDao {

    void getChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidade,
                                       @NotNull final LocalDate dataInicial,
                                       @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getChecklistsRealizadosDiaReport(@NotNull final List<Long> codUnidade,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable;

    void getExtratoChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final List<Long> codUnidade,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getExtratoChecklistsRealizadosDiaReport(@NotNull final List<Long> codUnidade,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable;

    void getTempoRealizacaoChecklistsMotoristaCsv(@NotNull final OutputStream outputStream,
                                                  @NotNull final List<Long> codUnidade,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getTempoRealizacaoChecklistsMotoristaReport(@NotNull final List<Long> codUnidade,
                                                       @NotNull final LocalDate dataInicial,
                                                       @NotNull final LocalDate dataFinal) throws Throwable;

    void getResumoChecklistsCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidade,
                                @NotNull final String placa,
                                @NotNull final LocalDate dataInicial,
                                @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getResumoChecklistsReport(@NotNull final List<Long> codUnidade,
                                     @NotNull final String placa,
                                     @NotNull final LocalDate dataInicial,
                                     @NotNull final LocalDate dataFinal) throws Throwable;

    void getEstratificacaoRespostasNokCsv(@NotNull final OutputStream outputStream,
                                          @NotNull final List<Long> codUnidade,
                                          @NotNull final String placa,
                                          @NotNull final LocalDate dataInicial,
                                          @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getEstratificacaoRespostasNokReport(@NotNull final List<Long> codUnidade,
                                               @NotNull final String placa,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws Throwable;
}