package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.model.TransportTranslecchiRequesterImpl;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist) throws Throwable {
        requester.insertModeloChecklist(modeloChecklist);
    }

    @Override
    public void updateModeloChecklist(@NotNull final String token,
                                      @NotNull final Long codUnidade,
                                      @NotNull final Long codModelo,
                                      @NotNull final ModeloChecklistEdicao modeloChecklist) throws Throwable {
        requester.updateModeloChecklist(token, codUnidade, codModelo, modeloChecklist);
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
