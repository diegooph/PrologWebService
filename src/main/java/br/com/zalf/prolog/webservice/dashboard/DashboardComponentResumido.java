package br.com.zalf.prolog.webservice.dashboard;

import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import lombok.Builder;
import lombok.Data;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@Builder(toBuilder = true, setterPrefix = "with")
public final class DashboardComponentResumido {
    private final Integer codigoComponente;
    private final String titulo;
    private final String subtitulo;
    private final String descricao;
    private final String urlEndpointDados;
    private final int codAgrupamento;
    private final String nomeAgrupamento;
    private final int codPilarProLog;
    private final int qtdBlocosHorizontais;
    private final int qtdBlocosVerticais;
    private final int ordem;
    private final IdentificadorTipoComponente identificadorTipo;
}