package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.PrologUtils;
import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoDao;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.customfields._model.TipoCampoPersonalizado;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.InfosChecklistInserido;
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
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.ModelosChecklistBloqueados;
import br.com.zalf.prolog.webservice.integracao.praxio.data.*;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimento;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimentoResponse;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.OffsetDateTime;
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
                                   @NotNull final RecursoIntegrado recursoIntegrado,
                                   @NotNull final IntegradorProLog integradorProLog,
                                   @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, recursoIntegrado, userToken);
        this.requester = requester;
    }

    @NotNull
    @Override
    public ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String token) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(modeloChecklist.getCodUnidade())) {
            // Ignoramos o statusAtivo, pois queremos for??ar que o modelo de checklist tenha o statusAtivo = false.
            return getIntegradorProLog()
                    .insertModeloChecklist(modeloChecklist, checklistOfflineListener, false, token);
        }
        // Direcionamos a requisi????o normalmente para o Prolog.
        return getIntegradorProLog()
                .insertModeloChecklist(modeloChecklist, checklistOfflineListener, statusAtivo, token);
    }

    @Override
    public void updateModeloChecklist(@NotNull final Long codUnidade,
                                      @NotNull final Long codModelo,
                                      @NotNull final ModeloChecklistEdicao modeloChecklist,
                                      @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                                      final boolean podeMudarCodigoContextoPerguntasEAlternativas,
                                      @NotNull final String token) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(modeloChecklist.getCodUnidade())) {
            // Ignoramos a propriedade sobrescreverPerguntasAlternativas pois queremos que para essa integra????o todas as
            // edi????es de perguntas e alternativas sobrescrevam os valores antigos sem alterar os c??digos existentes.
            getIntegradorProLog()
                    .updateModeloChecklist(
                            codUnidade,
                            codModelo,
                            modeloChecklist,
                            checklistOfflineListener,
                            false,
                            token);
            return;
        }
        // Direcionamos a requisi????o normalmente para o Prolog.
        getIntegradorProLog()
                .updateModeloChecklist(
                        codUnidade,
                        codModelo,
                        modeloChecklist,
                        checklistOfflineListener,
                        podeMudarCodigoContextoPerguntasEAlternativas,
                        token);
    }

    @NotNull
    @Override
    public InfosChecklistInserido insertChecklist(@NotNull final ChecklistInsercao checklistNew,
                                                  final boolean foiOffline,
                                                  final boolean deveAbrirOs) throws Throwable {
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        Connection conn = null;
        try {
            // Devemos enviar para o Globus apenas o modelo de checklist existe na tabela de integra????o.
            // Verifica se modelo informado existe na tabela.
            final boolean deveEnviarParaGlobus
                    = verificaModeloChecklistIntegrado(checklistNew.getCodUnidade(), checklistNew.getCodModelo());

            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);
            // Insere checklist na base de dados do ProLog
            // Se deve enviar para o Globus, ent??o n??o abrimos O.S pois ela vir?? da integra????o.
            final InfosChecklistInserido infosChecklistInserido = Injection
                    .provideChecklistDao()
                    .insertChecklist(conn, checklistNew, foiOffline, !deveEnviarParaGlobus);
            final Long codChecklistProLog = infosChecklistInserido.getCodChecklist();

            // Se a unidade est?? com a integra????o desativada ou n??o devemos enviar para o Globus, ent??o retornamos.
            if (!unidadeEstaComIntegracaoAtiva(checklistNew.getCodUnidade()) || !deveEnviarParaGlobus) {
                return infosChecklistInserido;
            }

            // Se o checklist tem pelo menos um item NOK, precisamos disparar o envio para a integra????o.
            if (checklistNew.getQtdAlternativasNok() > 0) {
                // Marcamos que o checklist precisa ser sincronizado. Isso ser?? ??til para que o processamento disparado
                // pelo agendador consiga distinguir quais checklists s??o necess??rios serem sincronizados.
                getSistemaGlobusPiccoloturDaoImpl().insertItensNokPendentesParaSincronizar(conn, codChecklistProLog);
                // Precisamos realizar o commit antes de executar a thread, para evitar problemas de concorr??ncia ao
                // acessar uma tabela que foi alterada pela connection, por??m os dados ainda n??o commitados.
                conn.commit();
                // Faremos o processamento de envio dos itens NOK noutra thread para que o usu??rio que est?? realizando
                // o checklist possa seguir seu rumo naturalmente.
                Executors.newSingleThreadExecutor().execute(
                        new ChecklistItensNokGlobusTask(
                                codChecklistProLog,
                                true,
                                getSistemaGlobusPiccoloturDaoImpl(),
                                requester,
                                null));
            } else {
                // Caso n??o precisamos processar nenhum envio, apenas fechamos a connection para garantir que tudo que
                // foi executado ser?? salvo.
                conn.commit();
            }
            return infosChecklistInserido;
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
    public InfosChecklistInserido insertChecklistOffline(@NotNull final ChecklistInsercao checklist) throws Throwable {
        return insertChecklist(checklist, true, false);
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(item.getCodUnidadeOrdemServico())) {
            throw new BloqueadoIntegracaoException(
                    "O fechamento de itens de O.S. integrados dever?? ser feito apenas pelo Sistema Globus");
        }
        // Se a unidade n??o possui integra????o direcionamos para o Prolog.
        getIntegradorProLog().resolverItem(item);
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(itensResolucao.getCodUnidadeOrdemServico())) {
            throw new BloqueadoIntegracaoException(
                    "O fechamento de itens de O.S. integrados dever?? ser feito apenas pelo Sistema Globus");
        }
        // Se a unidade n??o possui integra????o direcionamos para o Prolog.
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
            // N??o tem integra????o, vamos para o fluxo normal do Prolog.
            return getIntegradorProLog()
                    .insert(servicoDao,
                            campoPersonalizadoDao,
                            processoMovimentacao,
                            dataHoraMovimentacao,
                            fecharServicosAutomaticamente);
        }
        // Temos a integra????o ativa, executamos o fluxo integrado.
        // Garantimos que apenas movimenta????es v??lidas foram feitas para essa integra????o.
        for (final Movimentacao movimentacao : processoMovimentacao.getMovimentacoes()) {
            if (!movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.DESCARTE)
                    && !movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO)
                    && !movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ESTOQUE)
                    && !movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.VEICULO)) {
                if (movimentacao.isFrom(OrigemDestinoEnum.ANALISE)) {
                    // Adaptamos o texto de retorno para o cen??rio onde a origem ?? An??lise.
                    throw new BloqueadoIntegracaoException(
                            String.format(
                                    "ERRO!\nVoc?? est?? tentando mover um pneu da %s para o %s.\n" +
                                            "Essa op????o de movimenta????o ainda n??o est?? integrada",
                                    OrigemDestinoEnum.ANALISE.asString(),
                                    movimentacao.getDestino().getTipo().asString()));
                } else if (movimentacao.isTo(OrigemDestinoEnum.ANALISE)) {
                    // Adaptamos o texto de retorno para o cen??rio onde o destino ?? An??lise.
                    throw new BloqueadoIntegracaoException(
                            String.format(
                                    "ERRO!\nVoc?? est?? tentando mover um pneu do %s para a %s.\n" +
                                            "Essa op????o de movimenta????o ainda est?? sendo integrada",
                                    movimentacao.getOrigem().getTipo().asString(),
                                    OrigemDestinoEnum.ANALISE.asString()));
                } else {
                    throw new BloqueadoIntegracaoException(
                            "ERRO!\nVoc?? est?? tentando realizar uma movimenta????o que ainda n??o est?? integrada");
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
                    requester.getTokenAutenticacaoIntegracao(autenticacaoHolder);
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

            // Buscamos no Globus as informa????es dos locais de movimentos para inserir nas movimenta????es.
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    getIntegradorProLog()
                            .getApiAutenticacaoHolder(
                                    conn,
                                    codEmpresa,
                                    getSistemaKey(),
                                    MetodoIntegrado.GET_LOCAIS_DE_MOVIMENTO);
            final GlobusPiccoloturLocalMovimentoResponse globusResponse = requester.getLocaisMovimentoGlobusResponse(
                    apiAutenticacaoHolder,
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

            // ?? necess??rio transferir os pneus apenas se a unidade onde a movimenta????o foi feita ?? diferente do que
            // a unidade onde o usu??rio est??.
            // Ambos os c??digos de unidade s??o c??digos do Prolog.
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

            final ApiAutenticacaoHolder apiAutenticacaoHolderInsertMovimentacao =
                    getIntegradorProLog()
                            .getApiAutenticacaoHolder(
                                    conn,
                                    codEmpresa,
                                    getSistemaKey(),
                                    MetodoIntegrado.INSERT_MOVIMENTACAO);
            //noinspection ConstantConditions
            final GlobusPiccoloturMovimentacaoResponse response = requester.insertProcessoMovimentacao(
                    apiAutenticacaoHolderInsertMovimentacao,
                    autenticacaoResponse.getFormattedBearerToken(),
                    // Convertemos a dataHoraMovimentacao para LocalDateTime pois usamos assim na integra????o.
                    GlobusPiccoloturConverter.convert(
                            codUnidadeMovimento,
                            globusResponse.getUsuarioGlobus(),
                            localMovimentoGlobus,
                            processoMovimentacao,
                            dataHoraMovimentacao.toLocalDateTime()));
            if (!response.isSucesso()) {
                throw new GlobusPiccoloturException(
                        "[INTEGRA????O] Erro ao movimentar pneus no sistema integrado\n" + response.getPrettyErrors());
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
            // Se a unidade n??o est?? integrada, ent??o retornamos o campo padr??o, buscado pelo Prolog.
            return camposParaRealizacaoMovimentacao;
        }

        if (camposParaRealizacaoMovimentacao.isEmpty()) {
            throw new GlobusPiccoloturException("Nenhum campo personalizado dispon??vel");
        }
        // Buscamos o campo LISTA_SELE????O, se n??o encontramos nada, lan??amos uma exception, pois ?? obrigat??rio existir.
        final CampoPersonalizadoParaRealizacao oldCampoSelecaoLocalMovimento =
                camposParaRealizacaoMovimentacao
                        .stream()
                        .filter(campo -> campo.getTipoCampo().equals(TipoCampoPersonalizado.LISTA_SELECAO))
                        .findFirst()
                        .orElseThrow(() -> {
                            throw new GlobusPiccoloturException("Nenhum campo de Lista de Sele????o dispon??vel");
                        });

        // Fluxo integrado, direcionamos a requisi????o para a Praxio.
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
                    requester.getTokenAutenticacaoIntegracao(autenticacaoHolder);

            final String cpfColaborador =
                    PrologUtils.isDebug()
                            ? GlobusPiccoloturConstants.CPF_COLABORADOR_LOCAIS_MOVIMENTO
                            : getIntegradorProLog().getColaboradorByToken(getUserToken()).getCpfAsString();
            final ApiAutenticacaoHolder apiAutenticacaoHolder = getIntegradorProLog()
                    .getApiAutenticacaoHolder(
                            conn,
                            codEmpresa,
                            getSistemaKey(),
                            MetodoIntegrado.GET_LOCAIS_DE_MOVIMENTO);
            final List<GlobusPiccoloturLocalMovimento> locaisMovimentoGlobus =
                    requester.getLocaisMovimentoGlobusResponse(
                            apiAutenticacaoHolder,
                            autenticacaoResponse.getFormattedBearerToken(),
                            cpfColaborador)
                            .getLocais();

            // Os locais de movimento j?? s??o validados no request, n??o chegaram null aqui.
            @SuppressWarnings("ConstantConditions")
            final CampoPersonalizadoParaRealizacao novoCampoSelecaoLocalMovimento =
                    GlobusPiccoloturConverter.convert(oldCampoSelecaoLocalMovimento, locaisMovimentoGlobus);
            // Removemos o campo de sel????o antigo.
            camposParaRealizacaoMovimentacao.remove(oldCampoSelecaoLocalMovimento);
            // Adicionamos o novo campo de sele????o, esse cont??m as op????es que foram buscadas do Globus.
            // Adicionamos ele no in??cio, para que seja a primeira informa????o que o usu??rio preencha.
            camposParaRealizacaoMovimentacao.add(0, novoCampoSelecaoLocalMovimento);
            return camposParaRealizacaoMovimentacao;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    public boolean verificaModeloChecklistIntegrado(@NotNull final Long codUnidade,
                                                    @NotNull final Long codModeloChecklist) throws Throwable {
        final ModelosChecklistBloqueados modelosChecklistBloqueados = Injection
                .provideIntegracaoDao()
                .getModelosChecklistBloqueados(codUnidade);
        return !modelosChecklistBloqueados.getCodModelosBloqueados().contains(codModeloChecklist);
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
                "Transfer??ncia gerada a partir da movimenta????o de pneus integrada");
    }

    private boolean unidadeEstaComIntegracaoAtiva(@NotNull final Long codUnidade) throws Throwable {
        // Caso o c??digo da unidade est?? contido na lista de unidades bloqueadas, significa que a unidade
        // N??O EST?? integrada.
        return !getIntegradorProLog()
                .getCodUnidadesIntegracaoBloqueada(getUserToken(), getSistemaKey(), getRecursoIntegrado())
                .contains(codUnidade);
    }

    @NotNull
    private SistemaGlobusPiccoloturDao getSistemaGlobusPiccoloturDaoImpl() {
        return new SistemaGlobusPiccoloturDaoImpl();
    }
}
