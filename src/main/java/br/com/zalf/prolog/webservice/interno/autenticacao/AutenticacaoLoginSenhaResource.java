package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 02/03/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/autenticacao")
@Consumes({MediaType.MULTIPART_FORM_DATA})
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class AutenticacaoLoginSenhaResource {
    @NotNull
    private final AutenticacaoLoginSenhaService service = new AutenticacaoLoginSenhaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/create-login")
    public void createUsernamePassword(
            @HeaderParam("username") @Required final String username,
            @HeaderParam("password") @Required final String password) throws ProLogException {
        try {
            service.createUsernamePassword(username, password);
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
