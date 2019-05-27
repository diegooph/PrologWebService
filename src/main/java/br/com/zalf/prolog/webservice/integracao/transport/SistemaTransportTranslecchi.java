package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaTransportTranslecchi extends Sistema {

    public SistemaTransportTranslecchi(@NotNull final SistemaKey sistemaKey,
                                       @NotNull final IntegradorProLog integradorProLog,
                                       @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
    }

    @Override
    public void insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        throw new BloqueadoIntegracaoException("Devido à integração com o Sistema Transport, " +
                "a criação de modelos de checklist está bloqueada.");
    }

    @Override
    public void updateModeloChecklist(
            @NotNull final String token,
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        throw new BloqueadoIntegracaoException("Devido à integração com o Sistema Transport, " +
                "a atualização de modelos de checklist está bloqueada.");
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
