package br.com.zalf.prolog.webservice.frota.veiculo.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.frota.veiculo.relatorio.RelatorioVeiculoDao;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class DashboardVeiculoService {
    private static final String TAG = DashboardVeiculoService.class.getSimpleName();
    private final DashboardDao dashDao = Injection.provideDashboardDao();
    private final RelatorioVeiculoDao relatorioDao = Injection.provideRelatorioVeiculoDao();

    public QuantidadeItemComponent getQtdVeiculosAtivosComPneuAplicado(@NotNull final List<Long> codUnidades,
                                                                       @NotNull final Integer codComponente) {
        try {
            return DashboardVeiculoComponentsCreator.createQtdVeiculosAtivosComPneuAplicado(
                    dashDao.getComponentByCodigo(codComponente),
                    relatorioDao.getQtdVeiculosAtivosComPneuAplicado(codUnidades));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a qtd de veículos ativos com pneus aplicados. \n" +
                    "unidades: %s", codUnidades.toString()), e);
            throw new RuntimeException(e);
        }
    }
}