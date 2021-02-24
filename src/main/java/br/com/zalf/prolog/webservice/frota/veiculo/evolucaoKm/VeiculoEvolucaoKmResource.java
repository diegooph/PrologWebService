package br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created on 2020-10-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/v2/veiculos/evolucao-km")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class VeiculoEvolucaoKmResource {
    @NotNull
    private final VeiculoEvolucaoKmService service = new VeiculoEvolucaoKmService();

    @GET
    @Path("/busca")
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions
            = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    public Response getVeiculoEvolucaoKm(
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codVeiculo") @Required final Long codVeiculo,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws ProLogException {
        return service.getVeiculoEvolucaoKm(codEmpresa, codVeiculo, dataInicial, dataFinal);
    }
}