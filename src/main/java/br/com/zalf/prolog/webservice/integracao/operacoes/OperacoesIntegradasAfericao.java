package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 19/07/17.
 */
public interface OperacoesIntegradasAfericao {
    @NotNull
    NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception;
    @NotNull
    boolean insertAfericao(@NotNull final Afericao afericao, @NotNull final Long codUnidade) throws Exception;
}