package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Path("/motivos/motivoOrigemDestino")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MotivoOrigemDestinoResource {

    @NotNull
    private final MotivoOrigemDestinoService motivoOrigemDestinoService = new MotivoOrigemDestinoService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Long insert(@NotNull @Valid final MotivoOrigemDestinoInsercao motivoOrigemDestinoInsercao,
                       @NotNull @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoOrigemDestinoService.insert(motivoOrigemDestinoInsercao, tokenAutenticacao);
    }

    @GET
    @Path("/{codMotivoOrigemDestino}")
    public MotivoOrigemDestinoVisualizacaoListagem getMotivoOrigemDestino(@NotNull @PathParam("codMotivoOrigemDestino") final Long codMotivoOrigemDestino,
                                                                          @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoOrigemDestinoService.getMotivoOrigemDestino(codMotivoOrigemDestino, tokenAutenticacao);
    }

    @GET
    public List<MotivoOrigemDestinoVisualizacaoListagem> getMotivosOrigemDestino(@NotNull @QueryParam("codEmpersa") final Long codEmpresa,
                                                                                 @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoOrigemDestinoService.getMotivosOrigemDestino(codEmpresa, tokenAutenticacao);
    }

}
