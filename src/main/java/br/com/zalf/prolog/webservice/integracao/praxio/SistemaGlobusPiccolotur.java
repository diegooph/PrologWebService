package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklistStatus;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data.GlobusPiccoloturRequester;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data.SistemaGlobusPiccoloturDao;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data.SistemaGlobusPiccoloturDaoImpl;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistItensNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            final Long codChecklistProLog = Injection.provideChecklistDao().insert(conn, checklist, false);
            // Se o checklist só possui itens OK, não precisamos processar mais nada.
            if (checklist.getQtdItensNok() <= 0) {
                return codChecklistProLog;
            }

            // TODO - Mover para o integradorProLog
            final Long codUnidadeProLog = Injection
                    .provideVeiculoDao()
                    .getCodUnidadeByPlaca(conn, checklist.getPlacaVeiculo());

            final Map<Long, AlternativaChecklistStatus> alternativasStatus =
                    Injection
                            .provideChecklistDao()
                            .getItensStatus(conn, checklist.getCodModelo(), checklist.getPlacaVeiculo());

            final List<Long> codItensOsIncrementaQtdApontamentos = new ArrayList<>();

            for (final PerguntaRespostaChecklist pergunta : checklist.getListRespostas()) {
                for (final AlternativaChecklist alternativa : pergunta.getAlternativasResposta()) {
                    final AlternativaChecklistStatus alternativaChecklistStatus =
                            alternativasStatus.get(alternativa.getCodigo());
                    if (alternativaChecklistStatus != null
                            && alternativaChecklistStatus.getQtdApontamentosItemOs() > 0) {
                        codItensOsIncrementaQtdApontamentos.add(alternativaChecklistStatus.getCodItemOsAlternativa());
                    }
                }
            }

            if (!codItensOsIncrementaQtdApontamentos.isEmpty()) {
                Injection.provideOrdemServicoDao().incrementaQtdApontamentos(conn, codItensOsIncrementaQtdApontamentos);
            }

            final ChecklistItensNokGlobus checklistItensNokGlobus =
                    GlobusPiccoloturConverter.createChecklistItensNokGlobus(
                            codUnidadeProLog,
                            codChecklistProLog,
                            checklist,
                            alternativasStatus);

            // Antes de enviar as informações para o Globus, salvamos quais foram os itens Nok enviados.
            getSistemaGlobusPiccoloturDaoImpl().insertItensNokEnviadosGlobus(conn, checklistItensNokGlobus);

            final Long codOsAbertaGlobus =
                    requester.insertItensNok(GlobusPiccoloturConverter.convert(checklistItensNokGlobus));
            if (codOsAbertaGlobus <= 0) {
                throw new GlobusPiccoloturException("[ERRO INTEGRAÇÃO]: Globus retornou um código de O.S inválido");
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
    public void insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist) throws Throwable {
        throw new BloqueadoIntegracaoException("Devido à integração com o Sistema Globus, " +
                "a criação de modelos de checklist está bloqueada.");
    }

    @Override
    public void updateModeloChecklist(@NotNull final String token,
                                      @NotNull final Long codUnidade,
                                      @NotNull final Long codModelo,
                                      @NotNull final ModeloChecklistEdicao modeloChecklist) throws Throwable {
        throw new BloqueadoIntegracaoException("Devido à integração com o Sistema Globus, " +
                "a atualização de modelos de checklist está bloqueada.");
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Globus");
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        throw new BloqueadoIntegracaoException("O fechamento de itens de O.S. deverá ser feito pelo Sistema Globus");
    }

    @NotNull
    private SistemaGlobusPiccoloturDao getSistemaGlobusPiccoloturDaoImpl() {
        return new SistemaGlobusPiccoloturDaoImpl();
    }
}
