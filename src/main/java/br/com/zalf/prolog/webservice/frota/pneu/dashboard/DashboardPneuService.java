package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieChartComponent;
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
    private final DashboardDao dashDao = Injection.provideDashboardDao();
    private final RelatorioPneuDao relatorioDao = Injection.provideRelatorioPneuDao();

    public PieChartComponent getQtdPneusByStatus(@NotNull final Integer codComponente,
                                                 @NotNull final List<Long> codUnidades) {
        try {
            return DashboardPneuComponentsCreator.createQtdPneusByStatus(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getQtPneusByStatus(codUnidades));
        } catch (SQLException ex) {
            Log.e(TAG,
                    "Erro ao buscar a quantidade de pneus por status para as unidades: " + codUnidades,
                    ex);
            throw new RuntimeException(ex);
        }
    }

    public QuantidadeItemComponent getQtdPneusPressaoIncorreta(@NotNull final Integer codComponente,
                                                               @NotNull final List<Long> codUnidades) {
        try {
            return DashboardPneuComponentsCreator.createQtdPneusPressaoIncorreta(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getQtdPneusPressaoIncorreta(codUnidades));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a quantidade de pneus com pressão incorreta. \n" +
                    "Unidades: %s", codUnidades), e);
            throw new RuntimeException(e);
        }
    }
}