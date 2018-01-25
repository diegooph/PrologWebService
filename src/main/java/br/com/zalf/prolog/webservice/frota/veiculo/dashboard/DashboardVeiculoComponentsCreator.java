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
                .withTitulo(component.tituloComponente)
                .withSubtitulo(component.subtituloComponente)
                .withDescricao(component.descricaoComponente)
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withQtdItens(String.valueOf(qtdVeiculosAtivosComPneuAplicado))
                .withBackgroundColor(Color.fromHex(component.corBackgroundHex))
                .withUrlIcone(component.urlIcone)
                .build();
    }
}