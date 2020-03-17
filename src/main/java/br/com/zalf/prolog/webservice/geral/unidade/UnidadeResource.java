package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

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
public final class UnidadeResource {
    @NotNull
    private final UnidadeService service = new UnidadeService();

    @PUT
    @Secured(permissions = {Pilares.Geral.Empresa.EDITAR})
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateUnidade(@Valid final UnidadeEdicao unidade) {
        if (service.updateUnidade(unidade)) {
            return Response.ok("Unidade atualizada com sucesso.");
        } else {
            return Response.error("Erro ao atualizar unidade.");

        }
    }

    @GET
    @Secured(permissions = {Pilares.Geral.Empresa.VISUALIZAR})
    @Path("/{codUnidade}")
    public UnidadeVisualizacao getUnidadeByCodigo(@PathParam("codUnidade") final Long codUnidade) throws Throwable {
        return service.getUnidadeByCodigo(codUnidade);
    }

    @GET
    @Secured(permissions = {Pilares.Geral.Empresa.VISUALIZAR})
    public List<UnidadeVisualizacao> getUnidadesListagem(@QueryParam("codEmpresa") final Long codEmpresa,
                                                         @QueryParam("codRegional") final Long codRegional) throws Throwable {
        return service.getUnidadesListagem(codEmpresa, codRegional);
    }
}
