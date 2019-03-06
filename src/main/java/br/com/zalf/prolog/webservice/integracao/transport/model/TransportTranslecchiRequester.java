package br.com.zalf.prolog.webservice.integracao.transport.model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface TransportTranslecchiRequester extends Requester {

    @NotNull
    HolderResolucaoItensOrdemServico getHolderResolucaoMultiplosItens(
            @Nullable final Long codUnidade,
            @Nullable final Long codOrdemServico,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItens) throws Throwable;

    void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable;

    void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable;
}
