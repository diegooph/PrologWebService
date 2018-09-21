package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios.OrdemServicoRelatorioDao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 21/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardChecklistOsService {
    private static final String TAG = DashboardChecklistOsService.class.getSimpleName();
    @NotNull
    private final DashboardDao dashDao = Injection.provideDashboardDao();
    @NotNull
    private final OrdemServicoRelatorioDao relatorioDao = Injection.provideRelatoriosOrdemServicoDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    PieChartComponent getQtdItensOsAbertosByPrioridade(
            @NotNull final Integer codComponente,
            @NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return DashboardChecklistOsComponentsCreator.createQtdItensOsByPrioridade(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getQtdItensOsByPrioridade(codUnidades, ItemOrdemServico.Status.PENDENTE));
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar a quantidade de itens de O.S. para as unidades %s",
                    codUnidades.toString()), throwable);
            throw exceptionHandler.map(throwable, "Erro ao burcar a quantidade de itens de O.S,");
        }
    }
}