package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 19/07/17.
 */
public interface OperacoesIntegradasAfericao {
    @NotNull
    boolean insertAfericao(@NotNull final Afericao afericao, @NotNull final Long codUnidade) throws Exception;
}