package br.com.zalf.prolog.webservice.integracao.transport.model;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class TransportTranslecchiRequesterImpl implements TransportTranslecchiRequester {

    @NotNull
    @Override
    public HolderResolucaoItensOrdemServico getHolderResolucaoMultiplosItens(
            @Nullable final Long codUnidade,
            @Nullable final Long codOrdemServico,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItens) throws Throwable {
        throw new GenericException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Transport");
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        throw new GenericException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Transport");
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        throw new GenericException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Transport");
    }
}
