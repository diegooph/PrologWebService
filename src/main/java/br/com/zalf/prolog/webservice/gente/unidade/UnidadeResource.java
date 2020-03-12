package br.com.zalf.prolog.webservice.gente.unidade;

import br.com.zalf.prolog.webservice.gente.unidade._model.UnidadeVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;

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
