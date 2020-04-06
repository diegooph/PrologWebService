package br.com.zalf.prolog.webservice.frota.socorrorota.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.socorrorota.relatorio.SocorroRotaRelatorioDao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-03-31
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class DashboardSocorroRotaService {

    private static final String TAG = DashboardSocorroRotaService.class.getSimpleName();

    @NotNull
    private final DashboardDao dashDao = Injection.provideDashboardDao();

    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    private final SocorroRotaRelatorioDao relatorioDao = Injection.provideSocorroRotaRelatorioDao();

    public @NotNull PieChartComponent getQtdSocorroRotaPorStatus(@NotNull final Integer codComponente,
                                                                 @NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return DashboardSocorroRotaComponentsCreator.createSocorrosPorStatus(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getSocorrosPorStatus(codUnidades));
        } catch (final Throwable throwable) {
            Log.e(TAG,
                    "Erro ao buscar a quantidade de socorros por status para as unidades: " + codUnidades,
                    throwable);
            throw exceptionHandler.map(throwable, "Erro ao buscar a quantidade de socorros por status.");
        }
    }

}
