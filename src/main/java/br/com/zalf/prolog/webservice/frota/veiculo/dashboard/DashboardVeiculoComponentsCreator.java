package br.com.zalf.prolog.webservice.frota.veiculo.dashboard;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardVeiculoComponentsCreator {

    private DashboardVeiculoComponentsCreator() {
        throw new IllegalStateException(DashboardVeiculoComponentsCreator.class.getSimpleName() + " cannot be instatiated!");
    }

    @NotNull
    static QuantidadeItemComponent createQtdVeiculosAtivosComPneuAplicado(@NotNull final ComponentDataHolder component,
                                                                          final int qtdVeiculosAtivosComPneuAplicado) {
        return QuantidadeItemComponent.createDefault(
                component,
                String.valueOf(qtdVeiculosAtivosComPneuAplicado),
                "veículos ativos");
    }
}