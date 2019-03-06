package br.com.zalf.prolog.webservice.integracao.transport.model;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface TransportTranslecchiRequester extends Requester {

    void insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist) throws Throwable;

    void updateModeloChecklist(@NotNull final String token,
                               @NotNull final Long codUnidade,
                               @NotNull final Long codModelo,
                               @NotNull final ModeloChecklistEdicao modeloChecklist) throws Throwable;

    void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable;

    void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable;
}
