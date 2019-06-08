package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturRequester;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ChecklistItensNokGlobus;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 01/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class SistemaGlobusPiccolotur extends Sistema {
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
        Connection conn = null;
        try {
            /*
             * Inserir o checklist no ProLog SEM ABRIR O.S.
             * Incrementar a quantidade de apontamentos dos itens apontados como NOK já abertos.
             * Buscar status dos Itens.
             * Filtrar apenas os Itens que devem abrir uma O.S.
             * Converter para o objeto específico da Integração e enviar via Requester.
             */
            conn = new DatabaseConnectionProvider().provideDatabaseConnection();
            conn.setAutoCommit(false);
            // TODO - Mover para o integradorProLog
            final Long codChecklistProLog = Injection.provideChecklistDao().insert(conn, checklist, false);
            // Se o checklist só possui itens OK, não precisamos processar mais nada.
            if (checklist.getQtdItensNok() <= 0) {
                return codChecklistProLog;
            }

            // TODO - Mover para o integradorProLog
            final Long codUnidadeProLog = Injection
                    .provideVeiculoDao()
                    .getCodUnidadeByPlaca(conn, checklist.getPlacaVeiculo());

            final ChecklistItensNokGlobus checklistItensNokGlobus =
                    GlobusPiccoloturConverter.createChecklistItensNokGlobus(
                            codUnidadeProLog,
                            codChecklistProLog,
                            checklist);
            // TODO - Ainda não está pronto. Falta implementar
//            Injection
//                    .provideOrdemServicoDao()
//                    .incrementaQtdApontamentos(conn, checklist.getPlacaVeiculo(), checklist.getListRespostas());
            requester.insertItensNok(GlobusPiccoloturConverter.convert(checklistItensNokGlobus));
            conn.commit();
            return codChecklistProLog;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            new DatabaseConnectionProvider().closeResources(conn);
        }
    }
}
