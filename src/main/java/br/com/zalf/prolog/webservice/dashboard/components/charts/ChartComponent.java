package br.com.zalf.prolog.webservice.dashboard.components.charts;

import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 17/02/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class ChartComponent extends DashboardComponent {

    public ChartComponent(@NotNull Integer codigo,
                          @NotNull IdentificadorTipoComponente identificadorTipo,
                          @NotNull String titulo,
                          @Nullable String subtitulo,
                          @NotNull String descricao,
                          @NotNull String urlEndpointDados,
                          @NotNull Integer codTipoComponente,
                          int qtdBlocosHorizontais,
                          int qtdBlocosVerticais,
                          int ordemExibicao) {
        super(codigo, identificadorTipo, titulo, subtitulo, descricao, urlEndpointDados, codTipoComponente,
                qtdBlocosHorizontais, qtdBlocosVerticais, ordemExibicao);
    }
}