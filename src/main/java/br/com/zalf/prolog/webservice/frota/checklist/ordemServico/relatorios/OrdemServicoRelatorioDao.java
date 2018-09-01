package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServico;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by luiz on 26/04/17.
 */
public interface OrdemServicoRelatorioDao {

    void getItensMaiorQuantidadeNokCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final LocalDate dataInicial,
                                       @NotNull final LocalDate dataFinal) throws Throwable;
    @NotNull
    Report getItensMaiorQuantidadeNokReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable;

    void getMediaTempoConsertoItemCsv(@NotNull final OutputStream outputStream,
                                      @NotNull final List<Long> codUnidades,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable;
    @NotNull
    Report getMediaTempoConsertoItemReport(@NotNull final List<Long> codUnidades,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws Throwable;

    void getProdutividadeMecanicosCsv(@NotNull final OutputStream outputStream,
                                      @NotNull final List<Long> codUnidades,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable;
    @NotNull
    Report getProdutividadeMecanicosReport(@NotNull final List<Long> codUnidades,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws Throwable;

    void getEstratificacaoOsCsv(@NotNull final OutputStream outputStream, 
                                @NotNull final List<Long> codUnidades, 
                                @NotNull final String placa,
                                @NotNull final OrdemServico.Status statusOs,
                                @NotNull final ItemOrdemServico.Status statusItemOs,
                                @NotNull final LocalDate dataInicial,
                                @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getEstratificacaoOsReport(@NotNull final List<Long> codUnidades,
                                     @NotNull final String placa,
                                     @NotNull final OrdemServico.Status statusOs,
                                     @NotNull final ItemOrdemServico.Status statusItemOs,
                                     @NotNull final LocalDate dataInicial,
                                     @NotNull final LocalDate dataFinal) throws Throwable;
}