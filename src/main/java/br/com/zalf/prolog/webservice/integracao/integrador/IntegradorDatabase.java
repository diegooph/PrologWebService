package br.com.zalf.prolog.webservice.integracao.integrador;

import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by luiz on 7/17/17.
 */
public class IntegradorDatabase extends IntegradorBase {
    @Nullable
    private final VeiculoDao veiculoDao;
    @Nullable
    private final ChecklistDao checklistDao;

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull String userToken, @NotNull Long codUnidade) {
        try {
            return veiculoDao.getVeiculosAtivosByUnidade(codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private IntegradorDatabase(VeiculoDao veiculoDao, ChecklistDao checklistDao) {
        this.veiculoDao = veiculoDao;
        this.checklistDao = checklistDao;
    }

    public static final class Builder {
        private VeiculoDao veiculoDao;
        private ChecklistDao checklistDao;

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

        public IntegradorDatabase build() {
            return new IntegradorDatabase(veiculoDao, checklistDao);
        }
    }
}