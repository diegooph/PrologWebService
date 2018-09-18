package br.com.zalf.prolog.webservice.frota.checklist.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.components.charts.line.HorizontalLineChartComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.relatorios.ChecklistRelatorioDao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DashboardChecklistService {
    private static final String TAG = DashboardChecklistService.class.getSimpleName();
    private final DashboardDao dashDao = Injection.provideDashboardDao();
    private final ChecklistRelatorioDao relatorioDao = Injection.provideChecklistRelatorioDao();

    @NotNull
    public HorizontalLineChartComponent getQtdChecklistsUltimos30DiasByTipo(
            @NotNull final Integer codComponente,
            @NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return DashboardChecklistComponentsCreator.createQtdChecksUltimos30DiasByTipo(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getQtdChecklistsRealizadosByTipo(codUnidades, 30));
        } catch (final Throwable throwable) {
            Log.e(TAG,
                    "Erro ao buscar a quantidade de pneus por status para as unidades: " + codUnidades,
                    throwable);
            throw new RuntimeException(throwable);
        }
    }
}