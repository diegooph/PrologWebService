package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorDao;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
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
    private AfericaoDao afericaoDao;
    private ColaboradorDao colaboradorDao;
    private IntegracaoDao integracaoDao;

    private IntegradorProLog(VeiculoDao veiculoDao,
                             ChecklistDao checklistDao,
                             AfericaoDao afericaoDao,
                             ColaboradorDao colaboradorDao,
                             IntegracaoDao integracaoDao) {
        this.veiculoDao = veiculoDao;
        this.checklistDao = checklistDao;
        this.afericaoDao = afericaoDao;
        this.colaboradorDao = colaboradorDao;
        this.integracaoDao = integracaoDao;
    }

    @VisibleForTesting
    public static IntegradorProLog full() {
        return new IntegradorProLog(
                Injection.provideVeiculoDao(),
                Injection.provideChecklistDao(),
                Injection.provideAfericaoDao(),
                Injection.provideColaboradorDao(),
                Injection.provideIntegracaoDao());
    }

    //
    //
    // Informações Providas
    //
    //
    @Override
    public Colaborador getColaboradorByToken(@Nonnull String userToken) throws Exception {
        if (colaboradorDao == null) {
            colaboradorDao = Injection.provideColaboradorDao();
        }
        return colaboradorDao.getByToken(userToken);
    }

    @Override
    public Restricao getRestricaoByCodUnidade(@Nonnull Long codUnidade) throws Exception {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }

        return afericaoDao.getRestricaoByCodUnidade(codUnidade);
    }

    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByCodDiagrama(Short codDiagrama) throws Exception {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getDiagramaVeiculoByCod(codDiagrama);
    }

    @Override
    public Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(String placaVeiculo) throws Exception {
        if (veiculoDao == null) {
            veiculoDao = Injection.provideVeiculoDao();
        }
        return veiculoDao.getDiagramaVeiculoByPlaca(placaVeiculo);
    }

    @Override
    public String getCodUnidadeClienteByCodUnidadeProLog(Long codUnidadeProLog) throws Exception {
        if (integracaoDao == null) {
            integracaoDao = Injection.provideIntegracaoDao();
        }

        return integracaoDao.getCodUnidadeErpClienteByCodUnidadeProLog(codUnidadeProLog);
    }

    //
    //
    // Operações Integradas
    //
    //
    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@Nonnull Long codUnidade) throws Exception {
        return veiculoDao.getVeiculosAtivosByUnidade(codUnidade);
    }

    @Override
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@Nonnull Long codUnidade) throws Exception {
        return veiculoDao.getTipoVeiculosByUnidade(codUnidade);
    }

    @Override
    public List<String> getPlacasVeiculosByTipo(Long codUnidade, String codTipo) throws Exception {
        return veiculoDao.getPlacasVeiculosByTipo(codUnidade, codTipo);
    }

    @Override
    public CronogramaAfericao getCronogramaAfericao(@Nonnull Long codUnidade) throws Exception {
        return afericaoDao.getCronogramaAfericao(codUnidade);
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        return afericaoDao.getNovaAfericao(placaVeiculo);
    }

    @Override
    public boolean insertAfericao(@Nonnull Afericao afericao, @Nonnull Long codUnidade) throws Exception {
        return afericaoDao.insert(afericao, codUnidade);
    }

    @Nonnull
    @Override
    public Afericao getAfericaoByCodigo(@Nonnull Long codUnidade, @Nonnull Long codAfericao) throws Exception {
        return afericaoDao.getByCod(codUnidade, codAfericao);
    }

    @Override
    public List<Afericao> getAfericoes(@Nonnull Long codUnidade,
                                       @Nonnull String codTipoVeiculo,
                                       @Nonnull String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       int limit,
                                       long offset) throws Exception {
        return afericaoDao.getAfericoes(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit, offset);
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@Nonnull Long codUnidade,
                                                                                    @Nonnull Long codFuncao)
            throws Exception {
        return checklistDao.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@Nonnull Long codUnidade,
                                                      @Nonnull Long codModelo,
                                                      @Nonnull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        return checklistDao.getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo, tipoChecklist);
    }

    @Override
    public Long insertChecklist(@Nonnull Checklist checklist) throws Exception {
        return checklistDao.insert(checklist);
    }

    @Nonnull
    @Override
    public Checklist getChecklistByCodigo(@Nonnull Long codChecklist) throws Exception {
        return checklistDao.getByCod(codChecklist);
    }

    @Nonnull
    @Override
    public List<Checklist> getChecklistsByColaborador(@Nonnull Long cpf, Long dataInicial, Long dataFinal, int limit,
                                                      long offset, boolean resumido) throws Exception {
        return checklistDao.getByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
    }

    @Nonnull
    @Override
    public List<Checklist> getTodosChecklists(@Nonnull final Long codUnidade,
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

    @Nonnull
    @Override
    public FarolChecklist getFarolChecklist(@Nonnull final Long codUnidade,
                                            @Nonnull final Date dataInicial,
                                            @Nonnull final Date dataFinal,
                                            final boolean itensCriticosRetroativos) throws Exception {
        return checklistDao.getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
    }

    public static final class Builder {
        private VeiculoDao veiculoDao;
        private ChecklistDao checklistDao;
        private AfericaoDao afericaoDao;
        private ColaboradorDao colaboradorDao;
        private IntegracaoDao integracaoDao;

        public Builder() {

        }

        public Builder withVeiculoDao(VeiculoDao veiculoDao) {
            this.veiculoDao = veiculoDao;
            return this;
        }

        public Builder withChecklistDao(ChecklistDao checklistDao) {
            this.checklistDao = checklistDao;
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
            return new IntegradorProLog(veiculoDao, checklistDao, afericaoDao, colaboradorDao, integracaoDao);
        }
    }
}