package br.com.zalf.prolog.webservice.integracao.transport.model;

import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
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
    public void insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist) throws Throwable {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Transport");
    }

    @Override
    public void updateModeloChecklist(@NotNull final String token,
                                      @NotNull final Long codUnidade,
                                      @NotNull final Long codModelo,
                                      @NotNull final ModeloChecklistEdicao modeloChecklist) throws Throwable {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Transport");
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Transport");
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Transport");
    }
}
