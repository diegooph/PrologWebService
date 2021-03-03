package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.justificativa;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/v2/controle-jornada/justificativas-ajustes")
@ConsoleDebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class JustificativaAjusteResource {
    @NotNull
    private final JustificativaAjusteService service = new JustificativaAjusteService();

    @GET
    @Secured
    @UsedBy(platforms = Platform.WEBSITE)
    public List<JustificativaAjuste> getJustificativasAjuste(@QueryParam("codEmpresa") @Required final Long codEmpresa,
                                                             @QueryParam("ativas") @Optional final Boolean ativas)
            throws ProLogException {
        return service.getJustificativasAjuste(codEmpresa, ativas);
    }

    @POST
    @Secured
    @UsedBy(platforms = Platform.WEBSITE)
    public AbstractResponse insertJustificativaAjuste(
            @HeaderParam("Authorization") final String userToken,
            @NotNull final JustificativaAjuste justificativaAjuste) throws ProLogException {
        return service.insertJustificativaAjuste(userToken, justificativaAjuste);
    }
}