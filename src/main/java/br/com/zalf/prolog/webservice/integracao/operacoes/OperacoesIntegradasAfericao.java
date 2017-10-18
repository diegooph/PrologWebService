package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import com.sun.istack.internal.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Operações integradas da aferição.
 */
interface OperacoesIntegradasAfericao {
    @NotNull
    CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Exception;

    @NotNull
    NovaAfericao getNovaAfericao(@NotNull final String placaVeiculo) throws Exception;

    boolean insertAfericao(@NotNull final Afericao afericao, @NotNull final Long codUnidade) throws Exception;

    @Nonnull
    Afericao getAfericaoByCodigo(@Nonnull final Long codUnidade, @Nonnull final Long codAfericao) throws Exception;

    @NotNull
    List<Afericao> getAfericoes(@NotNull final Long codUnidade,
                                @NotNull final String codTipoVeiculo,
                                @NotNull final String placaVeiculo,
                                final long dataInicial,
                                final long dataFinal,
                                final long limit,
                                final long offset) throws Exception;
}