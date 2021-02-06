package br.com.zalf.prolog.webservice.frota.veiculo.historico;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.HistoricoEdicaoVeiculo;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-09-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@ConsoleDebugLog
@Path("/veiculos/historicos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class HistoricoEdicaoVeiculoResource {
    @NotNull
    private final HistoricoEdicaoVeiculoService service = new HistoricoEdicaoVeiculoService();

    @GET
    @Path("/")
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions
            = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    public List<HistoricoEdicaoVeiculo> getHistoricoEdicaoVeiculo(
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codVeiculo") @Required final Long codVeiculo) {
        return service.getHistoricoEdicaoVeiculo(codEmpresa, codVeiculo);
    }
}
