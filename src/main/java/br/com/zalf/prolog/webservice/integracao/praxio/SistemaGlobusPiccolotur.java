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
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturRequester;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturMovimentacaoResponse;
import br.com.zalf.prolog.webservice.integracao.praxio.data.SistemaGlobusPiccoloturDao;
import br.com.zalf.prolog.webservice.integracao.praxio.data.SistemaGlobusPiccoloturDaoImpl;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.MetodoIntegrado;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.LocalDateTime;
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
                                      final boolean statusAtivo) throws Throwable {
        // Ignoramos o statusAtivo repassado pois queremos forçar que o modelo de checklist tenha o statusAtivo = false.
        getIntegradorProLog().insertModeloChecklist(modeloChecklist, checklistOfflineListener, false);
    }

    @Override
    public void updateModeloChecklist(@NotNull final String token,
                                      @NotNull final Long codUnidade,
                                      @NotNull final Long codModelo,
                                      @NotNull final ModeloChecklistEdicao modeloChecklist,
                                      @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                                      final boolean sobrescreverPerguntasAlternativas) throws Throwable {
        // Ignoramos a propriedade sobrescreverPerguntasAlternativas pois queremos que para essa integração todas as
        // edições de perguntas e alternativas sobrescrevam os valores antigos sem alterar os códigos existentes.
        getIntegradorProLog()
                .updateModeloChecklist(
                        token,
                        codUnidade,
                        codModelo,
                        modeloChecklist,
                        checklistOfflineListener,
                        true);
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
    @Override
    public Long insert(@NotNull final ServicoDao servicoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final LocalDateTime dataHoraMovimentacao,
                       final boolean fecharServicosAutomaticamente) throws Throwable {
        // Garantimos que apenas movimentações válidas foram feitas para essa integração.
        for (final Movimentacao movimentacao : processoMovimentacao.getMovimentacoes()) {
            if (!movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.DESCARTE)
                    && !movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO)
                    && !movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ESTOQUE)
                    && !movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.VEICULO)) {
                if (movimentacao.isFrom(OrigemDestinoEnum.ANALISE)) {
                    // Adaptamos o texto de retorno para o cenário onde a origem é Análise.
                    throw new BloqueadoIntegracaoException(
                            String.format(
                                    "ERRO!\nVocê está tentando mover um pneu da %s para o %s.\n" +
                                            "Essa opção de movimentação ainda está sendo integrada",
                                    OrigemDestinoEnum.ANALISE.asString(),
                                    movimentacao.getDestino().getTipo().asString()));
                } else if (movimentacao.isTo(OrigemDestinoEnum.ANALISE)) {
                    // Adaptamos o texto de retorno para o cenário onde o destino é Análise.
                    throw new BloqueadoIntegracaoException(
                            String.format(
                                    "ERRO!\nVocê está tentando mover um pneu do %s para a %s.\n" +
                                            "Essa opção de movimentação ainda está sendo integrada",
                                    movimentacao.getOrigem().getTipo().asString(),
                                    OrigemDestinoEnum.ANALISE.asString()));
                } else {
                    throw new BloqueadoIntegracaoException(
                            "ERRO!\nVocê está tentando realizar uma movimentação que ainda não está integrada");
                }
            }
        }

        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);
            final Long codMovimentacao =
                    Injection
                            .provideMovimentacaoDao()
                            .insert(conn,
                                    servicoDao,
                                    processoMovimentacao,
                                    dataHoraMovimentacao,
                                    fecharServicosAutomaticamente);
            final long codUnidade = processoMovimentacao.getUnidade().getCodigo();
            final GlobusPiccoloturMovimentacaoResponse response = requester.insertProcessoMovimentacao(
                    getIntegradorProLog().getUrl(
                            getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(codUnidade),
                            getSistemaKey(),
                            MetodoIntegrado.INSERT_MOVIMENTACAO),
                    GlobusPiccoloturConverter.convert(processoMovimentacao, dataHoraMovimentacao));
            if (!response.isSucesso()) {
                throw new GlobusPiccoloturException(
                        "[INTEGRAÇÃO] Erro ao movimentar pneus no sistema integrado\n" + response.getPrettyErrors());
            }
            conn.commit();
            return codMovimentacao;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @NotNull
    private SistemaGlobusPiccoloturDao getSistemaGlobusPiccoloturDaoImpl() {
        return new SistemaGlobusPiccoloturDaoImpl();
    }
}
