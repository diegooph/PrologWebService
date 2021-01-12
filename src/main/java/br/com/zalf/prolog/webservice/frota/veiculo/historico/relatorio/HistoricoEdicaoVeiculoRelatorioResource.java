package br.com.zalf.prolog.webservice.frota.veiculo.historico.relatorio;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created on 2020-09-29
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@ConsoleDebugLog
@Path("/veiculos/historicos/relatorios")
public final class HistoricoEdicaoVeiculoRelatorioResource {
    @NotNull
    private final HistoricoEdicaoVeiculoRelatorioService service = new HistoricoEdicaoVeiculoRelatorioService();

    @GET
    @Path("/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions
            = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    public StreamingOutput getHistoricoEdicaoVeiculoCsv(
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codVeiculo") @Required final Long codVeiculo) {
        return outputStream -> service.getHistoricoEdicaoVeiculoCsv(outputStream, codEmpresa, codVeiculo);
    }
}
