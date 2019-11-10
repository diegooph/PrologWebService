package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/10/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface OperacoesIntegradasMovimentacao {
    @NotNull
    Long insert(@NotNull final ServicoDao servicoDao,
                @NotNull final ProcessoMovimentacao processoMovimentacao,
                final boolean fecharServicosAutomaticamente) throws Throwable;
}
