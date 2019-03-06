package br.com.zalf.prolog.webservice.integracao.transport.model;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class TransportTranslecchiRequesterImpl implements TransportTranslecchiRequester {

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        throw new GenericException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Transport");
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        throw new GenericException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Transport");
    }
}
