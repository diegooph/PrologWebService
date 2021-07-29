package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoDao;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
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
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleCreateDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class Sistema implements OperacoesIntegradas {
    @NotNull
    private final IntegradorProLog integradorProLog;
    @NotNull
    private final SistemaKey sistemaKey;
    @NotNull
    private final RecursoIntegrado recursoIntegrado;
    @NotNull
    private final String userToken;

    protected Sistema(final IntegradorProLog integradorProLog,
                      final SistemaKey sistemaKey,
                      final RecursoIntegrado recursoIntegrado,
                      final String userToken) {
        this.integradorProLog = checkNotNull(integradorProLog, "integradorProLog não pode ser nulo!");
        this.sistemaKey = checkNotNull(sistemaKey, "sistemaKey não pode ser nulo!");
        this.recursoIntegrado = checkNotNull(recursoIntegrado, "recursoIntegrado não pode ser nulo!");
        this.userToken = checkNotNull(userToken, "userToken não pode ser nulo!");
    }

    // #################################################################################################################
    // #################################################################################################################
    // ####################################### OPERAÇÕES INTEGRADAS - AFERIÇÃO #########################################
    // #################################################################################################################
    // #################################################################################################################

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable {
        return getIntegradorProLog().getCronogramaAfericao(codUnidades);
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final AfericaoBuscaFiltro afericaoBusca) throws Throwable {
        return getIntegradorProLog().getNovaAfericaoPlaca(afericaoBusca);
    }

    @NotNull
    @Override
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        return getIntegradorProLog().getPneusAfericaoAvulsa(codUnidade);
    }

    @NotNull
    @Override
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable {
        return getIntegradorProLog().getNovaAfericaoAvulsa(codUnidade, codPneu, tipoMedicaoColetadaAfericao);
    }

    @NotNull
    @Override
    public Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                                      @Nullable final Long codColaborador,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable {
        return getIntegradorProLog().getAfericoesAvulsas(codUnidade, codColaborador, dataInicial, dataFinal);
    }

    @NotNull
    @Override
    public Afericao getAfericaoByCodigo(@NotNull final Long codUnidade,
                                        @NotNull final Long codAfericao) throws Throwable {
        return getIntegradorProLog().getAfericaoByCodigo(codUnidade, codAfericao);
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
        return getIntegradorProLog()
                .getAfericoesPlacas(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit, offset);
    }

    @NotNull
    @Override
    public List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoAfericao(
            @NotNull final Long codUnidade,
            @NotNull final TipoProcessoColetaAfericao tipoProcessoColetaAfericao,
            @NotNull final CampoPersonalizadoDao campoPersonalizadoDao) throws Throwable {
        return getIntegradorProLog()
                .getCamposParaRealizacaoAfericao(codUnidade, tipoProcessoColetaAfericao, campoPersonalizadoDao);
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
        return getIntegradorProLog()
                .insertModeloChecklist(modeloChecklist, checklistOfflineListener, statusAtivo, userToken);
    }

    @Override
    public void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean podeMudarCodigoContextoPerguntasEAlternativas,
            @NotNull final String userToken) throws Throwable {
        getIntegradorProLog().updateModeloChecklist(
                codUnidade,
                codModelo,
                modeloChecklist,
                checklistOfflineListener,
                podeMudarCodigoContextoPerguntasEAlternativas,
                userToken);
    }

    @NotNull
    @Override
    public List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(@NotNull final Long codUnidade,
                                                                    @NotNull final Long codCargo) throws Throwable {
        return getIntegradorProLog().getModelosSelecaoRealizacao(codUnidade, codCargo);
    }

    @Override
    public @NotNull ModeloChecklistRealizacao getModeloChecklistRealizacao(
            final @NotNull Long codModeloChecklist,
            final @NotNull Long codVeiculo,
            final @NotNull String placaVeiculo,
            final @NotNull TipoChecklist tipoChecklist) throws Throwable {
        return getIntegradorProLog()
                .getModeloChecklistRealizacao(codModeloChecklist, codVeiculo, placaVeiculo, tipoChecklist);
    }

    @NotNull
    @Override
    public InfosChecklistInserido insertChecklist(@NotNull final ChecklistInsercao checklist,
                                                  final boolean foiOffline,
                                                  final boolean deveAbrirOs) throws Throwable {
        return getIntegradorProLog().insertChecklist(checklist, foiOffline, deveAbrirOs);
    }

    @NotNull
    @Override
    public List<TipoVeiculo> getTiposVeiculosFiltroChecklist(@NotNull final Long codEmpresa) throws Throwable {
        return getIntegradorProLog().getTiposVeiculosFiltroChecklist(codEmpresa);
    }

    @NotNull
    @Override
    public Checklist getChecklistByCodigo(@NotNull final Long codChecklist) throws Exception {
        return getIntegradorProLog().getChecklistByCodigo(codChecklist);
    }

    @NotNull
    @Override
    public List<Checklist> getChecklistsByColaborador(@NotNull final Long cpf,
                                                      @NotNull final Long dataInicial,
                                                      @NotNull final Long dataFinal,
                                                      final int limit,
                                                      final long offset, final boolean resumido) throws Exception {
        return getIntegradorProLog().getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
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
        return getIntegradorProLog()
                .getTodosChecklists(
                        codUnidade,
                        codEquipe,
                        codTipoVeiculo,
                        placaVeiculo,
                        dataInicial,
                        dataFinal,
                        limit,
                        offset,
                        resumido);
    }

    @NotNull
    @Override
    public List<ChecklistListagem> getListagemByColaborador(@NotNull final Long codColaborador,
                                                            @NotNull final LocalDate dataInicial,
                                                            @NotNull final LocalDate dataFinal,
                                                            final int limit,
                                                            final long offset) throws Throwable {
        return getIntegradorProLog().getListagemByColaborador(codColaborador, dataInicial, dataFinal, limit, offset);
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
        return getIntegradorProLog()
                .getListagem(
                        codUnidade,
                        codEquipe,
                        codTipoVeiculo,
                        codVeiculo,
                        dataInicial,
                        dataFinal,
                        limit,
                        offset);
    }

    @NotNull
    @Override
    public DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal,
                                                      final boolean itensCriticosRetroativos) throws Throwable {
        return getIntegradorProLog().getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
    }

    @NotNull
    @Override
    public InfosChecklistInserido insertChecklistOffline(@NotNull final ChecklistInsercao checklist) throws Throwable {
        return getIntegradorProLog().insertChecklistOffline(checklist);
    }

    // #################################################################################################################
    // #################################################################################################################
    // ############################## OPERAÇÕES INTEGRADAS - CHECKLIST ORDEM DE SERVIÇO ################################
    // #################################################################################################################
    // #################################################################################################################

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        getIntegradorProLog().resolverItem(item);
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        getIntegradorProLog().resolverItens(itensResolucao);
    }

    // #################################################################################################################
    // #################################################################################################################
    // ######################################## OPERAÇÕES INTEGRADAS - VEÍCULO #########################################
    // #################################################################################################################
    // #################################################################################################################

    @Override
    public void insert(
            @NotNull final VehicleCreateDto veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        getIntegradorProLog().insert(veiculo, checklistOfflineListener);
    }

    @NotNull
    @Override
    public InfosVeiculoEditado update(
            @NotNull final Long codColaboradorResponsavelEdicao,
            @NotNull final VeiculoEdicao veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        return getIntegradorProLog().update(codColaboradorResponsavelEdicao, veiculo, checklistOfflineListener);
    }

    @NotNull
    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade,
                                                    @Nullable final Boolean ativos) throws
            Exception {
        return getIntegradorProLog().getVeiculosAtivosByUnidade(codUnidade, ativos);
    }

    @NotNull
    @Override
    public List<String> getPlacasVeiculosByTipo(@NotNull final Long codUnidade,
                                                @NotNull final String codTipo) throws Exception {
        return getIntegradorProLog().getPlacasVeiculosByTipo(codUnidade, codTipo);
    }

    @NotNull
    @Override
    public List<VeiculoListagem> getVeiculosByUnidades(@NotNull final List<Long> codUnidades,
                                                       final boolean apenasAtivos,
                                                       @Nullable final Long codTipoVeiculo) throws Throwable {
        return getIntegradorProLog().getVeiculosByUnidades(codUnidades, apenasAtivos, codTipoVeiculo);
    }

    @NotNull
    @Override
    public VeiculoVisualizacao getVeiculoByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        return getIntegradorProLog().getVeiculoByCodigo(codVeiculo);
    }

    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull final String placa,
                                     @Nullable final Long codUnidade,
                                     final boolean withPneus) throws Throwable {
        return getIntegradorProLog().getVeiculoByPlaca(placa, codUnidade, withPneus);
    }

    @NotNull
    @Override
    public VeiculoDadosColetaKm getDadosColetaKmByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        return getIntegradorProLog().getDadosColetaKmByCodigo(codVeiculo);
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
        return getIntegradorProLog()
                .insertProcessoTransferenciaVeiculo(
                        processoTransferenciaVeiculo,
                        dadosChecklistOfflineChangedListener);
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
        return getIntegradorProLog().insert(codigoColaboradorCadastro, pneu, codUnidade, origemCadastro);
    }

    @NotNull
    @Override
    public List<Long> insert(@NotNull final Long codigoColaboradorCadastro,
                             @NotNull final List<Pneu> pneus) throws Throwable {
        return getIntegradorProLog().insert(codigoColaboradorCadastro, pneus);
    }

    @Override
    public void update(@NotNull final Long codigoColaboradorEdicao,
                       @NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codOriginalPneu) throws Throwable {
        getIntegradorProLog().update(codigoColaboradorEdicao, pneu, codUnidade, codOriginalPneu);
    }

    @NotNull
    @Override
    public List<Pneu> getPneusByCodUnidadesByStatus(@NotNull final List<Long> codUnidades,
                                                    @NotNull final StatusPneu status) throws Throwable {
        return getIntegradorProLog().getPneusByCodUnidadesByStatus(codUnidades, status);
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
        return getIntegradorProLog()
                .insertTransferencia(pneuTransferenciaRealizacao, dataHoraSincronizacao, isTransferenciaFromVeiculo);
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
        return getIntegradorProLog()
                .insert(servicoDao,
                        campoPersonalizadoDao,
                        processoMovimentacao,
                        dataHoraMovimentacao,
                        fecharServicosAutomaticamente);
    }

    @NotNull
    @Override
    public List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoMovimentacao(
            @NotNull final Long codUnidade,
            @NotNull final CampoPersonalizadoDao campoPersonalizadoDao) throws Throwable {
        return getIntegradorProLog().getCamposParaRealizacaoMovimentacao(codUnidade, campoPersonalizadoDao);
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
        return getIntegradorProLog().getVeiculoAberturaServico(filtro);
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade,
                             @NotNull final OffsetDateTime dataHorafechamentoServico,
                             @NotNull final Servico servico) throws Throwable {
        getIntegradorProLog().fechaServico(codUnidade, dataHorafechamentoServico, servico);
    }

    // #################################################################################################################
    // #################################################################################################################
    // ###################################### OPERAÇÕES INTEGRADAS - TIPO VEICULO ######################################
    // #################################################################################################################
    // #################################################################################################################

    @NotNull
    @Override
    public Long insertTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        return getIntegradorProLog().insertTipoVeiculo(tipoVeiculo);
    }

    @Override
    public void updateTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        getIntegradorProLog().updateTipoVeiculo(tipoVeiculo);
    }

    // #################################################################################################################
    // #################################################################################################################
    // ###################################### OPERAÇÕES INTEGRADAS - EMPRESA ###########################################
    // #################################################################################################################
    // #################################################################################################################

    @NotNull
    @Override
    public List<Empresa> getFiltros(@NotNull final Long cpf) throws Throwable {
        return getIntegradorProLog().getFiltros(cpf);
    }

    @NotNull
    protected IntegradorProLog getIntegradorProLog() {
        return integradorProLog;
    }

    @NotNull
    protected SistemaKey getSistemaKey() {
        return sistemaKey;
    }

    @NotNull
    protected RecursoIntegrado getRecursoIntegrado() {
        return recursoIntegrado;
    }

    @NotNull
    protected String getUserToken() {
        return userToken;
    }
}