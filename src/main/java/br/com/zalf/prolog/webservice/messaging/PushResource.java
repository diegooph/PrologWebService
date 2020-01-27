package br.com.zalf.prolog.webservice.messaging;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.messaging._model.PushColaboradorCadastro;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("push")
@Secured
@DebugLog
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PushResource {
    @NotNull
    private final PushService service = new PushService();

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    public Response salvarTokenPushColaborador(@Required final PushColaboradorCadastro pushColaborador) {
        service.salvarTokenPushColaborador(pushColaborador);
        return Response.ok("Token salvo com sucesso!");
    }
}
