package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by luiz on 7/17/17.
 */
public final class IntegradorProLog implements OperacoesIntegradas {
    @Nullable
    private final VeiculoDao veiculoDao;
    @Nullable
    private final ChecklistDao checklistDao;
    @Nullable
    private final AfericaoDao afericaoDao;

    private IntegradorProLog(VeiculoDao veiculoDao, ChecklistDao checklistDao, AfericaoDao afericaoDao) {
        this.veiculoDao = veiculoDao;
        this.checklistDao = checklistDao;
        this.afericaoDao = afericaoDao;
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) {
        try {
            return veiculoDao.getVeiculosAtivosByUnidade(codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        return null;
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        return afericaoDao.insert(afericao, codUnidade);
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao) throws Exception {
        return checklistDao.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade, @NotNull Long codModelo, @NotNull String placaVeiculo) throws Exception {
        return checklistDao.getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo);
    }

    @Override
    public boolean insertChecklist(@NotNull Checklist checklist) throws Exception {
        return checklistDao.insert(checklist);
    }

    public static final class Builder {
        private VeiculoDao veiculoDao;
        private ChecklistDao checklistDao;
        private AfericaoDao afericaoDao;

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

        public IntegradorProLog build() {
            return new IntegradorProLog(veiculoDao, checklistDao, afericaoDao);
        }
    }
}