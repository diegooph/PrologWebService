package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoDao;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.praxio.data.*;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.OffsetDateTime;
import java.util.concurrent.Executors;

/**
 * Created on 01/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaGlobusPiccolotur extends Sistema {
    @NotNull
    private static final Long COD_MODELO_LIBERADO = 501L;
    @NotNull
    private static final Long COD_UNIDADE_LIBERADA = 107L;
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
    public Long insertChecklist(@NotNull final ChecklistInsercao checklistNew,
                                final boolean foiOffline,
                                final boolean deveAbrirOs) throws Throwable {
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        Connection conn = null;
        try {
            // Devemos enviar para o Globus apenas se for o modelo 501 e a unidade 107, pois foram apenas estas
            // liberadas nesse primeiro momento, onde faremos um teste.
            final boolean deveEnviarParaGlobus =
                    checklistNew.getCodModelo().equals(COD_MODELO_LIBERADO)
                            && checklistNew.getCodUnidade().equals(COD_UNIDADE_LIBERADA);
            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);
            // Insere checklist na base de dados do ProLog
            // Se deve enviar para o Globus, então não abrimos O.S pois ela virá da integração.
            final Long codChecklistProLog = Injection
                    .provideChecklistDao()
                    .insert(conn, checklistNew, foiOffline, !deveEnviarParaGlobus);

            // Se não devemos enviar para o Globus, então retornamos. Já fizemos tudo o que deveríamos!
            if (!deveEnviarParaGlobus) {
                return codChecklistProLog;
            }

            // Se o checklist tem pelo menos um item NOK, precisamos disparar o envio para a integração.
            if (checklistNew.getQtdAlternativasNok() > 0) {
                // Marcamos que o checklist precisa ser sincronizado. Isso será útil para que o processamento disparado
                // pelo agendador consiga distinguir quais checklists são necessários serem sincronizados.
                getSistemaGlobusPiccoloturDaoImpl().insertItensNokPendentesParaSincronizar(conn, codChecklistProLog);
                // Precisamos realizar o commit antes de executar a thread, para evitar problemas de concorrência ao
                // acessar uma tabela que foi alterada pela connection, porém os dados ainda não commitados.
                conn.commit();
                // Faremos o processamento de envio dos itens NOK noutra thread para que o usuário que está realizando
                // o checklist possa seguir seu rumo naturalmente.
                Executors.newSingleThreadExecutor().execute(
                        new ChecklistItensNokGlobusTask(
                                codChecklistProLog,
                                true,
                                getSistemaGlobusPiccoloturDaoImpl(),
                                requester,
                                null));
            } else {
                // Caso não precisamos processar nenhum envio, apenas fechamos a connection para garantir que tudo que
                // foi executado será salvo.
                conn.commit();
            }
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

    @NotNull
    @Override
    public ResultInsertModeloChecklist insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist,
                                                             @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                                                             final boolean statusAtivo,
                                                             @NotNull final String token) throws Throwable {
        // Ignoramos o statusAtivo repassado pois queremos forçar que o modelo de checklist tenha o statusAtivo = false.
        return getIntegradorProLog().insertModeloChecklist(modeloChecklist, checklistOfflineListener, false, token);
    }

    @Override
    public void updateModeloChecklist(@NotNull final Long codUnidade,
                                      @NotNull final Long codModelo,
                                      @NotNull final ModeloChecklistEdicao modeloChecklist,
                                      @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                                      final boolean podeMudarCodigoContextoPerguntasEAlternativas,
                                      @NotNull final String token) throws Throwable {
        // Ignoramos a propriedade sobrescreverPerguntasAlternativas pois queremos que para essa integração todas as
        // edições de perguntas e alternativas sobrescrevam os valores antigos sem alterar os códigos existentes.
        getIntegradorProLog()
                .updateModeloChecklist(
                        codUnidade,
                        codModelo,
                        modeloChecklist,
                        checklistOfflineListener,
                        false,
                        token);
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
                       @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final OffsetDateTime dataHoraMovimentacao,
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
            final long codEmpresa =
                    getIntegradorProLog()
                            .getCodEmpresaByCodUnidadeProLog(conn, processoMovimentacao.getUnidade().getCodigo());
            final ApiAutenticacaoHolder autenticacaoHolder =
                    getIntegradorProLog()
                            .getApiAutenticacaoHolder(
                                    conn,
                                    codEmpresa,
                                    getSistemaKey(),
                                    MetodoIntegrado.GET_AUTENTICACAO);
            final GlobusPiccoloturAutenticacaoResponse autenticacaoResponse =
                    requester.getTokenAutenticacaoIntegracao(
                            autenticacaoHolder.getUrl(),
                            autenticacaoHolder.getApiTokenClient(),
                            autenticacaoHolder.getApiShortCode());
            final Long codMovimentacao =
                    Injection
                            .provideMovimentacaoDao()
                            .insert(conn,
                                    servicoDao,
                                    campoPersonalizadoDao,
                                    processoMovimentacao,
                                    dataHoraMovimentacao,
                                    fecharServicosAutomaticamente);
            final GlobusPiccoloturMovimentacaoResponse response = requester.insertProcessoMovimentacao(
                    getIntegradorProLog()
                            .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.INSERT_MOVIMENTACAO),
                    autenticacaoResponse.getFormattedBearerToken(),
                    // Convertemos a dataHoraMovimentacao para LocalDateTime pois usamos assim na integração.
                    GlobusPiccoloturConverter.convert(processoMovimentacao, dataHoraMovimentacao.toLocalDateTime()));
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
