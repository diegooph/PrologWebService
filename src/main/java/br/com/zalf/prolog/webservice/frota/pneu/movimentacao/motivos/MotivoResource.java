package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-03-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Path("/motivos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MotivoResource {

    @NotNull
    private final MotivoService motivoService = new MotivoService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Long insert(@Valid final MotivoInsercao motivo) {
        return motivoService.insert(motivo);
    }

    @GET
    @Path("/{codMotivo}")
    public MotivoVisualizacaoListagem getMotivoByCodigo(@NotNull @PathParam("codMotivo") final Long codMotivo) {
        return motivoService.getMotivoByCodigo(codMotivo);
    }

    @GET
    public List<MotivoVisualizacaoListagem> getMotivosListagem(@NotNull @QueryParam("codEmpresa") final Long codEmpresa) {
        return motivoService.getMotivosListagem(codEmpresa);
    }

}
