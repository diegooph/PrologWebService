package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaOrigemDestinoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaOrigemDestinoListagemMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaOrigemDestinoVisualizacao;
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
public class MotivoRetiradaOrigemDestinoResource {

    @NotNull
    private final MotivoRetiradaOrigemDestinoService motivoRetiradaOrigemDestinoService = new MotivoRetiradaOrigemDestinoService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<Long> insert(@NotNull @Valid @Required final List<MotivoRetiradaOrigemDestinoInsercao> unidades,
                             @NotNull @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoRetiradaOrigemDestinoService.insert(unidades, tokenAutenticacao);
    }

    @GET
    @Path("/{codMotivoOrigemDestino}")
    public MotivoRetiradaOrigemDestinoVisualizacao getMotivoOrigemDestino(@NotNull @PathParam("codMotivoOrigemDestino") final Long codMotivoOrigemDestino,
                                                                          @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoRetiradaOrigemDestinoService.getMotivoOrigemDestino(codMotivoOrigemDestino, tokenAutenticacao);
    }

    //todo: refatorar esse metodo para usar a func FUNC_MOTIVO_RETIRADA_ORIGEM_DESTINO_LISTAGEM E MUDA OS RECEBIMENTOS E OBJETO QUE RECEBE
    @GET
    public List<MotivoRetiradaOrigemDestinoVisualizacao> getMotivosOrigemDestino(@HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoRetiradaOrigemDestinoService.getMotivosOrigemDestino(tokenAutenticacao);
    }

    @GET
    @UsedBy(platforms = {Platform.ANDROID})
    @Path("/listagemResumida")
    public MotivoRetiradaOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(@QueryParam("origem") @NotNull final OrigemDestinoEnum origem,
                                                                                             @QueryParam("destino") @NotNull final OrigemDestinoEnum destino,
                                                                                             @QueryParam("codUnidade") @NotNull final Long codUnidade) {
        return motivoRetiradaOrigemDestinoService.getMotivosByOrigemAndDestinoAndUnidade(origem, destino, codUnidade);
    }

}
