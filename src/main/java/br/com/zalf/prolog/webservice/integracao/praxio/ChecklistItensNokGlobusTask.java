package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.TipoOutrosSimilarityFinder;
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
import org.simmetrics.metrics.StringMetrics;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Created on 01/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ChecklistItensNokGlobusTask implements Runnable {
    private static final String TAG = ChecklistItensNokGlobusTask.class.getSimpleName();
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

            // Apenas por garantia, verificamos novamente se ?? necess??rio enviar alguma coisa para a integra????o.
            if (checklistItensNokGlobus.getPerguntasNok().size() <= 0) {
                // Marca checklist como n??o precisa ser sincronizado.
                sistema.marcaChecklistNaoPrecisaSincronizar(conn, codChecklistProLog);
                conn.commit();
                // Avisamos que a sincronia n??o foi necess??ria para o checklist em quest??o.
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
                                    checklistToSyncGlobus.getCodVeiculoChecklist());
            final List<InfosAlternativaAberturaOrdemServico> itensOsIncrementaQtdApontamentos =
                    getItensIncrementaApontamentos(alternativasStatus, checklistItensNokGlobus.getPerguntasNok());

            // Verificamos se tem algum item que deve incrementar a quantidade de apontamentos em alguma O.S.
            if (!itensOsIncrementaQtdApontamentos.isEmpty()) {
                Injection
                        .provideOrdemServicoDao()
                        .incrementaQtdApontamentos(conn, codChecklistProLog, itensOsIncrementaQtdApontamentos);

                // Ap??s incrementar a quantidade de apontamento das alternativas necess??rias, removemos elas da lista de
                // alternativas que ser??o enviadas para o Globus. Assim, evitamos que um item incremente apontamento e
                // gere uma nova O.S.
                itensOsIncrementaQtdApontamentos.forEach(
                        item -> checklistItensNokGlobus.getPerguntasNok().forEach(
                                pergunta -> pergunta.getAlternativasNok().removeIf(
                                        alternativa -> item.contains(pergunta.getCodContextoPerguntaNok(),
                                                                     alternativa.getCodContextoAlternativaNok()))));

                // Pode acontecer de a pergunta ficar sem nenhuma alternativa agora. Removemos ela nesse caso.
                checklistItensNokGlobus.getPerguntasNok().removeIf(pergunta -> pergunta.getAlternativasNok().isEmpty());
            }

            // Pode acontecer de o checklist ter itens NOK apontados, por??m, ou estes itens n??o devem abrir O.S. ou
            // eles j?? est??o abertos em outra O.S. e n??o precisam ser lan??ados na integra????o. Para essa situa????o
            // consideramos que o checklist n??o precisa mais ser sincronizado.
            // Outra situa????o que pode ocorrer onde este if se torna necess??rio ?? a altera????o de um modelo de checklist
            // onde alternativas que deveriam abrir O.S. passam a n??o abrir mais, neste ponto, o checklist tem itens NOK
            // apontados na realiza????o, por??m nenhum deles devem configurar uma nova O.S. neste momento.
            if (checklistItensNokGlobus.getPerguntasNok().size() <= 0) {
                // Marca checklist como n??o precisa ser sincronizado.
                sistema.marcaChecklistNaoPrecisaSincronizar(conn, codChecklistProLog);
                conn.commit();
                // Avisamos que a sincronia n??o foi necess??ria para o checklist em quest??o.
                if (listener != null) {
                    listener.onSincroniaNaoExecutada(codChecklistProLog, isLastChecklist);
                }
                return;
            }

            // IMPORTANTE: Deixamos essas duas linhas antes de enviar a requisi????o para Globus, assim evitamos que,
            // ap??s enviar ao Globus ocorra um erro e o ProLog fique com um estado do dado e o Globus com outro.
            // Assumimos que a requisi????o ser?? sucesso assim, salvamos quais foram os itens enviados para poder
            // consultar. Se a requisi????o para o Globus retornar erro, ser?? feito rollback e ser?? como se esse c??digo
            // n??o tivesse executado.
            sistema.insertItensNokEnviadosGlobus(conn, checklistItensNokGlobus);
            // Tamb??m marcamos o checklist como sincronizado, pois as informa????es j?? est??o no sistema integrado.
            sistema.marcaChecklistSincronizado(conn, codChecklistProLog);

            final Long codOsAbertaGlobus =
                    requester.insertItensNok(GlobusPiccoloturConverter.convert(checklistItensNokGlobus));
            if (codOsAbertaGlobus <= 0) {
                throw new GlobusPiccoloturException("[ERRO INTEGRA????O]: Globus retornou um c??digo de O.S inv??lido");
            }

            // Precisamos que esse commit seja feito apenas ap??s a sincronia com o Globus, para que possamos fazer
            // rollback das informa????es com seguran??a e evitar incompatibilidade das informa????es.
            conn.commit();
            // Avismos que os itens foram sincronizados com sucesso.
            if (listener != null) {
                listener.onSincroniaOk(codChecklistProLog, isLastChecklist);
            }
        } catch (final Throwable throwable) {
            // N??o logamos mais exception no Sentry para n??o exceder a contagem do plano.
            Log.e(TAG, "Erro ao tentar sincronizar o checklist com o Globus", throwable);
            try {
                // Se tivemos um erro ao sincronizar o checklist, precisamos logar para saber como proceder na solu????o
                // do erro e conseguir sincronizar esse checklist.
                if (conn != null) {
                    // IMPORTANTE: ?? necess??rio que o 'conn.rollback()' seja executado antes da chamada do sistema,
                    // para liberar todas as tabelas e n??o termos deadlock.
                    // O rollback ir?? desfazer as altera????es e tamb??m liberar todos os Locks nas tabelas, assim
                    // poderemos salvar o log de erro recebido sem nenhum problema.
                    conn.rollback();
                    try {
                        sistema.erroAoSicronizarChecklist(
                                conn,
                                codChecklistProLog,
                                getErrorMessage(throwable),
                                throwable);
                        // Ap??s sincronizar os erros, podemos commitar a connection pois ela n??o ser?? mais utilizada.
                        conn.commit();
                    } catch (final Throwable error) {
                        // Caso ocorra algum erro ao salvar os logs de erro, fazemos rollback tamb??m.
                        conn.rollback();
                        Log.e(TAG, "Erro ao salvar mensagem de erro ao sincronizar checklist", error);
                        throw error;
                    }
                } else {
                    Log.d(TAG, "Connection nula, nada foi logado");
                }
                // Avisamos sobre o erro ao sincronizar o checklist.
                if (listener != null) {
                    listener.onErroSincronia(codChecklistProLog, isLastChecklist, throwable);
                }
            } catch (final Throwable t) {
                // Here you die, quietly! Indeed, i don't know what to do.
                Log.e(TAG, "Algo deu errado no fluxo de rollback da sincronia de checklist", t);
            }
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @NotNull
    private List<InfosAlternativaAberturaOrdemServico> getItensIncrementaApontamentos(
            @NotNull final Map<Long, List<InfosAlternativaAberturaOrdemServico>> alternativasStatus,
            @NotNull final List<PerguntaNokGlobus> perguntasNokGlobus) {
        final TipoOutrosSimilarityFinder similarityFinder = new TipoOutrosSimilarityFinder(StringMetrics.jaro());
        final List<InfosAlternativaAberturaOrdemServico> itensOsIncrementaQtdApontamentos = new ArrayList<>();
        for (final PerguntaNokGlobus pergunta : perguntasNokGlobus) {
            for (final AlternativaNokGlobus alternativa : pergunta.getAlternativasNok()) {
                final List<InfosAlternativaAberturaOrdemServico> infosAlternativaAberturaOrdemServicos =
                        alternativasStatus.get(alternativa.getCodAlternativaNok());
                // Esse IF ?? respons??vel por identificar se um item apontado como NOK deve incrementar a quantidade de
                // apontamentos. Fazemos isso verificando os seguintes pontos:
                // 1 - Caso a alternativa est?? configurada para abrir OS (isDeveAbrirOrdemServico).
                // 2 - Caso essa alternativa j?? est?? aberta em uma OS (getQtdApontamentosItem).
                // 3 - Caso a alternativa N??O ?? tipo outros (~isAlternativaTipoOutros) OU caso for tipo outros tenha
                // similaridade de texto (hasSimilarity).
                if (infosAlternativaAberturaOrdemServicos != null
                        && infosAlternativaAberturaOrdemServicos.size() > 0
                        && infosAlternativaAberturaOrdemServicos.get(0).isDeveAbrirOrdemServico()
                        && infosAlternativaAberturaOrdemServicos.get(0).getQtdApontamentosItem() > 0) {
                    if (alternativa.isAlternativaTipoOutros()) {
                        final Optional<InfosAlternativaAberturaOrdemServico> bestMatch =
                                similarityFinder.findBestMatch(
                                        alternativa.getDescricaoAlternativaNok(),
                                        infosAlternativaAberturaOrdemServicos);
                        bestMatch.ifPresent(itensOsIncrementaQtdApontamentos::add);
                    } else {
                        itensOsIncrementaQtdApontamentos.add(infosAlternativaAberturaOrdemServicos.get(0));
                    }
                }
            }
        }
        return itensOsIncrementaQtdApontamentos;
    }

    @NotNull
    private String getErrorMessage(@NotNull final Throwable t) {
        String errorMessage = "Erro n??o mapeado. Algo deu errado na integra????o ao enviar os itens NOK.";
        if (t instanceof ProLogException) {
            errorMessage = ((ProLogException) t).getMessage();
        } else if (t instanceof SQLException || t instanceof IllegalStateException) {
            errorMessage = "Erro Interno. Algo deu errado ao processar o envio localmente.";
            Log.e(TAG, errorMessage, t);
        } else if (t instanceof TimeoutException) {
            errorMessage = "Erro no Globus. O Globus n??o respondeu a mensagem a tempo.";
            Log.e(TAG, errorMessage, t);
        }
        return errorMessage;
    }
}
