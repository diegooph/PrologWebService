package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoInsercao;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    public Long insert(@Valid final MotivoOrigemDestinoInsercao motivoOrigemDestinoInsercao,
                         @HeaderParam("Authorization") @NotNull final String tokenAutenticacao) {
        return motivoOrigemDestinoService.insert(motivoOrigemDestinoInsercao, tokenAutenticacao);
    }

}
