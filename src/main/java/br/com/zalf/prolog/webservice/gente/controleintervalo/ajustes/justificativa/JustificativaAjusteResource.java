package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/controle-jornada/justificativas-ajustes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class JustificativaAjusteResource {

    @NotNull
    final JustificativaAjusteService service = new JustificativaAjusteService();

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured()
    @Path("/")
    public List<JustificativaAjuste> getJustificativasAjuste(@QueryParam("codEmpresa") @Required final Long codEmpresa,
                                                             @QueryParam("ativos") @Optional final Boolean ativos)
            throws ProLogException {
        return service.getJustificativasAjuste(codEmpresa, ativos);
    }

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Secured()
    @Path("/")
    public AbstractResponse adicionarMarcacaoAjuste(
            @HeaderParam("Authorization") String userToken,
            @NotNull final JustificativaAjuste justificativaAjuste) throws ProLogException {
        return service.insertJustificativaAjuste(userToken, justificativaAjuste);
    }
}