package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoDao;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.InfosChecklistInserido;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineDao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDaoV2;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.VeiculoServico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.VeiculoAberturaServicoFiltro;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.PneuTransferenciaDao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.InfosVeiculoEditado;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoDadosColetaKm;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.VeiculoTransferenciaDao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorDao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleCreateDto;
import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * O {@link IntegradorProLog} possui todos os métodos dos quais o sistema ProLog possui integração, seja ela com
 * qualquer empresa, encapsulados. Desse modo, se para qualquer método que se possua uma integração, também seja
 * necessário utilizar o banco do ProLog para inserir ou buscar qualquer informação basta utilizar esse integrador.
 * Toda classe {@link Sistema} possui um {@link IntegradorProLog}.
 */
public final class IntegradorProLog implements InformacoesProvidas, OperacoesIntegradas {
    @NotNull
    private final String userToken;
    private VeiculoTransferenciaDao veiculoTransferenciaDao;
    private PneuDao pneuDao;
    private PneuTransferenciaDao pneuTransferenciaDao;
    private TipoVeiculoDao tipoVeiculoDao;
    private ChecklistDao checklistDao;
    private ChecklistOfflineDao checklistOfflineDao;
    private ChecklistModeloDao checklistModeloDao;
    private OrdemServicoDao ordemServicoDao;
    private ServicoDao afericaoServicoDao;
    private MovimentacaoDao movimentacaoDao;
    private VeiculoDao veiculoDao;
    private AfericaoDaoV2 afericaoDao;
    private ColaboradorDao colaboradorDao;
    private EmpresaDao empresaDao;
    private IntegracaoDao integracaoDao;

    private IntegradorProLog(@NotNull final String userToken,
                             final VeiculoDao veiculoDao,
                             final VeiculoTransferenciaDao veiculoTransferenciaDao,
                             final PneuDao pneuDao,
                             final PneuTransferenciaDao pneuTransferenciaDao,
                             final TipoVeiculoDao tipoVeiculoDao,
                             final ChecklistDao checklistDao,
                             final ChecklistOfflineDao checklistOfflineDao,
                             final ChecklistModeloDao checklistModeloDao,
                             final OrdemServicoDao ordemServicoDao,
                             final AfericaoDaoV2 afericaoDao,
                             final ServicoDao afericaoServicoDao,
                             final MovimentacaoDao movimentacaoDao,
                             final ColaboradorDao colaboradorDao,
                             final EmpresaDao empresaDao,
                             final IntegracaoDao integracaoDao) {
        this.userToken = TokenCleaner.getOnlyToken(userToken);
        this.veiculoDao = veiculoDao;
        this.veiculoTransferenciaDao = veiculoTransferenciaDao;
        this.pneuDao = pneuDao;
        this.pneuTransferenciaDao = pneuTransferenciaDao;
        this.tipoVeiculoDao = tipoVeiculoDao;
        this.checklistDao = checklistDao;
        this.checklistOfflineDao = checklistOfflineDao;
        this.checklistModeloDao = checklistModeloDao;
        this.ordemServicoDao = ordemServicoDao;
        this.afericaoDao = afericaoDao;
        this.afericaoServicoDao = afericaoServicoDao;
        this.movimentacaoDao = movimentacaoDao;
        this.colaboradorDao = colaboradorDao;
        this.empresaDao = empresaDao;
        this.integracaoDao = integracaoDao;
    }

    @VisibleForTesting
    public static IntegradorProLog full(@NotNull final String userToken) {
        return new IntegradorProLog(
                userToken,
                Injection.provideVeiculoDao(),
                Injection.provideVeiculoTransferenciaDao(),
                Injection.providePneuDao(),
                Injection.providePneuTransferenciaDao(),
                Injection.provideTipoVeiculoDao(),
                Injection.provideChecklistDao(),
                Injection.provideChecklistOfflineDao(),
                Injection.provideChecklistModeloDao(),
                Injection.provideOrdemServicoDao(),
                Injection.provideAfericaoDao(),
                Injection.provideServicoDao(),
                Injection.provideMovimentacaoDao(),
                Injection.provideColaboradorDao(),
                Injection.provideEmpresaDao(),
                Injection.provideIntegracaoDao());
    }

    //
    //
    // Informações Providas
    //
    //
    @NotNull
    @Override
    public Colaborador getColaboradorByToken(@NotNull final String userToken) throws Exception {
        if (colaboradorDao == null) {
            colaboradorDao = Injection.provideColaboradorDao();
        }
        return colaboradorDao.getByToken(userToken);
    }

    @NotNull
    @Override
    public Restricao getRestricaoByCodUnidade(@NotNull final Long codUnidade) throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }
        return afericaoDao.getRestricaoByCodUnidade(codUnidade);
    }

    @NotNull
    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByCodDiagrama(@NotNull final Short codDiagrama)
            throws Exception {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getDiagramaVeiculoByCod(codDiagrama);
    }

    @NotNull
    @Override
    public String getTokenIntegracaoByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws Throwable {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }
        return integracaoDao.getTokenIntegracaoByCodUnidadeProLog(codUnidadeProLog);
    }

    @NotNull
    @Override
    public Long getCodEmpresaByCodUnidadeProLog(@NotNull final Connection conn,
                                                @NotNull final Long codUnidadeProLog) throws Throwable {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }
        return integracaoDao.getCodEmpresaByCodUnidadeProLog(conn, codUnidadeProLog);
    }

    @NotNull
    @Override
    public String getUrl(@NotNull final Connection conn,
                         @NotNull final Long codEmpresa,
                         @NotNull final SistemaKey sistemaKey,
                         @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }
        return integracaoDao.getUrl(conn, codEmpresa, sistemaKey, metodoIntegrado);
    }

    @NotNull
    @Override
    public String getCodAuxiliarByCodUnidadeProlog(@NotNull final Connection conn,
                                                   @NotNull final Long codUnidadeProlog) throws Throwable {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }
        return integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, codUnidadeProlog);
    }

    @NotNull
    @Override
    public ApiAutenticacaoHolder getApiAutenticacaoHolder(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final SistemaKey sistemaKey,
            @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }
        return integracaoDao.getApiAutenticacaoHolder(conn, codEmpresa, sistemaKey, metodoIntegrado);
    }

    @NotNull
    @Override
    public List<Long> getCodUnidadesIntegracaoBloqueada(@NotNull final String userToken,
                                                        @NotNull final SistemaKey sistemaKey,
                                                        @NotNull final RecursoIntegrado recursoIntegrado)
            throws Throwable {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }
        return integracaoDao.getCodUnidadesIntegracaoBloqueada(userToken, sistemaKey, recursoIntegrado);
    }

    @Override
    public boolean getConfigAberturaServicoPneuIntegracao(@NotNull final Long codUnidade) throws Throwable {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }
        return integracaoDao.getConfigAberturaServicoPneuIntegracao(codUnidade);
    }

    @Override
    public void insert(
            @NotNull final VehicleCreateDto veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        veiculoDao.insert(veiculo, checklistOfflineListener);
    }

    @NotNull
    @Override
    public InfosVeiculoEditado update(
            @NotNull final Long codColaboradorResponsavelEdicao,
            @NotNull final VeiculoEdicao veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.update(codColaboradorResponsavelEdicao, veiculo, checklistOfflineListener);
    }

    //
    //
    // Operações Integradas
    //
    //
    @NotNull
    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade, @Nullable final Boolean ativos)
            throws
            Exception {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getVeiculosAtivosByUnidade(codUnidade, ativos);
    }

    @NotNull
    @Override
    public List<String> getPlacasVeiculosByTipo(@NotNull final Long codUnidade, @NotNull final String codTipo)
            throws Exception {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getPlacasVeiculosByTipo(codUnidade, codTipo);
    }

    @NotNull
    @Override
    public List<VeiculoListagem> getVeiculosByUnidades(@NotNull final List<Long> codUnidades,
                                                       final boolean apenasAtivos,
                                                       @Nullable final Long codTipoVeiculo) throws Throwable {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getVeiculosByUnidades(codUnidades, apenasAtivos, codTipoVeiculo);
    }

    @NotNull
    @Override
    public VeiculoVisualizacao getVeiculoByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getVeiculoByCodigo(codVeiculo);
    }

    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull final String placa,
                                     @NotNull final Long codUnidade,
                                     final boolean withPneus) throws Throwable {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getVeiculoByPlaca(placa, codUnidade, withPneus);
    }

    @NotNull
    @Override
    public VeiculoDadosColetaKm getDadosColetaKmByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getDadosColetaKmByCodigo(codVeiculo);
    }

    @NotNull
    @Override
    public Long insertProcessoTransferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable {
        if (veiculoTransferenciaDao == null) {
            veiculoTransferenciaDao = Injection.provideVeiculoTransferenciaDao();
        }
        return veiculoTransferenciaDao
                .insertProcessoTransferenciaVeiculo(
                        processoTransferenciaVeiculo,
                        dadosChecklistOfflineChangedListener);
    }

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }
        return afericaoDao.getCronogramaAfericao(codUnidades);
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final AfericaoBuscaFiltro afericaoBusca) throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }
        return afericaoDao.getNovaAfericaoPlaca(afericaoBusca);
    }

    @NotNull
    @Override
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }
        return afericaoDao.getPneusAfericaoAvulsa(codUnidade);
    }

    @NotNull
    @Override
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }
        return afericaoDao.getNovaAfericaoAvulsa(codUnidade, codPneu, tipoMedicaoColetadaAfericao);
    }

    @NotNull
    @Override
    public Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                                      @Nullable final Long codColaborador,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }
        return afericaoDao.getAfericoesAvulsas(codUnidade, codColaborador, dataInicial, dataFinal);
    }

    @NotNull
    @Override
    public Afericao getAfericaoByCodigo(@NotNull final Long codUnidade, @NotNull final Long codAfericao)
            throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }
        return afericaoDao.getByCod(codUnidade, codAfericao);
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
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }
        return afericaoDao.getAfericoesPlacas(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit,
                                              offset);
    }

    @NotNull
    @Override
    public List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoAfericao(
            @NotNull final Long codUnidade,
            @NotNull final TipoProcessoColetaAfericao tipoProcessoColetaAfericao,
            @NotNull final CampoPersonalizadoDao campoPersonalizadoDao) throws Throwable {
        return campoPersonalizadoDao.getCamposParaRealizacaoAfericao(codUnidade, tipoProcessoColetaAfericao);
    }

    @NotNull
    @Override
    public ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String userToken) throws Throwable {
        if (checklistModeloDao == null) {
            checklistModeloDao = Injection.provideChecklistModeloDao();
        }
        return checklistModeloDao.insertModeloChecklist(modeloChecklist,
                                                        checklistOfflineListener,
                                                        statusAtivo,
                                                        userToken);
    }

    @Override
    public void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean podeMudarCodigoContextoPerguntasEAlternativas,
            @NotNull final String userToken) throws Throwable {
        if (checklistModeloDao == null) {
            checklistModeloDao = Injection.provideChecklistModeloDao();
        }
        checklistModeloDao.updateModeloChecklist(
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
        if (checklistModeloDao == null) {
            checklistModeloDao = Injection.provideChecklistModeloDao();
        }
        return checklistModeloDao.getModelosSelecaoRealizacao(codUnidade, codCargo);
    }

    @NotNull
    @Override
    public ModeloChecklistRealizacao getModeloChecklistRealizacao(
            final @NotNull Long codModeloChecklist,
            final @NotNull Long codVeiculo,
            final @NotNull String placaVeiculo,
            final @NotNull TipoChecklist tipoChecklist) throws Throwable {
        if (checklistModeloDao == null) {
            checklistModeloDao = Injection.provideChecklistModeloDao();
        }
        return checklistModeloDao.getModeloChecklistRealizacao(codModeloChecklist,
                                                               codVeiculo,
                                                               placaVeiculo,
                                                               tipoChecklist);
    }

    @NotNull
    @Override
    public InfosChecklistInserido insertChecklist(@NotNull final ChecklistInsercao checklist,
                                                  final boolean foiOffline,
                                                  final boolean deveAbrirOs) throws Throwable {
        if (checklistDao == null) {
            checklistDao = Injection.provideChecklistDao();
        }
        return checklistDao.insertChecklist(checklist, foiOffline, deveAbrirOs);
    }

    @NotNull
    @Override
    public List<TipoVeiculo> getTiposVeiculosFiltroChecklist(@NotNull final Long codEmpresa) throws Throwable {
        if (tipoVeiculoDao == null) {
            tipoVeiculoDao = Injection.provideTipoVeiculoDao();
        }
        return tipoVeiculoDao.getTiposVeiculosByEmpresa(codEmpresa);
    }

    @NotNull
    @Override
    public Checklist getChecklistByCodigo(@NotNull final Long codChecklist) throws Exception {
        if (checklistDao == null) {
            checklistDao = Injection.provideChecklistDao();
        }
        return checklistDao.getByCod(codChecklist);
    }

    @NotNull
    @Override
    public List<Checklist> getChecklistsByColaborador(@NotNull final Long cpf,
                                                      @NotNull final Long dataInicial,
                                                      @NotNull final Long dataFinal,
                                                      final int limit,
                                                      final long offset,
                                                      final boolean resumido) throws Exception {
        if (checklistDao == null) {
            checklistDao = Injection.provideChecklistDao();
        }
        return checklistDao.getByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
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
        return null;
    }

    @NotNull
    @Override
    public List<ChecklistListagem> getListagemByColaborador(@NotNull final Long codColaborador,
                                                            @NotNull final LocalDate dataInicial,
                                                            @NotNull final LocalDate dataFinal,
                                                            final int limit,
                                                            final long offset) throws Throwable {
        if (checklistDao == null) {
            checklistDao = Injection.provideChecklistDao();
        }
        return checklistDao.getListagemByColaborador(codColaborador, dataInicial, dataFinal, limit, offset);
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
        if (checklistDao == null) {
            checklistDao = Injection.provideChecklistDao();
        }
        return checklistDao.getListagem(
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
        if (checklistDao == null) {
            checklistDao = Injection.provideChecklistDao();
        }
        return checklistDao.getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
    }

    @NotNull
    @Override
    public InfosChecklistInserido insertChecklistOffline(@NotNull final ChecklistInsercao checklist) throws Throwable {
        if (checklistOfflineDao == null) {
            checklistOfflineDao = Injection.provideChecklistOfflineDao();
        }
        return checklistOfflineDao.insertChecklistOffline(checklist);
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        if (ordemServicoDao == null) {
            ordemServicoDao = Injection.provideOrdemServicoDao();
        }
        ordemServicoDao.resolverItem(item);
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        if (ordemServicoDao == null) {
            ordemServicoDao = Injection.provideOrdemServicoDao();
        }
        ordemServicoDao.resolverItens(itensResolucao);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final Long codigoColaboradorCadastro,
                       @NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final OrigemAcaoEnum origemCadastro) throws Throwable {
        if (pneuDao == null) {
            pneuDao = Injection.providePneuDao();
        }
        return pneuDao.insert(codigoColaboradorCadastro, pneu, codUnidade, origemCadastro);
    }

    @NotNull
    @Override
    public List<Long> insert(@NotNull final Long codigoColaboradorCadastro,
                             @NotNull final List<Pneu> pneus) throws Throwable {
        if (pneuDao == null) {
            pneuDao = Injection.providePneuDao();
        }
        return pneuDao.insert(codigoColaboradorCadastro, pneus);
    }

    @Override
    public void update(@NotNull final Long codigoColaboradorEdicao,
                       @NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codOriginalPneu) throws Throwable {
        if (pneuDao == null) {
            pneuDao = Injection.providePneuDao();
        }
        pneuDao.update(codigoColaboradorEdicao, pneu, codUnidade, codOriginalPneu);
    }

    @NotNull
    @Override
    public List<Pneu> getPneusByCodUnidadesByStatus(@NotNull final List<Long> codUnidades,
                                                    @NotNull final StatusPneu status) throws Throwable {
        if (pneuDao == null) {
            pneuDao = Injection.providePneuDao();
        }
        return pneuDao.getPneusByCodUnidadesByStatus(codUnidades, status);
    }

    @NotNull
    @Override
    public Long insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final OffsetDateTime dataHoraSincronizacao,
                                    final boolean isTransferenciaFromVeiculo) throws Throwable {
        if (pneuTransferenciaDao == null) {
            pneuTransferenciaDao = Injection.providePneuTransferenciaDao();
        }
        return pneuTransferenciaDao
                .insertTransferencia(pneuTransferenciaRealizacao, dataHoraSincronizacao, isTransferenciaFromVeiculo);
    }

    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final VeiculoAberturaServicoFiltro filtro)
            throws Throwable {
        if (afericaoServicoDao == null) {
            afericaoServicoDao = Injection.provideServicoDao();
        }
        return afericaoServicoDao.getVeiculoAberturaServico(filtro);
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade,
                             @NotNull final OffsetDateTime dataHorafechamentoServico,
                             @NotNull final Servico servico) throws Throwable {
        if (afericaoServicoDao == null) {
            afericaoServicoDao = Injection.provideServicoDao();
        }
        afericaoServicoDao.fechaServico(codUnidade, dataHorafechamentoServico, servico);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final ServicoDao servicoDao,
                       @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final OffsetDateTime dataHoraMovimentacao,
                       final boolean fecharServicosAutomaticamente) throws Throwable {
        if (movimentacaoDao == null) {
            movimentacaoDao = Injection.provideMovimentacaoDao();
        }
        return movimentacaoDao
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
        return campoPersonalizadoDao.getCamposParaRealizacaoMovimentacao(codUnidade);
    }

    @NotNull
    @Override
    public Long insertTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        if (tipoVeiculoDao == null) {
            tipoVeiculoDao = Injection.provideTipoVeiculoDao();
        }
        return tipoVeiculoDao.insertTipoVeiculo(tipoVeiculo);
    }

    @Override
    public void updateTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        if (tipoVeiculoDao == null) {
            tipoVeiculoDao = Injection.provideTipoVeiculoDao();
        }
        tipoVeiculoDao.updateTipoVeiculo(tipoVeiculo);
    }

    @NotNull
    @Override
    public List<Empresa> getFiltros(@NotNull final Long cpf) throws Throwable {
        if (empresaDao == null) {
            empresaDao = Injection.provideEmpresaDao();
        }
        return empresaDao.getFiltros(cpf);
    }

    @NotNull
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }
        return afericaoDao.insert(codUnidade, afericao, deveAbrirServico);
    }

    public static final class Builder {
        private final String userToken;
        private VeiculoDao veiculoDao;
        private VeiculoTransferenciaDao veiculoTransferenciaDao;
        private PneuDao pneuDao;
        private PneuTransferenciaDao pneuTransferenciaDao;
        private TipoVeiculoDao tipoVeiculoDao;
        private ChecklistDao checklistDao;
        private ChecklistOfflineDao checklistOfflineDao;
        private ChecklistModeloDao checklistModeloDao;
        private OrdemServicoDao ordemServicoDao;
        private AfericaoDaoV2 afericaoDao;
        private ServicoDao afericaoServicoDao;
        private MovimentacaoDao movimentacaoDao;
        private ColaboradorDao colaboradorDao;
        private EmpresaDao empresaDao;
        private IntegracaoDao integracaoDao;

        public Builder(@NotNull final String userToken) {
            this.userToken = userToken;
        }

        public Builder withVeiculoDao(final VeiculoDao veiculoDao) {
            this.veiculoDao = veiculoDao;
            return this;
        }

        public Builder withVeiculoTransferenciaDao(final VeiculoTransferenciaDao veiculoTransferenciaDao) {
            this.veiculoTransferenciaDao = veiculoTransferenciaDao;
            return this;
        }

        public Builder withPneuDao(final PneuDao pneuDao) {
            this.pneuDao = pneuDao;
            return this;
        }

        public Builder withPneuTransferenciaDao(final PneuTransferenciaDao pneuTransferenciaDao) {
            this.pneuTransferenciaDao = pneuTransferenciaDao;
            return this;
        }

        public Builder withTipoVeiculoDao(final TipoVeiculoDao tipoVeiculoDao) {
            this.tipoVeiculoDao = tipoVeiculoDao;
            return this;
        }

        public Builder withChecklistDao(final ChecklistDao checklistDao) {
            this.checklistDao = checklistDao;
            return this;
        }

        public Builder withChecklistOfflineDao(final ChecklistOfflineDao checklistOfflineDao) {
            this.checklistOfflineDao = checklistOfflineDao;
            return this;
        }

        public Builder withChecklistModeloDao(final ChecklistModeloDao checklistModeloDao) {
            this.checklistModeloDao = checklistModeloDao;
            return this;
        }

        public Builder withOrdemServicoDao(final OrdemServicoDao ordemServicoDao) {
            this.ordemServicoDao = ordemServicoDao;
            return this;
        }

        public Builder withAfericaoDao(final AfericaoDaoV2 afericaoDao) {
            this.afericaoDao = afericaoDao;
            return this;
        }

        public Builder withAfericaoServicoDao(final ServicoDao afericaoServicoDao) {
            this.afericaoServicoDao = afericaoServicoDao;
            return this;
        }

        public Builder withMovimentacaoDao(final MovimentacaoDao movimentacaoDao) {
            this.movimentacaoDao = movimentacaoDao;
            return this;
        }

        public Builder withColaboradorDao(final ColaboradorDao colaboradorDao) {
            this.colaboradorDao = colaboradorDao;
            return this;
        }

        public Builder withEmpresaDao(final EmpresaDao empresaDao) {
            this.empresaDao = empresaDao;
            return this;
        }

        public Builder withIntegracaoDao(final IntegracaoDao integracaoDao) {
            this.integracaoDao = integracaoDao;
            return this;
        }

        public IntegradorProLog build() {
            return new IntegradorProLog(
                    userToken,
                    veiculoDao,
                    veiculoTransferenciaDao,
                    pneuDao,
                    pneuTransferenciaDao,
                    tipoVeiculoDao,
                    checklistDao,
                    checklistOfflineDao,
                    checklistModeloDao,
                    ordemServicoDao,
                    afericaoDao,
                    afericaoServicoDao,
                    movimentacaoDao,
                    colaboradorDao,
                    empresaDao,
                    integracaoDao);
        }
    }
}