package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.barchart.VerticalBarChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.VerticalComboChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieChartComponent;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.RelatorioPneuDao;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Calendar;
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

    public VerticalComboChartComponent getQtdAfericoesUltimaSemana(@NotNull final Integer codComponente,
                                                                   @NotNull final List<Long> codUnidades) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        final java.sql.Date dataInicial = new java.sql.Date(calendar.getTimeInMillis());
        final java.sql.Date dataFinal = new java.sql.Date(System.currentTimeMillis());
        try {
            return DashboardPneuComponentsCreator.createQtdAfericoesUltimaSemana(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getQtAfericoesByTipoByData(dataInicial, dataFinal, codUnidades));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a quantidade de aferições realizadas por data, agrupadas por tipo. \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s \n" +
                    "unidades: %s", dataInicial.toString(), dataFinal, codUnidades.toString()), e);
            throw new RuntimeException(e);
        }
    }

    public VerticalBarChartComponent getServicosEmAbertoByTipo(@NotNull final Integer codComponente,
                                                               @NotNull final List<Long> codUnidades) {
        try {
            return DashboardPneuComponentsCreator.createServicosEmAbertoByTipo(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getServicosEmAbertoByTipo(codUnidades));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar os serivços em aberto agrupados por tipo. \n" +
                    "unidades:" + codUnidades.toString(), e);
            throw new RuntimeException(e);
        }
    }
}