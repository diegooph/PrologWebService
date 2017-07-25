package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import com.sun.istack.internal.NotNull;

/**
 * Operações integradas da aferição.
 */
interface OperacoesIntegradasAfericao {
    @NotNull
    NovaAfericao getNovaAfericao(@NotNull final String placaVeiculo) throws Exception;

    boolean insertAfericao(@NotNull final Afericao afericao, @NotNull final Long codUnidade) throws Exception;
}