package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagem;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

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
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class UnidadeResource implements UnidadeResourceContract {
    @NotNull
    private final UnidadeService service = new UnidadeService();

    @PUT
    @Secured(permissions = {Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Override
    public Response updateUnidade(final UnidadeEdicao unidade) {
        service.updateUnidade(unidade);
        return Response.ok("Unidade atualizada com sucesso.");
    }

    @GET
    @Secured(permissions = {Pilares.Geral.Empresa.VISUALIZAR_ESTRUTURA, Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Path("/{codUnidade}")
    @Override
    public UnidadeVisualizacaoListagem getUnidadeByCodigo(@PathParam("codUnidade") final Long codUnidade) {
        return service.getUnidadeByCodigo(codUnidade);
    }

    @GET
    @Secured(permissions = {Pilares.Geral.Empresa.VISUALIZAR_ESTRUTURA, Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Override
    public List<UnidadeVisualizacaoListagem> getUnidadesListagem(
            @QueryParam("codEmpresa") final Long codEmpresa,
            @QueryParam("codigosRegionais") final List<Long> codigosRegionais) {
        return service.getUnidadesListagem(codEmpresa, codigosRegionais);
    }
}
