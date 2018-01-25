package br.com.zalf.prolog.webservice.frota.veiculo.dashboard;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DashboardVeiculoComponentsCreator {

    private DashboardVeiculoComponentsCreator() {
        throw new IllegalStateException(DashboardVeiculoComponentsCreator.class.getSimpleName() + " cannot be instatiated!");
    }

    @NotNull
    static QuantidadeItemComponent createQtdVeiculosAtivosComPneuAplicado(@NotNull final ComponentDataHolder component,
                                                                          final int qtdVeiculosAtivosComPneuAplicado) {
        return new QuantidadeItemComponent.Builder()
                .withTitulo("Quantide de veículos ativos")
                .withSubtitulo("Apenas veículos que tenham pneus aplicados")
                .withDescricao("Mostra a quantidade de veículos ativos que possuam ao menos algum pneu aplicado")
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdItens(String.valueOf(qtdVeiculosAtivosComPneuAplicado))
                .withBackgroundColor(Color.WHITE)
                .withUrlIcone("TODO: URL DO ÍCONE")
                .build();
    }
}