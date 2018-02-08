package br.com.zalf.prolog.webservice.seguranca.relato.dashboard;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2/8/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardRelatoComponentsCreator {

    private DashboardRelatoComponentsCreator() {
        throw new IllegalStateException(DashboardRelatoComponentsCreator.class.getSimpleName() + " cannot be instatiated!");
    }


    @NotNull
    static QuantidadeItemComponent createQtdRelatosRealizadosHoje(@NotNull final ComponentDataHolder component,
                                                                  final int qtdRelatosRealizadosHoje) {
        return QuantidadeItemComponent.createDefault(
                component,
                String.valueOf(qtdRelatosRealizadosHoje),
                "relatos realizados hoje");
    }
}