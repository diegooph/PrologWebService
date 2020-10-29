package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.PlacasBloqueadasResponse;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.PlacaItensOsAbertos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by luiz on 26/04/17.
 */
public interface OrdemServicoRelatorioDao {

    @NotNull
    List<PlacaItensOsAbertos> getPlacasMaiorQtdItensOsAbertos(@NotNull final List<Long> codUnidades,
                                                              final int qtdPlacasParaBuscar) throws Throwable;

    @NotNull
    Map<PrioridadeAlternativa, Integer> getQtdItensOsByPrioridade(@NotNull final List<Long> codUnidades,
                                                                  @NotNull final ItemOrdemServico.Status statusItensContagem)
            throws Throwable;

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
                                @NotNull final String statusOs,
                                @NotNull final String statusItemOs,
                                @Nullable final LocalDate dataInicialAbertura,
                                @Nullable final LocalDate dataFinalAbertura,
                                @Nullable final LocalDate dataInicialResolucao,
                                @Nullable final LocalDate dataFinalResolucao) throws Throwable;

    @NotNull
    Report getEstratificacaoOsReport(@NotNull final List<Long> codUnidades,
                                     @NotNull final String placa,
                                     @NotNull final String statusOs,
                                     @NotNull final String statusItemOs,
                                     @Nullable final LocalDate dataInicialAbertura,
                                     @Nullable final LocalDate dataFinalAbertura,
                                     @Nullable final LocalDate dataInicialResolucao,
                                     @Nullable final LocalDate dataFinalResolucao) throws Throwable;
    /**
     * Método para buscar placas bloqueadas - Componente Dash.
     *
     * @param codUnidades      Códigos das unidades pelas quais as informações serão filtradas.
     * @throws Throwable Se algum erro ocorrer.
     * @return PlacasBloqueadasResponse retorna objeto com as placas que estão bloqueadas.
     */
    @NotNull
    PlacasBloqueadasResponse getPlacasBloqueadas(@NotNull final List<Long> codUnidades) throws Throwable;
}