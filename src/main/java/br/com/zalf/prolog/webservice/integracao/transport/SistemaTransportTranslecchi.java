package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
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
                                       @NotNull final RecursoIntegrado recursoIntegrado,
                                       @NotNull final IntegradorProLog integradorProLog,
                                       @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, recursoIntegrado, userToken);
    }

    @NotNull
    @Override
    public ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String userToken) throws Throwable {
        // Ignoramos o statusAtivo repassado pois queremos for??ar que o modelo de checklist tenha o statusAtivo = false.
        return getIntegradorProLog()
                .insertModeloChecklist(modeloChecklist, checklistOfflineListener, false, userToken);
    }

    @Override
    public void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean podeMudarCodigoContextoPerguntasEAlternativas,
            @NotNull final String userToken) throws Throwable {
        // Ignoramos a propriedade sobrescreverPerguntasAlternativas pois queremos que para essa integra????o todas as
        // edi????es de perguntas e alternativas sobrescrevam os valores antigos sem alterar os c??digos existentes.
        getIntegradorProLog()
                .updateModeloChecklist(
                        codUnidade,
                        codModelo,
                        modeloChecklist,
                        checklistOfflineListener,
                        false,
                        userToken);
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. dever?? ser feito pelo Sistema Transport");
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. dever?? ser feito pelo Sistema Transport");
    }
}
