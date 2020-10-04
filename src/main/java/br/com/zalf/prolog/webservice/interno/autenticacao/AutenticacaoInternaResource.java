package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUserAuthentication;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 02/03/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/interno/autenticacao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class AutenticacaoInternaResource {
    @NotNull
    private final AutenticacaoInternaService service = new AutenticacaoInternaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/login")
    public PrologInternalUser login(@NotNull final PrologInternalUserAuthentication internalUser) {
        return service.login(internalUser);
    }
}
