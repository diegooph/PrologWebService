package br.com.zalf.prolog.webservice.frota.checklist.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.components.charts.line.HorizontalLineChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.table.TableComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.checklist.relatorios.ChecklistRelatorioDao;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DashboardChecklistService {
    private static final String TAG = DashboardChecklistService.class.getSimpleName();
    @NotNull
    private final DashboardDao dashDao = Injection.provideDashboardDao();
    @NotNull
    private final ChecklistRelatorioDao relatorioDao = Injection.provideChecklistRelatorioDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

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
                    "Erro ao buscar a quantidade de checklists realizados para as unidades: " + codUnidades,
                    throwable);
            throw exceptionHandler.map(throwable, "Erro ao buscar a quantidade de checklists realizados");
        }
    }

    @NotNull
    TableComponent getChecksRealizadosAbaixo130(
            @NotNull final Integer codComponente,
            @NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return DashboardChecklistComponentsCreator.createChecksRealizadosAbaixo130(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getQtdChecksRealizadosAbaixoTempoEspecifico(
                            codUnidades,
                            TimeUnit.SECONDS.toMillis(90),
                            30));
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar os checklists realizados em menos de 1:30 para as " +
                    "unidades %s", codUnidades.toString()), throwable);
            throw exceptionHandler.map(throwable, "Erro ao buscar checklists realizados em menos de 1:30");
        }
    }
}