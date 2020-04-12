package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.ProLogUtils;
import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoDao;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.customfields._model.TipoCampoPersonalizado;
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
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.praxio.data.*;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimento;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimentoResponse;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    public Long insertChecklist(@NotNull final ChecklistInsercao checklistNew,
                                final boolean foiOffline,
                                final boolean deveAbrirOs) throws Throwable {
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        Connection conn = null;
        try {
            // Devemos enviar para o Globus apenas o modelo de checklist existe na tabela de integração.
            // Verifica se modelo informado existe na tabela.
            final boolean deveEnviarParaGlobus = getSistemaGlobusPiccoloturDaoImpl()
                    .verificaModeloChecklistIntegrado(checklistNew.getCodUnidade(), checklistNew.getCodModelo());

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
    public ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
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
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        final boolean itemIntegrado =
                getSistemaGlobusPiccoloturDaoImpl().
                        verificaItensIntegrados(Collections.singletonList(item.getCodItemResolvido()));
        if (itemIntegrado) {
            throw new BloqueadoIntegracaoException(
                    "O fechamento de itens de O.S. integrados deverá ser feito apenas pelo Sistema Globus");
        }
        getIntegradorProLog().resolverItem(item);
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        final boolean itensIntegrados =
                getSistemaGlobusPiccoloturDaoImpl().verificaItensIntegrados(itensResolucao.getCodigosItens());
        if (itensIntegrados) {
            throw new BloqueadoIntegracaoException(
                    "O fechamento de itens de O.S. integrados deverá ser feito apenas pelo Sistema Globus");
        }
        getIntegradorProLog().resolverItens(itensResolucao);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final ServicoDao servicoDao,
                       @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final OffsetDateTime dataHoraMovimentacao,
                       final boolean fecharServicosAutomaticamente) throws Throwable {
        final Long codUnidadeOrigem = processoMovimentacao.getUnidade().getCodigo();
        if (!unidadeEstaComIntegracaoAtiva(codUnidadeOrigem)) {
            // Não tem integração, vamos para o fluxo normal do Prolog.
            return getIntegradorProLog()
                    .insert(servicoDao,
                            campoPersonalizadoDao,
                            processoMovimentacao,
                            dataHoraMovimentacao,
                            fecharServicosAutomaticamente);
        }
        // Temos a integração ativa, executamos o fluxo integrado.
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
                                            "Essa opção de movimentação ainda não está integrada",
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
                            .getCodEmpresaByCodUnidadeProLog(conn, codUnidadeOrigem);
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

            final Long codUnidadeMovimento =
                    GlobusPiccoloturUtils
                            .getCodUnidadeMovimentoFromCampoPersonalizado(
                                    processoMovimentacao.getRespostasCamposPersonalizados());

            // Buscamos no Globus as informações dos locais de movimentos para inserir nas movimentações.
            final String url =
                    getIntegradorProLog()
                            .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.GET_LOCAIS_DE_MOVIMENTO);
            final GlobusPiccoloturLocalMovimentoResponse globusResponse = requester.getLocaisMovimentoGlobusResponse(
                    url,
                    autenticacaoResponse.getFormattedBearerToken(),
                    processoMovimentacao.getColaborador().getCpfAsString());

            //noinspection ConstantConditions
            final GlobusPiccoloturLocalMovimento localMovimentoGlobus =
                    globusResponse.getLocais()
                            .stream()
                            .filter(local -> local.getCodUnidadeProlog().equals(codUnidadeMovimento))
                            .findAny()
                            .orElseThrow(() -> {
                                throw new GlobusPiccoloturException("Nenhum local de movimento encontrado!");
                            });

            // É necessário transferir os pneus apenas se a unidade onde a movimentação foi feita é diferente do que
            // a unidade onde o usuário está.
            // Ambos os códigos de unidade são códigos do Prolog.
            if (!codUnidadeOrigem.equals(codUnidadeMovimento)) {
                // Nesse caso devemos transferir os pneus em estoque para a unidade de movimento.
                final List<Movimentacao> movimentacoesEstoque = processoMovimentacao.getMovimentacoes()
                        .stream()
                        .filter(movimentacao -> movimentacao.isTo(OrigemDestinoEnum.ESTOQUE))
                        .collect(Collectors.toList());

                if (!movimentacoesEstoque.isEmpty()) {
                    final Long codColaborador = getIntegradorProLog().getColaboradorByToken(getUserToken()).getCodigo();
                    final PneuTransferenciaRealizacao pneuTransferencia =
                            createPneuTransferencia(
                                    codUnidadeOrigem,
                                    codUnidadeMovimento,
                                    codColaborador,
                                    movimentacoesEstoque);
                    Injection
                            .providePneuTransferenciaDao()
                            .insertTransferencia(
                                    conn,
                                    pneuTransferencia,
                                    dataHoraMovimentacao,
                                    false);
                }
            }

            //noinspection ConstantConditions
            final GlobusPiccoloturMovimentacaoResponse response = requester.insertProcessoMovimentacao(
                    getIntegradorProLog()
                            .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.INSERT_MOVIMENTACAO),
                    autenticacaoResponse.getFormattedBearerToken(),
                    // Convertemos a dataHoraMovimentacao para LocalDateTime pois usamos assim na integração.
                    GlobusPiccoloturConverter.convert(
                            codUnidadeMovimento,
                            globusResponse.getUsuarioGlobus(),
                            localMovimentoGlobus,
                            processoMovimentacao,
                            dataHoraMovimentacao.toLocalDateTime()));
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
    @Override
    public List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoMovimentacao(
            @NotNull final Long codUnidade,
            @NotNull final CampoPersonalizadoDao campoPersonalizadoDao) throws Throwable {
        final List<CampoPersonalizadoParaRealizacao> camposParaRealizacaoMovimentacao =
                campoPersonalizadoDao.getCamposParaRealizacaoMovimentacao(codUnidade);
        if (!unidadeEstaComIntegracaoAtiva(codUnidade)) {
            // Se a unidade não está integrada, então retornamos o campo padrão, buscado pelo Prolog.
            return camposParaRealizacaoMovimentacao;
        }

        if (camposParaRealizacaoMovimentacao.isEmpty()) {
            throw new GlobusPiccoloturException("Nenhum campo personalizado disponível");
        }
        // Buscamos o campo LISTA_SELEÇÃO, se não encontramos nada, lançamos uma exception, pois é obrigatório existir.
        final CampoPersonalizadoParaRealizacao oldCampoSelecaoLocalMovimento =
                camposParaRealizacaoMovimentacao
                        .stream()
                        .filter(campo -> campo.getTipoCampo().equals(TipoCampoPersonalizado.LISTA_SELECAO))
                        .findFirst()
                        .orElseThrow(() -> {
                            throw new GlobusPiccoloturException("Nenhum campo de Lista de Seleção disponível");
                        });

        // Fluxo integrado, direcionamos a requisição para a Praxio.
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final Long codEmpresa = oldCampoSelecaoLocalMovimento.getCodEmpresa();

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

            final String cpfColaborador =
                    ProLogUtils.isDebug()
                            ? GlobusPiccoloturConstants.CPF_COLABORADOR_LOCAIS_MOVIMENTO
                            : getIntegradorProLog().getColaboradorByToken(getUserToken()).getCpfAsString();
            final String url =
                    getIntegradorProLog()
                            .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.GET_LOCAIS_DE_MOVIMENTO);
            final List<GlobusPiccoloturLocalMovimento> locaisMovimentoGlobus =
                    requester.getLocaisMovimentoGlobusResponse(
                            url,
                            autenticacaoResponse.getFormattedBearerToken(),
                            cpfColaborador)
                            .getLocais();

            // Os locais de movimento já são validados no request, não chegaram null aqui.
            @SuppressWarnings("ConstantConditions") final CampoPersonalizadoParaRealizacao novoCampoSelecaoLocalMovimento =
                    GlobusPiccoloturConverter.convert(oldCampoSelecaoLocalMovimento, locaisMovimentoGlobus);
            // Removemos o campo de selção antigo.
            camposParaRealizacaoMovimentacao.remove(oldCampoSelecaoLocalMovimento);
            // Adicionamos o novo campo de seleção, esse contém as opções que foram buscadas do Globus.
            // Adicionamos ele no início, para que seja a primeira informação que o usuário preencha.
            camposParaRealizacaoMovimentacao.add(0, novoCampoSelecaoLocalMovimento);
            return camposParaRealizacaoMovimentacao;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @NotNull
    private PneuTransferenciaRealizacao createPneuTransferencia(@NotNull final Long codUnidadeOrigem,
                                                                @NotNull final Long codUnidadeMovimento,
                                                                @NotNull final Long codColaboradorMovimentacao,
                                                                @NotNull final List<Movimentacao> movimentacoesEstoque) {
        final List<Long> codPneusParaTransferir = movimentacoesEstoque.stream()
                .map(movimentacao -> movimentacao.getPneu().getCodigo())
                .collect(Collectors.toList());
        return new PneuTransferenciaRealizacao(
                codUnidadeOrigem,
                codUnidadeMovimento,
                codColaboradorMovimentacao,
                codPneusParaTransferir,
                "Transferência gerada a partir da movimentação de pneus integrada");
    }

    private boolean unidadeEstaComIntegracaoAtiva(@NotNull final Long codUnidade) throws Throwable {
        // Caso o código da unidade está contido na lista de unidades bloqueadas, significa que a unidade
        // NÃO ESTÁ integrada.
        return !getIntegradorProLog().getCodUnidadesIntegracaoBloqueada(getUserToken()).contains(codUnidade);
    }

    @NotNull
    private SistemaGlobusPiccoloturDao getSistemaGlobusPiccoloturDaoImpl() {
        return new SistemaGlobusPiccoloturDaoImpl();
    }
}
