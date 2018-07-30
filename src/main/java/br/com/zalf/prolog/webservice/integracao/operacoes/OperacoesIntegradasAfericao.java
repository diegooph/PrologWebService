package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericaoPlaca;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Operações integradas da aferição.
 */
interface OperacoesIntegradasAfericao {
    @NotNull
    CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Exception;

    @NotNull
    NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                           @NotNull final String placaVeiculo,
                                           @NotNull final String tipoAfericao) throws Exception;

    boolean insertAfericao(@NotNull final Afericao afericao, @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    AfericaoPlaca getAfericaoByCodigo(@NotNull final Long codUnidade, @NotNull final Long codAfericao) throws Exception;

    @NotNull
    List<AfericaoPlaca> getAfericoes(@NotNull final Long codUnidade,
                                     @NotNull final String codTipoVeiculo,
                                     @NotNull final String placaVeiculo,
                                     final long dataInicial,
                                     final long dataFinal,
                                     final int limit,
                                     final long offset) throws Exception;
}