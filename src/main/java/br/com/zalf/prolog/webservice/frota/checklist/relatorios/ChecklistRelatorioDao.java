package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecksRealizadosAbaixoTempoEspecifico;
import br.com.zalf.prolog.webservice.frota.checklist.model.QuantidadeChecklists;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by luiz on 25/04/17.
 */
public interface ChecklistRelatorioDao {

    @NotNull
    List<ChecksRealizadosAbaixoTempoEspecifico> getQtdChecksRealizadosAbaixoTempoEspecifico(@NotNull final List<Long> codUnidades,
                                                                                            final long tempoRealizacaoFiltragemMilis,
                                                                                            final int diasRetroativosParaBuscar)
            throws Throwable;

    @NotNull
    List<QuantidadeChecklists> getQtdChecklistsRealizadosByTipo(@NotNull final List<Long> codUnidades,
                                                                final int diasRetroativosParaBuscar)
            throws Throwable;

    void getChecklistsRealizadosDiaAmbevCsv(@NotNull final OutputStream outputStream,
                                            @NotNull final List<Long> codUnidade,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidade,
                                                 @NotNull final LocalDate dataInicial,
                                                 @NotNull final LocalDate dataFinal) throws Throwable;

    void getExtratoChecklistsRealizadosDiaAmbevCsv(@NotNull final OutputStream outputStream,
                                                   @NotNull final List<Long> codUnidade,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getExtratoChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidade,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable;

    void getTempoRealizacaoChecklistsMotoristasCsv(@NotNull final OutputStream outputStream,
                                                   @NotNull final List<Long> codUnidade,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getTempoRealizacaoChecklistsMotoristasReport(@NotNull final List<Long> codUnidade,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable;

    void getResumoChecklistsCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidade,
                                @NotNull final LocalDate dataInicial,
                                @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getResumoChecklistsReport(@NotNull final List<Long> codUnidade,
                                     @NotNull final LocalDate dataInicial,
                                     @NotNull final LocalDate dataFinal) throws Throwable;

    void getEstratificacaoRespostasNokCsv(@NotNull final OutputStream outputStream,
                                          @NotNull final List<Long> codUnidade,
                                          @NotNull final LocalDate dataInicial,
                                          @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getEstratificacaoRespostasNokReport(@NotNull final List<Long> codUnidade,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws Throwable;

    void getListagemModelosChecklistCsv(@NotNull final OutputStream outputStream,
                                        @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Report getListagemModelosChecklistReport(@NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Report getUltimoChecklistRealizadoPlacaReport(@NotNull final List<Long> codUnidades,
                                                  @NotNull final List<Long> codTiposVeiculos) throws Throwable;

    void getUltimoChecklistRealizadoPlacaCsv(@NotNull final OutputStream outputStream,
                                             @NotNull final List<Long> codUnidades,
                                             @NotNull final List<Long> codTiposVeiculos) throws Throwable;
}