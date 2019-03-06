package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.model.TransportTranslecchiRequesterImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaTransportTranslecchi extends Sistema {
    @NotNull
    private final TransportTranslecchiRequesterImpl requester;

    public SistemaTransportTranslecchi(@NotNull final TransportTranslecchiRequesterImpl requester,
                                       @NotNull final SistemaKey sistemaKey,
                                       @NotNull final IntegradorProLog integradorProLog,
                                       @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @NotNull
    @Override
    public HolderResolucaoItensOrdemServico getHolderResolucaoMultiplosItens(
            @Nullable final Long codUnidade,
            @Nullable final Long codOrdemServico,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItens) throws Throwable {
        return requester.getHolderResolucaoMultiplosItens(codUnidade, codOrdemServico, placaVeiculo, statusItens);
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        requester.resolverItem(item);
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        requester.resolverItens(itensResolucao);
    }
}
