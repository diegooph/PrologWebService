package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.table.TableComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.relatorios.OrdemServicoRelatorioDao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 21/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardChecklistOsService {
    private static final String TAG = DashboardChecklistOsService.class.getSimpleName();
    /**
     * Quantidade de placas que o relatório irá retornar.
     */
    private static final int PLACAS_MAIOR_QTD_ITENS_OS_ABERTOS = 15;
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
            throw exceptionHandler.map(throwable, "Erro ao burcar a quantidade de itens de O.S.");
        }
    }

    @NotNull
    TableComponent getPlacasMaiorQtdItensOsAbertos(
            @NotNull final Integer codComponente,
            @NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return DashboardChecklistOsComponentsCreator.createPlacasMaiorQtdItensOsAbertos(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getPlacasMaiorQtdItensOsAbertos(codUnidades, PLACAS_MAIOR_QTD_ITENS_OS_ABERTOS));
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar as placas com maior quantidade de itens de O.S. abertos para as " +
                            "unidades %s", codUnidades.toString()), throwable);
            throw exceptionHandler.map(throwable, "Erro ao buscar placas com maior quantidade de " +
                    "itens de O.S. em aberto");
        }
    }

    @NotNull
    TableComponent getPlacasBloqueadas(@NotNull final Integer codComponente, @NotNull final List<Long> codUnidades)
            throws ProLogException {
        try {
            return DashboardChecklistOsComponentsCreator.createPlacasBloqueadas(
                    dashDao.getComponenteByCodigo(codComponente),
                    relatorioDao.getPlacasBloqueadas(
                            codUnidades));
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar as placas bloqueadas" +
                    "unidades %s", codUnidades.toString()), throwable);
            throw exceptionHandler.map(throwable, "Erro ao buscar as placas bloqueadas");
        }
    }
}