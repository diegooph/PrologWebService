package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.servico.model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.VeiculoServico;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface OperacoesIntegradasAfericaoServico {
    @NotNull
    VeiculoServico getVeiculoAberturaServico(@NotNull final Long codServico,
                                             @NotNull final String placaVeiculo) throws Throwable;
    void fechaServico(@NotNull final Long codUnidade, @NotNull final Servico servico) throws Throwable;
}
