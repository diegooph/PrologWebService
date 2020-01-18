package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorDao;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.VeiculoServico;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.PneuTransferenciaDao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoCadastro;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.VeiculoTransferenciaDao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.MetodoIntegrado;
import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private VeiculoDao veiculoDao;
    private VeiculoTransferenciaDao veiculoTransferenciaDao;
    private PneuDao pneuDao;
    private PneuTransferenciaDao pneuTransferenciaDao;
    private TipoVeiculoDao tipoVeiculoDao;
    private ChecklistDao checklistDao;
    private ChecklistModeloDao checklistModeloDao;
    private OrdemServicoDao ordemServicoDao;
    private AfericaoDao afericaoDao;
    private ServicoDao afericaoServicoDao;
    private MovimentacaoDao movimentacaoDao;
    private ColaboradorDao colaboradorDao;
    private IntegracaoDao integracaoDao;
    @NotNull
    private final String userToken;

    private IntegradorProLog(@NotNull final String userToken,
                             VeiculoDao veiculoDao,
                             VeiculoTransferenciaDao veiculoTransferenciaDao,
                             PneuDao pneuDao,
                             PneuTransferenciaDao pneuTransferenciaDao,
                             TipoVeiculoDao tipoVeiculoDao,
                             ChecklistDao checklistDao,
                             ChecklistModeloDao checklistModeloDao,
                             OrdemServicoDao ordemServicoDao,
                             AfericaoDao afericaoDao,
                             ServicoDao afericaoServicoDao,
                             MovimentacaoDao movimentacaoDao,
                             ColaboradorDao colaboradorDao,
                             IntegracaoDao integracaoDao) {
        this.userToken = TokenCleaner.getOnlyToken(userToken);
        this.veiculoDao = veiculoDao;
        this.veiculoTransferenciaDao = veiculoTransferenciaDao;
        this.pneuDao = pneuDao;
        this.pneuTransferenciaDao = pneuTransferenciaDao;
        this.tipoVeiculoDao = tipoVeiculoDao;
        this.checklistDao = checklistDao;
        this.checklistModeloDao = checklistModeloDao;
        this.ordemServicoDao = ordemServicoDao;
        this.afericaoDao = afericaoDao;
        this.afericaoServicoDao = afericaoServicoDao;
        this.movimentacaoDao = movimentacaoDao;
        this.colaboradorDao = colaboradorDao;
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
                Injection.provideChecklistModeloDao(),
                Injection.provideOrdemServicoDao(),
                Injection.provideAfericaoDao(),
                Injection.provideServicoDao(),
                Injection.provideMovimentacaoDao(),
                Injection.provideColaboradorDao(),
                Injection.provideIntegracaoDao());
    }

    //
    //
    // Informações Providas
    //
    //
    @NotNull
    @Override
    public Colaborador getColaboradorByToken(@NotNull String userToken) throws Exception {
        if (colaboradorDao == null) {
            colaboradorDao = Injection.provideColaboradorDao();
        }
        return colaboradorDao.getByToken(userToken);
    }

    @NotNull
    @Override
    public Restricao getRestricaoByCodUnidade(@NotNull Long codUnidade) throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }

        return afericaoDao.getRestricaoByCodUnidade(codUnidade);
    }

    @NotNull
    @Override
    public ConfiguracaoNovaAfericao getConfiguracaoNovaAfericao(@NotNull final String placa) throws Throwable {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }

        return afericaoDao.getConfiguracaoNovaAfericao(placa);
    }

    @NotNull
    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByCodDiagrama(@NotNull Short codDiagrama) throws Exception {
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

    //
    //
    // Operações Integradas
    //
    //
    @NotNull
    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade, @Nullable Boolean ativos) throws
            Exception {
        return veiculoDao.getVeiculosAtivosByUnidade(codUnidade, ativos);
    }

    @NotNull
    @Override
    public List<String> getPlacasVeiculosByTipo(@NotNull Long codUnidade, @NotNull String codTipo) throws Exception {
        return veiculoDao.getPlacasVeiculosByTipo(codUnidade, codTipo);
    }

    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull String placa, boolean withPneus) throws Exception {
        return veiculoDao.getVeiculoByPlaca(placa, withPneus);
    }

    @Override
    public boolean insert(
            @NotNull final VeiculoCadastro veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        return veiculoDao.insert(veiculo, checklistOfflineListener);
    }

    @Override
    public boolean update(
            @NotNull final String placaOriginal,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        return veiculoDao.update(placaOriginal, veiculo, checklistOfflineListener);
    }

    @Override
    public void updateStatus(
            @NotNull final Long codUnidade,
            @NotNull final String placa,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        veiculoDao.updateStatus(codUnidade, placa, veiculo, checklistOfflineListener);
    }

    @Override
    public boolean delete(
            @NotNull final String placa,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        return veiculoDao.delete(placa, checklistOfflineListener);
    }

    @NotNull
    @Override
    public Long insertProcessoTransferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable {
        return veiculoTransferenciaDao
                .insertProcessoTransferenciaVeiculo(
                        processoTransferenciaVeiculo,
                        dadosChecklistOfflineChangedListener);
    }

    @NotNull
    @Override
    public List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull Long codEmpresa) throws Throwable {
        return tipoVeiculoDao.getTiposVeiculosByEmpresa(codEmpresa);
    }

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable {
        return afericaoDao.getCronogramaAfericao(codUnidades);
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull Long codUnidade,
                                                  @NotNull String placaVeiculo,
                                                  @NotNull String tipoAfericao) throws Throwable {
        return afericaoDao.getNovaAfericaoPlaca(codUnidade, placaVeiculo, tipoAfericao);
    }

    @NotNull
    @Override
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        return afericaoDao.getPneusAfericaoAvulsa(codUnidade);
    }

    @NotNull
    @Override
    public Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                                      @Nullable final Long codColaborador,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable {
        return afericaoDao.getAfericoesAvulsas(codUnidade, codColaborador, dataInicial, dataFinal);
    }

    @NotNull
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        return afericaoDao.insert(codUnidade, afericao, deveAbrirServico);
    }

    @NotNull
    @Override
    public Afericao getAfericaoByCodigo(@NotNull Long codUnidade, @NotNull Long codAfericao) throws Throwable {
        return afericaoDao.getByCod(codUnidade, codAfericao);
    }

    @NotNull
    @Override
    public List<AfericaoPlaca> getAfericoesPlacas(@NotNull Long codUnidade,
                                                  @NotNull String codTipoVeiculo,
                                                  @NotNull String placaVeiculo,
                                                  @NotNull LocalDate dataInicial,
                                                  @NotNull LocalDate dataFinal,
                                                  int limit,
                                                  long offset) throws Throwable {
        return afericaoDao.getAfericoesPlacas(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit,
                offset);
    }

    @NotNull
    @Override
    public ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String userToken) throws Throwable {
        return checklistModeloDao.insertModeloChecklist(modeloChecklist, checklistOfflineListener, statusAtivo, userToken);
    }

    @Override
    public void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean podeMudarCodigoContextoPerguntasEAlternativas,
            @NotNull final String userToken) throws Throwable {
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
        return checklistModeloDao.getModelosSelecaoRealizacao(codUnidade, codCargo);
    }

    @NotNull
    @Override
    public ModeloChecklistRealizacao getModeloChecklistRealizacao(
            final @NotNull Long codModeloChecklist,
            final @NotNull Long codVeiculo,
            final @NotNull String placaVeiculo,
            final @NotNull TipoChecklist tipoChecklist) throws Throwable {
        return checklistModeloDao.getModeloChecklistRealizacao(codModeloChecklist, codVeiculo, placaVeiculo, tipoChecklist);
    }

    @NotNull
    @Override
    public Long insertChecklist(@NotNull final ChecklistInsercao checklist,
                                final boolean foiOffline,
                                final boolean deveAbrirOs) throws Throwable {
        return checklistDao.insert(checklist, foiOffline, deveAbrirOs);
    }

    @NotNull
    @Override
    public Checklist getChecklistByCodigo(@NotNull final Long codChecklist) throws Exception {
        return checklistDao.getByCod(codChecklist);
    }

    @NotNull
    @Override
    public List<Checklist> getChecklistsByColaborador(@NotNull Long cpf,
                                                      @NotNull Long dataInicial,
                                                      @NotNull Long dataFinal,
                                                      int limit,
                                                      long offset,
                                                      boolean resumido) throws Exception {
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
        return checklistDao.getAll(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                limit, offset, resumido);
    }

    @NotNull
    @Override
    public DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal,
                                                      final boolean itensCriticosRetroativos) throws Throwable {
        return checklistDao.getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        ordemServicoDao.resolverItem(item);
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        ordemServicoDao.resolverItens(itensResolucao);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final Pneu pneu, @NotNull final Long codUnidade) throws Throwable {
        return pneuDao.insert(pneu, codUnidade);
    }

    @NotNull
    @Override
    public List<Long> insert(@NotNull final List<Pneu> pneus) throws Throwable {
        return pneuDao.insert(pneus);
    }

    @Override
    public void update(@NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codOriginalPneu) throws Throwable {
        pneuDao.update(pneu, codUnidade, codOriginalPneu);
    }

    @NotNull
    @Override
    public Long insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final OffsetDateTime dataHoraSincronizacao,
                                    final boolean isTransferenciaFromVeiculo) throws Throwable {
        return pneuTransferenciaDao
                .insertTransferencia(pneuTransferenciaRealizacao, dataHoraSincronizacao, isTransferenciaFromVeiculo);
    }

    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final Long codServico,
                                                    @NotNull final String placaVeiculo) throws Throwable {
        return afericaoServicoDao.getVeiculoAberturaServico(codServico, placaVeiculo);
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade,
                             @NotNull final LocalDateTime dataHorafechamentoServico,
                             @NotNull final Servico servico) throws Throwable {
        afericaoServicoDao.fechaServico(codUnidade, dataHorafechamentoServico, servico);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final ServicoDao servicoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final LocalDateTime dataHoraMovimentacao,
                       final boolean fecharServicosAutomaticamente) throws Throwable {
        return movimentacaoDao
                .insert(servicoDao, processoMovimentacao, dataHoraMovimentacao, fecharServicosAutomaticamente);
    }

    public static final class Builder {
        private VeiculoDao veiculoDao;
        private VeiculoTransferenciaDao veiculoTransferenciaDao;
        private PneuDao pneuDao;
        private PneuTransferenciaDao pneuTransferenciaDao;
        private TipoVeiculoDao tipoVeiculoDao;
        private ChecklistDao checklistDao;
        private ChecklistModeloDao checklistModeloDao;
        private OrdemServicoDao ordemServicoDao;
        private AfericaoDao afericaoDao;
        private ServicoDao afericaoServicoDao;
        private MovimentacaoDao movimentacaoDao;
        private ColaboradorDao colaboradorDao;
        private IntegracaoDao integracaoDao;
        private final String userToken;

        public Builder(@NotNull final String userToken) {
            this.userToken = userToken;
        }

        public Builder withVeiculoDao(VeiculoDao veiculoDao) {
            this.veiculoDao = veiculoDao;
            return this;
        }

        public Builder withVeiculoTransferenciaDao(VeiculoTransferenciaDao veiculoTransferenciaDao) {
            this.veiculoTransferenciaDao = veiculoTransferenciaDao;
            return this;
        }

        public Builder withPneuDao(PneuDao pneuDao) {
            this.pneuDao = pneuDao;
            return this;
        }

        public Builder withPneuTransferenciaDao(PneuTransferenciaDao pneuTransferenciaDao) {
            this.pneuTransferenciaDao = pneuTransferenciaDao;
            return this;
        }

        public Builder withTipoVeiculoDao(TipoVeiculoDao tipoVeiculoDao) {
            this.tipoVeiculoDao = tipoVeiculoDao;
            return this;
        }

        public Builder withChecklistDao(ChecklistDao checklistDao) {
            this.checklistDao = checklistDao;
            return this;
        }

        public Builder withChecklistModeloDao(ChecklistModeloDao checklistModeloDao) {
            this.checklistModeloDao = checklistModeloDao;
            return this;
        }

        public Builder withOrdemServicoDao(OrdemServicoDao ordemServicoDao) {
            this.ordemServicoDao = ordemServicoDao;
            return this;
        }

        public Builder withAfericaoDao(AfericaoDao afericaoDao) {
            this.afericaoDao = afericaoDao;
            return this;
        }

        public Builder withAfericaoServicoDao(ServicoDao afericaoServicoDao) {
            this.afericaoServicoDao = afericaoServicoDao;
            return this;
        }

        public Builder withMovimentacaoDao(MovimentacaoDao movimentacaoDao) {
            this.movimentacaoDao = movimentacaoDao;
            return this;
        }

        public Builder withColaboradorDao(ColaboradorDao colaboradorDao) {
            this.colaboradorDao = colaboradorDao;
            return this;
        }

        public Builder withIntegracaoDao(IntegracaoDao integracaoDao) {
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
                    checklistModeloDao,
                    ordemServicoDao,
                    afericaoDao,
                    afericaoServicoDao,
                    movimentacaoDao,
                    colaboradorDao,
                    integracaoDao);
        }
    }
}