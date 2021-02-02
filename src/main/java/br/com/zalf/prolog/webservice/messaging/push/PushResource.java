package br.com.zalf.prolog.webservice.messaging.push;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.messaging.push._model.PushColaboradorCadastro;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("push")
@Secured
@ConsoleDebugLog
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PushResource {
    @NotNull
    private final PushService service = new PushService();

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    public Response salvarTokenPushColaborador(@HeaderParam("Authorization") @Required final String userToken,
                                               @Required final PushColaboradorCadastro pushColaborador) {
        service.salvarTokenPushColaborador(userToken, pushColaborador);
        return Response.ok("Token salvo com sucesso!");
    }
}
