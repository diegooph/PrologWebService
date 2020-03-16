package br.com.zalf.prolog.webservice.gente.unidade;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.gente.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.gente.unidade._model.UnidadeVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@DebugLog
@Path("/unidades")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UnidadeResource {

    private final UnidadeService service = new UnidadeService();

    @PUT
    @Secured
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateUnidade(@Valid final UnidadeEdicao unidade) {
        if (service.updateUnidade(unidade)) {
            return Response.ok("Unidade atualizada com sucesso.");
        } else {
            return Response.error("Erro ao atualizar unidade.");

        }
    }

    @GET
    @Secured
    @Path("/getByCodUnidade/{codUnidade}")
    public UnidadeVisualizacao getUnidadeByCodUnidade(@PathParam("codUnidade") final Long codUnidade) throws Throwable {
        return service.getUnidadeByCodUnidade(codUnidade);
    }

    @GET
    @Secured
    @Path("/getAllByCodEmpresaAndCodRegional")
    public List<UnidadeVisualizacao> getAllUnidadeByCodEmpresaAndCodRegional(@QueryParam("codEmpresa") final Long codEmpresa,
                                                                             @QueryParam("codRegional") final Long codRegional) throws Throwable {
        return service.getAllUnidadeByCodEmpresaAndCodRegional(codEmpresa, codRegional);
    }

}
