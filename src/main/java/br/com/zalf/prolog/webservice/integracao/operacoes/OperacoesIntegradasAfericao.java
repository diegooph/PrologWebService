package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Operações integradas da aferição.
 */
interface OperacoesIntegradasAfericao {
    @NotNull
    CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                           @NotNull final String placaVeiculo,
                                           @NotNull final String tipoAfericao) throws Throwable;

    @NotNull
    List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable;

    Long insertAfericao(@NotNull final Afericao afericao, @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    Afericao getAfericaoByCodigo(@NotNull final Long codUnidade, @NotNull final Long codAfericao) throws Throwable;

    @NotNull
    List<AfericaoPlaca> getAfericoesPlacas(@NotNull final Long codUnidade,
                                           @NotNull final String codTipoVeiculo,
                                           @NotNull final String placaVeiculo,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal,
                                           final int limit,
                                           final long offset) throws Throwable;
}