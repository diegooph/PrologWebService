package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.dashboard.components.piechart.PieChartComponent;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.RelatorioPneuDao;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 1/22/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DashboardPneuService {
    private static final String TAG = DashboardPneuService.class.getSimpleName();

    public PieChartComponent getQtdPneusByStatus(@NotNull final List<Long> codUnidades) {
        try {
            final RelatorioPneuDao dao = Injection.provideRelatorioPneuDao();
            return DashboardPneuComponentsCreator.createQtdPneusByStatus(dao.getQtPneusByStatus(codUnidades));
        } catch (SQLException ex) {
            Log.e(TAG,
                    "Erro ao buscar a quantidade de pneus por status para as unidades: " + codUnidades,
                    ex);
            throw new RuntimeException(ex);
        }
    }
}