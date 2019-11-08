package br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.integracao.api.pneu.movimentacao.ProcessoMovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/29/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SistemaApiProLogRequester extends Requester {
    @NotNull
    SuccessResponseIntegracao insertProcessoMovimentacao(
            @NotNull final String url,
            @NotNull final String tokenIntegracao,
            @NotNull final List<ProcessoMovimentacaoGlobus> processoMovimentacaoGlobus) throws Throwable;
}
