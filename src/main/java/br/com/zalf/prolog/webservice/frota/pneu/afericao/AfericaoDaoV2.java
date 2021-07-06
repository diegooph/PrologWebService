package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface AfericaoDaoV2 {

    @NotNull
    Long insert(@NotNull final Connection conn,
                @NotNull final Long codUnidade,
                @NotNull final Afericao afericao,
                final boolean deveAbrirServico) throws Throwable;

    @NotNull
    Long insert(@NotNull final Long codUnidade,
                @NotNull final Afericao afericao,
                final boolean deveAbrirServico) throws Throwable;

    @NotNull
    NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final AfericaoBuscaFiltro afericaoBusca) throws Throwable;

    @NotNull
    NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable;

    @NotNull
    Restricao getRestricaoByCodUnidade(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    Restricao getRestricaoByCodUnidade(@NotNull final Connection conn,
                                       @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<AfericaoPlaca> getAfericoesPlacas(@NotNull final Long codUnidade,
                                           @NotNull final String codTipoVeiculo,
                                           @NotNull final String placaVeiculo,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal,
                                           final int limit,
                                           final long offset) throws Throwable;

    @NotNull
    List<AfericaoAvulsa> getAfericoesAvulsas(@NotNull final Long codUnidade,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal,
                                             final int limit,
                                             final long offset) throws Throwable;

    @NotNull
    Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                               @Nullable final Long codColaborador,
                               @NotNull final LocalDate dataInicial,
                               @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Afericao getByCod(@NotNull final Long codUnidade, @NotNull final Long codAfericao) throws Throwable;

    @NotNull
    @Deprecated
    Restricao getRestricoesByPlaca(@NotNull final String placa) throws Throwable;
}