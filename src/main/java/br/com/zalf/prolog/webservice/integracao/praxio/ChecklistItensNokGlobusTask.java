package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.InfosAlternativaAberturaOrdemServico;
import br.com.zalf.prolog.webservice.integracao.agendador.SincroniaChecklistListener;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturRequester;
import br.com.zalf.prolog.webservice.integracao.praxio.data.SistemaGlobusPiccoloturDao;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.AlternativaNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistItensNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistToSyncGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.PerguntaNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created on 01/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ChecklistItensNokGlobusTask implements Runnable {
    @NotNull
    private final Long codChecklistProLog;
    @NotNull
    private final Boolean isLastChecklist;
    @NotNull
    private final SistemaGlobusPiccoloturDao sistema;
    @NotNull
    private final GlobusPiccoloturRequester requester;
    @Nullable
    private final SincroniaChecklistListener listener;

    public ChecklistItensNokGlobusTask(@NotNull final Long codChecklistProLog,
                                       @NotNull final Boolean isLastChecklist,
                                       @NotNull final SistemaGlobusPiccoloturDao sistema,
                                       @NotNull final GlobusPiccoloturRequester requester,
                                       @Nullable final SincroniaChecklistListener listener) {
        this.codChecklistProLog = codChecklistProLog;
        this.sistema = sistema;
        this.requester = requester;
        this.listener = listener;
        this.isLastChecklist = isLastChecklist;
    }

    @Override
    public void run() {
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        Connection conn = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);

            final ChecklistToSyncGlobus checklistToSyncGlobus =
                    sistema.getChecklistToSyncGlobus(conn, codChecklistProLog);
            final ChecklistItensNokGlobus checklistItensNokGlobus = checklistToSyncGlobus.getChecklistItensNokGlobus();

            // Apenas por garantia, verificamos novamente se é necessário enviar alguma coisa para a integração.
            if (checklistItensNokGlobus.getPerguntasNok().size() <= 0) {
                // Marca checklist como não precisa ser sincronizado.
                sistema.marcaChecklistNaoPrecisaSincronizar(conn, codChecklistProLog);
                conn.commit();
                // Avisamos que a sincronia não foi necessária para o checklist em questão.
                if (listener != null) {
                    listener.onSincroniaNaoExecutada(codChecklistProLog, isLastChecklist);
                }
                return;
            }

            // Dentro do checklist realizado buscamos os itens apontados como NOK que devem abrir O.S ou incrementar a
            // quantidade apontamentos.
            final Map<Long, List<InfosAlternativaAberturaOrdemServico>> alternativasStatus =
                    Injection
                            .provideOrdemServicoDao()
                            .getItensStatus(
                                    conn,
                                    checklistToSyncGlobus.getCodModeloChecklist(),
                                    checklistToSyncGlobus.getCodVersaoModeloChecklist(),
                                    checklistToSyncGlobus.getPlacaVeiculoChecklist());
            final List<InfosAlternativaAberturaOrdemServico> itensOsIncrementaQtdApontamentos =
                    getItensIncrementaApontamentos(alternativasStatus, checklistItensNokGlobus.getPerguntasNok());

            // Verificamos se tem algum item que deve incrementar a quantidade de apontamentos em alguma O.S.
            if (!itensOsIncrementaQtdApontamentos.isEmpty()) {
                Injection
                        .provideOrdemServicoDao()
                        .incrementaQtdApontamentos(conn, codChecklistProLog, itensOsIncrementaQtdApontamentos);

                // Após incrementar a quantidade de apontamento das perguntas necessárias, removemos elas da lista de
                // que perguntas que será enviada para o Globus. Assim, evitamos que um item incremente apontamente e
                // gere uma nova O.S.
                itensOsIncrementaQtdApontamentos.forEach(item -> {
                    checklistItensNokGlobus.getPerguntasNok().removeIf(pergunta -> {
                        for (final AlternativaNokGlobus alternativa : pergunta.getAlternativasNok()) {
                            if (item.getCodContextoPergunta() == pergunta.getCodContextoPerguntaNok() &&
                                    item.getCodContextoAlternativa() == alternativa.getCodContextoAlternativaNok()) {
                                return true;
                            }
                        }
                        return false;
                    });
                });
            }

            // Pode acontecer de o checklist ter itens NOK apontados, porém, ou estes itens não devem abrir O.S ou
            // eles já estão abertos em outra O.S e não precisam ser lançados na integração. Para essa situação
            // consideramos que o checklist não precisa mais ser sincronizado.
            // Outra situação que pode ocorrer onde este if se torna necessário é a alteração de um modelo de checklist
            // onde alternativas que deveriam abrir O.S passam a não abrir mais, neste ponto, o checklist tem itens NOK
            // apontados na realização, porém nenhum deles devem configurar uma nova O.S neste momento.
            if (checklistItensNokGlobus.getPerguntasNok().size() <= 0) {
                // Marca checklist como não precisa ser sincronizado.
                sistema.marcaChecklistNaoPrecisaSincronizar(conn, codChecklistProLog);
                conn.commit();
                // Avisamos que a sincronia não foi necessária para o checklist em questão.
                if (listener != null) {
                    listener.onSincroniaNaoExecutada(codChecklistProLog, isLastChecklist);
                }
                return;
            }

            // IMPORTANTE: Deixamos essas duas linhas antes de enviar a requisição para Globus, assim evitamos que,
            // após enviar ao Globus ocorra um erro e o ProLog fique com um estado do dado e o Globus com outro.
            // Assumimos que a requisição será sucesso assim, salvamos quais foram os itens enviados para poder
            // consultar. Se a requisição para o Globus retornar erro, será feito rollback e será como se esse código
            // não tivesse executado.
            sistema.insertItensNokEnviadosGlobus(conn, checklistItensNokGlobus);
            // Também marcamos o checklist como sincronizado, pois as informações já estão no sistema integrado.
            sistema.marcaChecklistSincronizado(conn, codChecklistProLog);

            final Long codOsAbertaGlobus =
                    requester.insertItensNok(GlobusPiccoloturConverter.convert(checklistItensNokGlobus));
            if (codOsAbertaGlobus <= 0) {
                throw new GlobusPiccoloturException("[ERRO INTEGRAÇÃO]: Globus retornou um código de O.S inválido");
            }

            conn.commit();
            // Avismos que os itens foram sincronizados com sucesso.
            if (listener != null) {
                listener.onSincroniaOk(codChecklistProLog, isLastChecklist);
            }
        } catch (final Throwable t) {
            try {
                // Se tivemos um erro ao logar o checklist, precisamos logar para saber como proceder na solução do
                // erro e conseguir sincronizar esse checklist.
                sistema.erroAoSicronizarChecklist(codChecklistProLog, getErrorMessage(t));
                // Avisamos sobre o erro ao sincronizar o checklist.
                if (listener != null) {
                    listener.onErroSincronia(codChecklistProLog, isLastChecklist, t);
                }
                if (conn != null) {
                    conn.rollback();
                }
                throw t;
            } catch (final Throwable ignored) {
                // Here you die, quietly! Indeed, i don't know what to do.
            }
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @NotNull
    private List<InfosAlternativaAberturaOrdemServico> getItensIncrementaApontamentos(
            @NotNull final Map<Long, List<InfosAlternativaAberturaOrdemServico>> alternativasStatus,
            @NotNull final List<PerguntaNokGlobus> perguntasNokGlobus) {
        final List<InfosAlternativaAberturaOrdemServico> itensOsIncrementaQtdApontamentos = new ArrayList<>();
        for (final PerguntaNokGlobus pergunta : perguntasNokGlobus) {
            for (final AlternativaNokGlobus alternativa : pergunta.getAlternativasNok()) {
                final List<InfosAlternativaAberturaOrdemServico> infosAlternativaAberturaOrdemServicos =
                        alternativasStatus.get(alternativa.getCodAlternativaNok());
                if (infosAlternativaAberturaOrdemServicos != null
                        && infosAlternativaAberturaOrdemServicos.size() > 0
                        && infosAlternativaAberturaOrdemServicos.get(0).isDeveAbrirOrdemServico()
                        && infosAlternativaAberturaOrdemServicos.get(0).getQtdApontamentosItem() > 0) {
                    itensOsIncrementaQtdApontamentos.add(infosAlternativaAberturaOrdemServicos.get(0));
                }
            }
        }
        return itensOsIncrementaQtdApontamentos;
    }

    @NotNull
    private String getErrorMessage(@NotNull final Throwable t) {
        String errorMessage = "Erro não mapeado. Algo deu errado na integração ao enviar os itens NOK.";
        if (t instanceof ProLogException) {
            errorMessage = ((ProLogException) t).getMessage();
        } else if (t instanceof SQLException || t instanceof IllegalStateException) {
            errorMessage = "Erro Interno. Algo deu errado ao processar o envio localmente.";
        } else if (t instanceof TimeoutException) {
            errorMessage = "Erro no Globus. O Globus não respondeu a mensagem a tempo.";
        }
        return errorMessage;
    }
}
