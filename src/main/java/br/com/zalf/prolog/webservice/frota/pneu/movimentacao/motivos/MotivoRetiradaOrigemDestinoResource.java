package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Secured
@Path("/motivos/motivoOrigemDestino")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MotivoRetiradaOrigemDestinoResource {

    @NotNull
    private final MotivoRetiradaOrigemDestinoService motivoRetiradaOrigemDestinoService = new MotivoRetiradaOrigemDestinoService();

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @UsedBy(platforms = {Platform.WEBSITE})
    public List<Long> insert(@NotNull @Valid @Required final List<MotivoRetiradaOrigemDestinoInsercao> unidades) {
        return motivoRetiradaOrigemDestinoService.insert(unidades, colaboradorAutenticadoProvider.get().getCodigo());
    }

    @GET
    @Path("/{codMotivoOrigemDestino}")
    @UsedBy(platforms = {Platform.WEBSITE})
    public MotivoRetiradaOrigemDestinoVisualizacao getMotivoOrigemDestino(@NotNull @PathParam("codMotivoOrigemDestino") final Long codMotivoOrigemDestino,
                                                                          @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoRetiradaOrigemDestinoService.getMotivoOrigemDestino(codMotivoOrigemDestino, tokenAutenticacao);
    }

    @GET
    @UsedBy(platforms = {Platform.WEBSITE})
    public List<MotivoRetiradaOrigemDestinoListagem> getMotivosOrigemDestino(@HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoRetiradaOrigemDestinoService.getMotivosOrigemDestino(tokenAutenticacao);
    }

    @GET
    @UsedBy(platforms = {Platform.ANDROID})
    @Path("/listagemResumida")
    public MotivoRetiradaOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(@QueryParam("origemMovimento") @NotNull final OrigemDestinoEnum origemMovimento,
                                                                                             @QueryParam("destinoMovimento") @NotNull final OrigemDestinoEnum destinoMovimento,
                                                                                             @QueryParam("codUnidade") @NotNull final Long codUnidade) {
        return motivoRetiradaOrigemDestinoService.getMotivosByOrigemAndDestinoAndUnidade(origemMovimento, destinoMovimento, codUnidade);
    }

    @GET
    @Path("/unidade/{codUnidade}")
    @UsedBy(platforms = {Platform.ANDROID})
    public List<OrigemDestinoListagem> getRotasExistentesByUnidade(@PathParam("codUnidade") @NotNull final Long codUnidade) {
        return motivoRetiradaOrigemDestinoService.getRotasExistentesByUnidade(codUnidade);
    }

}
