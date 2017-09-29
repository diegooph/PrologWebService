package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.Colaborador;
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
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.google.common.annotations.VisibleForTesting;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * O {@link IntegradorProLog} possui todos os métodos dos quais o sistema ProLog possui integração, seja ela com
 * qualquer empresa, encapsulados. Desse modo, se para qualquer método que se possua uma integração, também seja
 * necessário utilizar o banco do ProLog para inserir ou buscar qualquer informação basta utilizar esse integrador.
 * Toda classe {@link Sistema} possui um {@link IntegradorProLog}.
 */
public final class IntegradorProLog implements InformacoesProvidas, OperacoesIntegradas {
    @Nullable
    private VeiculoDao veiculoDao;
    @Nullable
    private ChecklistDao checklistDao;
    @Nullable
    private AfericaoDao afericaoDao;
    @Nullable
    private ColaboradorDao colaboradorDao;
    @Nullable
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
    public Colaborador getColaboradorByToken(@NotNull String userToken) throws Exception {
        if (colaboradorDao == null) {
            colaboradorDao = Injection.provideColaboradorDao();
        }
        return colaboradorDao.getByToken(userToken);
    }

    @Override
    public Restricao getRestricaoByCodUnidade(@NotNull Long codUnidade) throws Exception {
        if (afericaoDao == null) {
            afericaoDao = Injection.provideAfericaoDao();
        }

        return afericaoDao.getRestricaoByCodUnidade(codUnidade);
    }

    @Override
    public DiagramaVeiculo getDiagramaVeiculoByPlaca(String placaVeiculo) throws Exception {
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
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        return veiculoDao.getVeiculosAtivosByUnidade(codUnidade);
    }

    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull Long codUnidade) throws Exception {
        return afericaoDao.getCronogramaAfericao(codUnidade);
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        return afericaoDao.getNovaAfericao(placaVeiculo);
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        return afericaoDao.insert(afericao, codUnidade);
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao)
            throws Exception {
        return checklistDao.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo) throws Exception {
        return checklistDao.getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo);
    }

    @Override
    public Long insertChecklist(@NotNull Checklist checklist) throws Exception {
        return checklistDao.insert(checklist);
    }

    @NotNull
    @Override
    public FarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                            @NotNull final Date dataInicial,
                                            @NotNull final Date dataFinal,
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