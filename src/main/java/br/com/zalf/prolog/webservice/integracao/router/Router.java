package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.autenticacao.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoDao;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistResource;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.InfosChecklistInserido;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.VeiculoServico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.VeiculoAberturaServicoFiltro;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.InfosVeiculoEditado;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoDadosColetaKm;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemasFactoryOld;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoCreateDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Os Routers são as classes responsáveis por direcionar o fluxo de um request depois do mesmo atingir o servidor do
 * ProLog. O que ele faz, na verdade, é verificar se a empresa do usuário que faz o request possui integração no recurso
 * que está sendo consumido. Por exemplo, se um usuário da Avilan consome um método do {@link ChecklistResource}, como
 * esse recurso possui integração com alguma empresa, o request pode ir parar em um Router (depende o método). Esse
 * Router irá verificar se a Avilan possui integração com o {@link RecursoIntegrado#CHECKLIST} e, se possuir,
 * instanciará a subclasse de {@link Sistema} correta para processar a requisição.
 */
public abstract class Router implements OperacoesIntegradas {
    @NotNull
    private final IntegracaoDao integracaoDao;
    @NotNull
    private final IntegradorProLog integradorProLog;
    @NotNull
    private final String userToken;
    @NotNull
    private final RecursoIntegrado recursoIntegrado;
    @Nullable
    private SistemaKey sistemaKey;
    private boolean hasTried;

    Router(@NotNull final IntegracaoDao integracaoDao,
           @NotNull final IntegradorProLog integradorProLog,
           @NotNull final String userToken,
           @NotNull final RecursoIntegrado recursoIntegrado) {
        checkNotNull(userToken, "userToken não pode ser null!");
        this.integracaoDao = checkNotNull(integracaoDao, "integracaoDao não pode ser null!");
        this.integradorProLog = checkNotNull(integradorProLog, "integradorProLog não pode ser null!");
        this.userToken = TokenCleaner.getOnlyToken(userToken);
        this.recursoIntegrado = checkNotNull(recursoIntegrado, "recursoIntegrado não pode ser null!");
    }

    // #################################################################################################################
    // #################################################################################################################
    // ####################################### OPERAÇÕES INTEGRADAS - AFERIÇÃO #########################################
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getCronogramaAfericao(codUnidades);
        } else {
            return integradorProLog.getCronogramaAfericao(codUnidades);
        }
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final AfericaoBuscaFiltro afericaoBusca) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getNovaAfericaoPlaca(afericaoBusca);
        } else {
            return integradorProLog.getNovaAfericaoPlaca(afericaoBusca);
        }
    }

    @NotNull
    @Override
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getPneusAfericaoAvulsa(codUnidade);
        } else {
            return integradorProLog.getPneusAfericaoAvulsa(codUnidade);
        }
    }

    @NotNull
    @Override
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getNovaAfericaoAvulsa(codUnidade, codPneu, tipoMedicaoColetadaAfericao);
        } else {
            return integradorProLog.getNovaAfericaoAvulsa(codUnidade, codPneu, tipoMedicaoColetadaAfericao);
        }
    }

    @NotNull
    @Override
    public Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                                      @Nullable final Long codColaborador,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getAfericoesAvulsas(codUnidade, codColaborador, dataInicial, dataFinal);
        } else {
            return integradorProLog.getAfericoesAvulsas(codUnidade, codColaborador, dataInicial, dataFinal);
        }
    }

    @NotNull
    @Override
    public Afericao getAfericaoByCodigo(@NotNull final Long codUnidade,
                                        @NotNull final Long codAfericao) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getAfericaoByCodigo(codUnidade, codAfericao);
        } else {
            return integradorProLog.getAfericaoByCodigo(codUnidade, codAfericao);
        }
    }

    @NotNull
    @Override
    public List<AfericaoPlaca> getAfericoesPlacas(@NotNull final Long codUnidade,
                                                  @NotNull final String codTipoVeiculo,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal,
                                                  final int limit,
                                                  final long offset) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getAfericoesPlacas(
                    codUnidade,
                    codTipoVeiculo,
                    placaVeiculo,
                    dataInicial,
                    dataFinal,
                    limit,
                    offset);
        } else {
            return integradorProLog.getAfericoesPlacas(
                    codUnidade,
                    codTipoVeiculo,
                    placaVeiculo,
                    dataInicial,
                    dataFinal,
                    limit,
                    offset);
        }
    }

    @NotNull
    @Override
    public List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoAfericao(
            @NotNull final Long codUnidade,
            @NotNull final TipoProcessoColetaAfericao tipoProcessoColetaAfericao,
            @NotNull final CampoPersonalizadoDao campoPersonalizadoDao) throws Throwable {
        if (getSistema() != null) {
            return getSistema()
                    .getCamposParaRealizacaoAfericao(codUnidade, tipoProcessoColetaAfericao, campoPersonalizadoDao);
        } else {
            return integradorProLog
                    .getCamposParaRealizacaoAfericao(codUnidade, tipoProcessoColetaAfericao, campoPersonalizadoDao);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // ####################################### OPERAÇÕES INTEGRADAS - CHECKLIST ########################################
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    @Override
    public ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String userToken) throws Throwable {
        if (getSistema() != null) {
            return getSistema()
                    .insertModeloChecklist(modeloChecklist, checklistOfflineListener, statusAtivo, userToken);
        } else {
            return integradorProLog
                    .insertModeloChecklist(modeloChecklist, checklistOfflineListener, statusAtivo, userToken);
        }
    }

    @Override
    public void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean podeMudarCodigoContextoPerguntasEAlternativas,
            @NotNull final String userToken) throws Throwable {
        if (getSistema() != null) {
            getSistema().updateModeloChecklist(
                    codUnidade,
                    codModelo,
                    modeloChecklist,
                    checklistOfflineListener,
                    podeMudarCodigoContextoPerguntasEAlternativas,
                    userToken);
        } else {
            integradorProLog.updateModeloChecklist(
                    codUnidade,
                    codModelo,
                    modeloChecklist,
                    checklistOfflineListener,
                    podeMudarCodigoContextoPerguntasEAlternativas,
                    userToken);
        }
    }

    @NotNull
    @Override
    public List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(@NotNull final Long codUnidade,
                                                                    @NotNull final Long codCargo) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getModelosSelecaoRealizacao(codUnidade, codCargo);
        } else {
            return integradorProLog.getModelosSelecaoRealizacao(codUnidade, codCargo);
        }
    }

    @NotNull
    @Override
    public ModeloChecklistRealizacao getModeloChecklistRealizacao(
            final @NotNull Long codModeloChecklist,
            final @NotNull Long codVeiculo,
            final @NotNull String placaVeiculo,
            final @NotNull TipoChecklist tipoChecklist) throws Throwable {
        if (getSistema() != null) {
            return getSistema()
                    .getModeloChecklistRealizacao(codModeloChecklist, codVeiculo, placaVeiculo, tipoChecklist);
        } else {
            return integradorProLog
                    .getModeloChecklistRealizacao(codModeloChecklist, codVeiculo, placaVeiculo, tipoChecklist);
        }
    }

    @NotNull
    @Override
    public InfosChecklistInserido insertChecklist(@NotNull final ChecklistInsercao checklist,
                                                  final boolean foiOffline,
                                                  final boolean deveAbrirOs) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insertChecklist(checklist, foiOffline, deveAbrirOs);
        } else {
            return integradorProLog.insertChecklist(checklist, foiOffline, deveAbrirOs);
        }
    }

    @NotNull
    @Override
    public List<TipoVeiculo> getTiposVeiculosFiltroChecklist(@NotNull final Long codEmpresa) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getTiposVeiculosFiltroChecklist(codEmpresa);
        } else {
            return integradorProLog.getTiposVeiculosFiltroChecklist(codEmpresa);
        }
    }

    @NotNull
    @Override
    public Checklist getChecklistByCodigo(@NotNull final Long codChecklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().getChecklistByCodigo(codChecklist);
        } else {
            return integradorProLog.getChecklistByCodigo(codChecklist);
        }
    }

    @NotNull
    @Override
    public List<Checklist> getChecklistsByColaborador(@NotNull final Long cpf,
                                                      @NotNull final Long dataInicial,
                                                      @NotNull final Long dataFinal,
                                                      final int limit,
                                                      final long offset,
                                                      final boolean resumido) throws Exception {
        if (getSistema() != null) {
            return getSistema().getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
        } else {
            return integradorProLog.getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
        }
    }

    @NotNull
    @Override
    public List<Checklist> getTodosChecklists(@NotNull final Long codUnidade,
                                              @Nullable final Long codEquipe,
                                              @Nullable final Long codTipoVeiculo,
                                              @Nullable final String placaVeiculo,
                                              final long dataInicial,
                                              final long dataFinal,
                                              final int limit,
                                              final long offset,
                                              final boolean resumido) throws Exception {
        if (getSistema() != null) {
            return getSistema()
                    .getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                                        limit, offset, resumido);
        } else {
            return integradorProLog
                    .getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                                        limit, offset, resumido);
        }
    }

    @NotNull
    @Override
    public List<ChecklistListagem> getListagemByColaborador(@NotNull final Long codColaborador,
                                                            @NotNull final LocalDate dataInicial,
                                                            @NotNull final LocalDate dataFinal,
                                                            final int limit,
                                                            final long offset) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getListagemByColaborador(codColaborador, dataInicial, dataFinal, limit, offset);
        } else {
            return integradorProLog.getListagemByColaborador(codColaborador, dataInicial, dataFinal, limit, offset);
        }
    }

    @NotNull
    @Override
    public List<ChecklistListagem> getListagem(@NotNull final Long codUnidade,
                                               @Nullable final Long codEquipe,
                                               @Nullable final Long codTipoVeiculo,
                                               @Nullable final Long codVeiculo,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal,
                                               final int limit,
                                               final long offset) throws Throwable {
        if (getSistema() != null) {
            return getSistema()
                    .getListagem(
                            codUnidade,
                            codEquipe,
                            codTipoVeiculo,
                            codVeiculo,
                            dataInicial,
                            dataFinal,
                            limit,
                            offset);
        } else {
            return integradorProLog
                    .getListagem(codUnidade,
                                 codEquipe,
                                 codTipoVeiculo,
                                 codVeiculo,
                                 dataInicial,
                                 dataFinal,
                                 limit,
                                 offset);
        }
    }

    @NotNull
    @Override
    public DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal,
                                                      final boolean itensCriticosRetroativos) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
        } else {
            return integradorProLog.getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
        }
    }

    @NotNull
    @Override
    public InfosChecklistInserido insertChecklistOffline(@NotNull final ChecklistInsercao checklist) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insertChecklistOffline(checklist);
        } else {
            return integradorProLog.insertChecklistOffline(checklist);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // ############################## OPERAÇÕES INTEGRADAS - CHECKLIST ORDEM DE SERVIÇO ################################
    // #################################################################################################################
    // #################################################################################################################
    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        if (getSistema() != null) {
            getSistema().resolverItem(item);
        } else {
            integradorProLog.resolverItem(item);
        }
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        if (getSistema() != null) {
            getSistema().resolverItens(itensResolucao);
        } else {
            integradorProLog.resolverItens(itensResolucao);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // ######################################## OPERAÇÕES INTEGRADAS - VEÍCULO #########################################
    // #################################################################################################################
    // #################################################################################################################
    @Override
    public void insert(
            @NotNull final VeiculoCreateDto veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (getSistema() != null) {
            getSistema().insert(veiculo, checklistOfflineListener);
        } else {
            integradorProLog.insert(veiculo, checklistOfflineListener);
        }
    }

    @NotNull
    @Override
    public InfosVeiculoEditado update(
            @NotNull final Long codColaboradorResponsavelEdicao,
            @NotNull final VeiculoEdicao veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (getSistema() != null) {
            return getSistema().update(codColaboradorResponsavelEdicao, veiculo, checklistOfflineListener);
        } else {
            return integradorProLog.update(codColaboradorResponsavelEdicao, veiculo, checklistOfflineListener);
        }
    }

    @NotNull
    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade, @Nullable final Boolean ativos)
            throws
            Exception {
        if (getSistema() != null) {
            return getSistema().getVeiculosAtivosByUnidade(codUnidade, ativos);
        } else {
            return integradorProLog.getVeiculosAtivosByUnidade(codUnidade, ativos);
        }
    }

    @NotNull
    @Override
    public List<String> getPlacasVeiculosByTipo(@NotNull final Long codUnidade, @NotNull final String codTipo)
            throws Exception {
        if (getSistema() != null) {
            return getSistema().getPlacasVeiculosByTipo(codUnidade, codTipo);
        } else {
            return integradorProLog.getPlacasVeiculosByTipo(codUnidade, codTipo);
        }
    }

    @NotNull
    @Override
    public List<VeiculoListagem> getVeiculosByUnidades(@NotNull final List<Long> codUnidades,
                                                       final boolean apenasAtivos,
                                                       @Nullable final Long codTipoVeiculo) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getVeiculosByUnidades(codUnidades, apenasAtivos, codTipoVeiculo);
        } else {
            return integradorProLog.getVeiculosByUnidades(codUnidades, apenasAtivos, codTipoVeiculo);
        }
    }

    @NotNull
    @Override
    public VeiculoVisualizacao getVeiculoByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getVeiculoByCodigo(codVeiculo);
        } else {
            return integradorProLog.getVeiculoByCodigo(codVeiculo);
        }
    }

    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull final String placa,
                                     @NotNull final Long codUnidade,
                                     final boolean withPneus) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getVeiculoByPlaca(placa, codUnidade, withPneus);
        } else {
            return integradorProLog.getVeiculoByPlaca(placa, codUnidade, withPneus);
        }
    }

    @NotNull
    @Override
    public VeiculoDadosColetaKm getDadosColetaKmByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getDadosColetaKmByCodigo(codVeiculo);
        } else {
            return integradorProLog.getDadosColetaKmByCodigo(codVeiculo);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // ################################# OPERAÇÕES INTEGRADAS - VEÍCULO TRANSFERÊNCIA ##################################
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    @Override
    public Long insertProcessoTransferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable {
        if (getSistema() != null) {
            return getSistema()
                    .insertProcessoTransferenciaVeiculo(
                            processoTransferenciaVeiculo,
                            dadosChecklistOfflineChangedListener);
        } else {
            return integradorProLog
                    .insertProcessoTransferenciaVeiculo(
                            processoTransferenciaVeiculo,
                            dadosChecklistOfflineChangedListener);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // ########################################## OPERAÇÕES INTEGRADAS - PNEU ##########################################
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    @Override
    public Long insert(@NotNull final Long codigoColaboradorCadastro,
                       @NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final OrigemAcaoEnum origemCadastro) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insert(codigoColaboradorCadastro, pneu, codUnidade, origemCadastro);
        } else {
            return integradorProLog.insert(codigoColaboradorCadastro, pneu, codUnidade, origemCadastro);
        }
    }

    @NotNull
    @Override
    public List<Long> insert(@NotNull final Long codigoColaboradorCadastro,
                             @NotNull final List<Pneu> pneus) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insert(codigoColaboradorCadastro, pneus);
        } else {
            return integradorProLog.insert(codigoColaboradorCadastro, pneus);
        }
    }

    @Override
    public void update(@NotNull final Long codigoColaboradorEdicao,
                       @NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codOriginalPneu) throws Throwable {
        if (getSistema() != null) {
            getSistema().update(codigoColaboradorEdicao, pneu, codUnidade, codOriginalPneu);
        } else {
            integradorProLog.update(codigoColaboradorEdicao, pneu, codUnidade, codOriginalPneu);
        }
    }

    @NotNull
    @Override
    public List<Pneu> getPneusByCodUnidadesByStatus(@NotNull final List<Long> codUnidades,
                                                    @NotNull final StatusPneu status) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getPneusByCodUnidadesByStatus(codUnidades, status);
        } else {
            return integradorProLog.getPneusByCodUnidadesByStatus(codUnidades, status);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // ################################### OPERAÇÕES INTEGRADAS - PNEU TRANSFERÊNCIA ###################################
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    @Override
    public Long insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final OffsetDateTime dataHoraSincronizacao,
                                    final boolean isTransferenciaFromVeiculo) throws Throwable {
        if (getSistema() != null) {
            return getSistema()
                    .insertTransferencia(
                            pneuTransferenciaRealizacao,
                            dataHoraSincronizacao,
                            isTransferenciaFromVeiculo);
        } else {
            return integradorProLog
                    .insertTransferencia(
                            pneuTransferenciaRealizacao,
                            dataHoraSincronizacao,
                            isTransferenciaFromVeiculo);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // ###################################### OPERAÇÕES INTEGRADAS - MOVIMENTAÇÃO ######################################
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    @Override
    public Long insert(@NotNull final ServicoDao servicoDao,
                       @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final OffsetDateTime dataHoraMovimentacao,
                       final boolean fecharServicosAutomaticamente) throws Throwable {
        if (getSistema() != null) {
            return getSistema()
                    .insert(servicoDao,
                            campoPersonalizadoDao,
                            processoMovimentacao,
                            dataHoraMovimentacao,
                            fecharServicosAutomaticamente);
        } else {
            return integradorProLog
                    .insert(servicoDao,
                            campoPersonalizadoDao,
                            processoMovimentacao,
                            dataHoraMovimentacao,
                            fecharServicosAutomaticamente);
        }
    }

    @NotNull
    @Override
    public List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoMovimentacao(
            @NotNull final Long codUnidade,
            @NotNull final CampoPersonalizadoDao campoPersonalizadoDao) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getCamposParaRealizacaoMovimentacao(codUnidade, campoPersonalizadoDao);
        } else {
            return integradorProLog.getCamposParaRealizacaoMovimentacao(codUnidade, campoPersonalizadoDao);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // #################################### OPERAÇÕES INTEGRADAS - AFERIÇÃO SERVIÇO ####################################
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final VeiculoAberturaServicoFiltro filtro)
            throws Throwable {
        if (getSistema() != null) {
            return getSistema().getVeiculoAberturaServico(filtro);
        } else {
            return integradorProLog.getVeiculoAberturaServico(filtro);
        }
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade,
                             @NotNull final OffsetDateTime dataHorafechamentoServico,
                             @NotNull final Servico servico) throws Throwable {
        if (getSistema() != null) {
            getSistema().fechaServico(codUnidade, dataHorafechamentoServico, servico);
        } else {
            integradorProLog.fechaServico(codUnidade, dataHorafechamentoServico, servico);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // ###################################### OPERAÇÕES INTEGRADAS - TIPO VEICULO ######################################
    // #################################################################################################################
    // #################################################################################################################
    @Override
    public @NotNull Long insertTipoVeiculo(final @NotNull TipoVeiculo tipoVeiculo) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insertTipoVeiculo(tipoVeiculo);
        } else {
            return integradorProLog.insertTipoVeiculo(tipoVeiculo);
        }
    }

    @Override
    public void updateTipoVeiculo(final @NotNull TipoVeiculo tipoVeiculo) throws Throwable {
        if (getSistema() != null) {
            getSistema().updateTipoVeiculo(tipoVeiculo);
        } else {
            integradorProLog.updateTipoVeiculo(tipoVeiculo);
        }
    }

    // #################################################################################################################
    // #################################################################################################################
    // ###################################### OPERAÇÕES INTEGRADAS - EMPRESA ###########################################
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    @Override
    public List<Empresa> getFiltros(@NotNull final Long cpf) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getFiltros(cpf);
        } else {
            return integradorProLog.getFiltros(cpf);
        }
    }

    @Nullable
    private Sistema getSistema() throws Exception {
        if (sistemaKey == null && !hasTried) {
            sistemaKey = integracaoDao.getSistemaKey(userToken, recursoIntegrado);
            hasTried = true;
        }
        if (sistemaKey == null) {
            return null;
        }

        return SistemasFactoryOld.createSistema(sistemaKey, recursoIntegrado, integradorProLog, userToken);
    }
}