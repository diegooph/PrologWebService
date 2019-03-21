package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorDao;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * O {@link IntegradorProLog} possui todos os métodos dos quais o sistema ProLog possui integração, seja ela com
 * qualquer empresa, encapsulados. Desse modo, se para qualquer método que se possua uma integração, também seja
 * necessário utilizar o banco do ProLog para inserir ou buscar qualquer informação basta utilizar esse integrador.
 * Toda classe {@link Sistema} possui um {@link IntegradorProLog}.
 */
public final class IntegradorProLog implements InformacoesProvidas, OperacoesIntegradas {
    private VeiculoDao veiculoDao;
    private ChecklistDao checklistDao;
    private ChecklistModeloDao checklistModeloDao;
    private OrdemServicoDao ordemServicoDao;
    private AfericaoDao afericaoDao;
    private ColaboradorDao colaboradorDao;
    private IntegracaoDao integracaoDao;
    @NotNull
    private final String userToken;

    private IntegradorProLog(@NotNull final String userToken,
                             VeiculoDao veiculoDao,
                             ChecklistDao checklistDao,
                             ChecklistModeloDao checklistModeloDao,
                             OrdemServicoDao ordemServicoDao,
                             AfericaoDao afericaoDao,
                             ColaboradorDao colaboradorDao,
                             IntegracaoDao integracaoDao) {
        this.userToken = TokenCleaner.getOnlyToken(userToken);
        this.veiculoDao = veiculoDao;
        this.checklistDao = checklistDao;
        this.checklistModeloDao = checklistModeloDao;
        this.ordemServicoDao = ordemServicoDao;
        this.afericaoDao = afericaoDao;
        this.colaboradorDao = colaboradorDao;
        this.integracaoDao = integracaoDao;
    }

    @VisibleForTesting
    public static IntegradorProLog full(@NotNull final String userToken) {
        return new IntegradorProLog(
                userToken,
                Injection.provideVeiculoDao(),
                Injection.provideChecklistDao(),
                Injection.provideChecklistModeloDao(),
                Injection.provideOrdemServicoDao(),
                Injection.provideAfericaoDao(),
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
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull String placaVeiculo) throws Exception {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getDiagramaVeiculoByPlaca(placaVeiculo);
    }

    @NotNull
    @Override
    public String getCodUnidadeClienteByCodUnidadeProLog(@NotNull Long codUnidadeProLog) throws Exception {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }

        return integracaoDao.getCodUnidadeErpClienteByCodUnidadeProLog(codUnidadeProLog);
    }

    @NotNull
    @Override
    public String getTokenIntegracaoByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws Throwable {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }

        return integracaoDao.getTokenIntegracaoByCodUnidadeProLog(codUnidadeProLog);
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
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@NotNull Long codUnidade) throws Exception {
        return veiculoDao.getTipoVeiculosByUnidade(codUnidade);
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

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable {
        return afericaoDao.getCronogramaAfericao(codUnidade);
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

    @Nullable
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade, @NotNull final Afericao afericao) throws Throwable {
        return afericaoDao.insert(codUnidade, afericao);
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

    @Override
    public void insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist) throws Throwable {
        checklistModeloDao.insertModeloChecklist(modeloChecklist);
    }

    @Override
    public void updateModeloChecklist(@NotNull final String token,
                                      @NotNull final Long codUnidade,
                                      @NotNull final Long codModelo,
                                      @NotNull final ModeloChecklistEdicao modeloChecklist) throws Throwable {
        checklistModeloDao.updateModeloChecklist(token, codUnidade, codModelo, modeloChecklist);
    }

    @NotNull
    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao)
            throws Exception {
        return checklistDao.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
    }

    @NotNull
    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        return checklistDao.getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo, tipoChecklist);
    }

    @NotNull
    @Override
    public Long insertChecklist(@NotNull Checklist checklist) throws Exception {
        return checklistDao.insert(checklist);
    }

    @NotNull
    @Override
    public Checklist getChecklistByCodigo(@NotNull final Long codChecklist) throws Exception {
        return checklistDao.getByCod(codChecklist, userToken);
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

    public static final class Builder {
        private VeiculoDao veiculoDao;
        private ChecklistDao checklistDao;
        private ChecklistModeloDao checklistModeloDao;
        private OrdemServicoDao ordemServicoDao;
        private AfericaoDao afericaoDao;
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
                    checklistDao,
                    checklistModeloDao,
                    ordemServicoDao,
                    afericaoDao,
                    colaboradorDao,
                    integracaoDao);
        }
    }
}