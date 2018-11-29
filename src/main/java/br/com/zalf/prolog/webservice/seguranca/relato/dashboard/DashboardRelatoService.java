package br.com.zalf.prolog.webservice.seguranca.relato.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.seguranca.relato.relatorio.RelatoRelatorioDao;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 2/8/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardRelatoService {
    private static final String TAG = DashboardRelatoService.class.getSimpleName();
    private final DashboardDao dashDao = Injection.provideDashboardDao();
    private final RelatoRelatorioDao relatorioDao = Injection.provideRelatoRelatorioDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    QuantidadeItemComponent getQtdRelatosRealizadosHoje(@NotNull final Integer codComponente,
                                                        @NotNull final List<Long> codUnidades) {
        try {
            return DashboardRelatoComponentsCreator.createQtdRelatosRealizadosHoje(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getQtdRelatosRealizadosHoje(codUnidades));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a quantidade de relatos realizados hoje. \n" +
                    "Unidades: %s", codUnidades), e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    PieChartComponent getQtdRelatosPendentesByStatusInterval30days(
            @NotNull final Integer codComponente,
            @NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return DashboardRelatoComponentsCreator.createQtdRelatosPendentesByStatusInterval30days(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getQtdRelatosPendentesByStatus(codUnidades, 30));
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar a quantidade de relatos pendentes para as unidades %s",
                    codUnidades.toString()), throwable);
            throw exceptionHandler.map(throwable, "Erro ao burcar a quantidade de relatos pendentes.");
        }
    }
}