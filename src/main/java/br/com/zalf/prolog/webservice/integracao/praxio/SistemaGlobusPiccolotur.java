package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data.GlobusPiccoloturRequester;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data.SistemaGlobusPiccoloturDao;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data.SistemaGlobusPiccoloturDaoImpl;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.concurrent.Executors;

/**
 * Created on 01/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaGlobusPiccolotur extends Sistema {
    @NotNull
    private final GlobusPiccoloturRequester requester;

    public SistemaGlobusPiccolotur(@NotNull final GlobusPiccoloturRequester requester,
                                   @NotNull final SistemaKey sistemaKey,
                                   @NotNull final IntegradorProLog integradorProLog,
                                   @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @NotNull
    @Override
    public Long insertChecklist(@NotNull final Checklist checklist) throws Throwable {
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        Connection conn = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);
            // TODO - Mover para o integradorProLog
            // Insere checklist na base de dados do ProLog
            final Long codChecklistProLog = Injection.provideChecklistDao().insert(conn, checklist, false);
            // Se o checklist tem pelo menos um item NOK, precisamos disparar o envio para a integração.
            if (checklist.getQtdItensNok() > 0) {
                // Marcamos que o checklist precisa ser sincronizado. Isso será útil para que o processamento disparado
                // pelo agendador consiga distinguir quais checklists são necessários serem sincronizados.
                getSistemaGlobusPiccoloturDaoImpl().insertItensNokPendentesParaSincronizar(conn, codChecklistProLog);
                // Faremos o processamento de envio dos itens NOK noutra thread para que o usuário que está realizando
                // o checklist possa seguir seu rumo naturalmente.
                Executors.newSingleThreadExecutor().execute(
                        new ChecklistItensNokGlobusTask(
                                codChecklistProLog,
                                true,
                                checklist,
                                getSistemaGlobusPiccoloturDaoImpl(),
                                requester,
                                null));
            }
            conn.commit();
            return codChecklistProLog;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    public void insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist,
                                      @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                                      final boolean statusAtivo) {
        throw new BloqueadoIntegracaoException("Devido à integração com o Sistema Globus, " +
                "a criação de modelos de checklist está bloqueada.");
    }

    @Override
    public void updateModeloChecklist(@NotNull final String token,
                                      @NotNull final Long codUnidade,
                                      @NotNull final Long codModelo,
                                      @NotNull final ModeloChecklistEdicao modeloChecklist,
                                      @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                                      final boolean sobrescreverPerguntasAlternativas) {
        throw new BloqueadoIntegracaoException("Devido à integração com o Sistema Globus, " +
                "a atualização de modelos de checklist está bloqueada.");
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Globus");
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Globus");
    }

    @NotNull
    private SistemaGlobusPiccoloturDao getSistemaGlobusPiccoloturDaoImpl() {
        return new SistemaGlobusPiccoloturDaoImpl();
    }
}
